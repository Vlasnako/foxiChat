package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"server/db"
	"server/internal"
	"time"

	"github.com/go-chi/chi/v5"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

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
	mongoDB := mc.Database("notification_service")
	//Create a notification token collection
	tokenCollection := mongoDB.Collection("notificationTokens")

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

	r := chi.NewRouter()

	r.Post("/tokens", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("endpoint /tokens")
		// Decode the token sent from the user into the token variable
		// You should have some input validation here
		var token internal.NotificationToken
		//token.ID = primitive.NewObjectID()
		err := json.NewDecoder(r.Body).Decode(&token)
		if err != nil {
			fmt.Println(err)
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		// Insert the token into the database
		err = db.InsertToken(tokenCollection, token, ctx)
		if err != nil {
			// You should have better error handling here
			http.Error(w, err.Error(), http.StatusInternalServerError)
			fmt.Println(err)

			return
		}
		fmt.Println("no errors")
	})

	fcmClient, err := internal.FirebaseInit(ctx)
	if err != nil {
		fmt.Printf("error connecting to firebase: %v", err)
		os.Exit(1)
	}
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
		// ******************* SAMPLE MESSAGE *********************************************
		err = internal.SendNotification(fcmClient, ctx, tokens, message.UserId, "message.Message")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
	// Start the server
	fmt.Println("Server Starting on Port 3000")
	log.Fatal(http.ListenAndServe(":3000", r))
}
