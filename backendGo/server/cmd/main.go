package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"server/auth"
	"server/db"
	"server/internal"
	"time"

	"github.com/go-chi/chi/v5"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

const DB_NAME = "chat_db"
const NOTIFICATION_TOKENS_COLL_NAME = "notificationTokens"
const MESSAGE_COLLECTION = "messages"
const ROOMS_COLLECTION = "rooms"

func main() {

	ctx := context.Background()
	//Database Init
	mc, err := db.Init(ctx, "mongodb+srv://vlas:vlasnako@cluster0.lnckptx.mongodb.net/?retryWrites=true&w=majority")
	if err != nil {
		fmt.Printf("mongo error: %v", err)
		os.Exit(1)
	}
	defer mc.Disconnect(ctx)
	//Create a mongo database with the db name
	mongoDB := mc.Database(DB_NAME)

	tokenCollection := mongoDB.Collection(NOTIFICATION_TOKENS_COLL_NAME)
	//messageCollection := mongoDB.Collection(MESSAGE_COLLECTION)
	roomsCollection := mongoDB.Collection(ROOMS_COLLECTION)
	messagesCollection := mongoDB.Collection(MESSAGE_COLLECTION)

	const hours_in_a_week = 24 * 7
	//create the index model with the field "timestamp"
	index := mongo.IndexModel{
		Keys: bson.M{"timestamp": 1},
		Options: options.Index().SetExpireAfterSeconds(
			int32((time.Hour * 3 * hours_in_a_week).Seconds()),
		),
	}
	//Create the index on the token collection
	_, err = tokenCollection.Indexes().CreateOne(ctx, index)
	if err != nil {
		fmt.Printf("mongo index error: %v", err)
		os.Exit(1)
	}

	// Init the app
	err = internal.FirebaseInit(ctx)
	if err != nil {
		log.Fatalf("error connecting to firebase: %v", err)
		os.Exit(1)
	}

	// Get the FCM client
	fcmClient, err := internal.CreateFCMClient(ctx)
	if err != nil {
		fmt.Printf("error connecting to fcm client: %v", err)
		os.Exit(1)
	}

	r := chi.NewRouter()
	// tokens endpoint
	r.Post("/send-notifications", func(w http.ResponseWriter, r *http.Request) {
		var message internal.NotificationToken
		err := json.NewDecoder(r.Body).Decode(&message)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}
		tokens, err := db.GetNotificationTokens(tokenCollection, ctx, message.UserId)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		// **************************** SAMPLE MESSAGE *******************************************
		err = internal.SendNotification(fcmClient, ctx, tokens, message.UserId, "message.Message")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
	r.Post("/tokens", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("endpoint /tokens")
		// Decode the token sent from the user into the token variable
		// You should have some input validation here
		var token internal.NotificationToken
		//token.ID = primitive.NewObjectID()
		err := json.NewDecoder(r.Body).Decode(&token)
		if err != nil {
			fmt.Printf("Error decoding the token: %v", err)
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		// Insert the token into the database
		err = db.InsertToken(tokenCollection, token, ctx)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error inserting the token: %v", err)
			return
		}

	})

	r.Mount("/users", UserRoutes(ctx, tokenCollection))
	r.Mount("/rooms", RoomRoutes(ctx, roomsCollection))
	r.Mount("/messages", MessageRoutes(ctx, messagesCollection))
	// Start the server
	fmt.Println("Server Starting on Port 3000")
	log.Fatal(http.ListenAndServe(":3000", r))
}
func MessageRoutes(
	ctx context.Context, messageCollection *mongo.Collection,
) chi.Router {
	r := chi.NewRouter()
	r.Post("/send-message", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Endpoint: Send message")
		var message internal.Message
		err := json.NewDecoder(r.Body).Decode(&message)

		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error decoding the message: %v", err)
			return
		}
		err = db.InsertMessage(messageCollection, ctx, message)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error inserting the message: %v", err)
			return
		}
	})
	return r
}
func RoomRoutes(
	ctx context.Context, roomsCollection *mongo.Collection,
) chi.Router {
	r := chi.NewRouter()
	r.Post("/create-room", func(w http.ResponseWriter, r *http.Request) {
		var room internal.Room
		err := json.NewDecoder(r.Body).Decode(&room)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error creating the room: %v", err)
			return
		}

		err = db.InsertRoom(roomsCollection, ctx, room)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error inserting the room: %v", err)
			return
		}
	})

	r.Post("/get-user-rooms", func(w http.ResponseWriter, r *http.Request) {
		var uid string
		err := json.NewDecoder(r.Body).Decode(&uid)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Endpoint: Error getting user rooms: %v", err)
			return
		}
		rooms, err := db.GetSpecificUserRooms(roomsCollection, ctx, uid)
		w.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(w).Encode(rooms); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error encoding rooms: %v\n", err)
			return
		}

	})
	r.Post("/join-room", func(w http.ResponseWriter, r *http.Request) {
		var request map[string]string
		err := json.NewDecoder(r.Body).Decode(&request)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Endpoint: Error adding user to room: %v", err)
			return
		}
		err = db.AddUserToRoom(roomsCollection, ctx, request["room_id"], request["uid"])
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error joining the room: %v", err)
			return
		}
	})

	r.Get("/get-all-rooms", func(w http.ResponseWriter, r *http.Request) {
		rooms, err := db.GetRooms(roomsCollection, ctx)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error getting rooms: %v\n", err)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(w).Encode(rooms); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Printf("Error encoding rooms: %v\n", err)
			return
		}
	})
	return r
}

func UserRoutes(ctx context.Context, tokenCollection *mongo.Collection) chi.Router {
	r := chi.NewRouter()

	r.Post("/create-user", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("worked")
		var u internal.User
		err := json.NewDecoder(r.Body).Decode(&u)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error creating the user: %v", err)
			return
		}

		err = internal.ValidateUser(&u)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error creating the user: %v", err)
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		userRecord, err := auth.CreateNewUser(
			ctx, internal.FirebaseApp,
			u.Email, u.PhoneNumber,
			u.Password,
			u.DisplayName,
			u.PhotoUrl,
		)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error creating the user: %v", err)
			return
		}
		w.WriteHeader(http.StatusOK)
		err = json.NewEncoder(w).Encode(userRecord)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			fmt.Printf("Error creating the user: %v", err)
			return
		}
	})

	return r

}
