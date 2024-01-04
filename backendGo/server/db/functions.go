package db

import (
	"context"
	"fmt"
	"server/internal"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

// Insert a token
func InsertToken(
	collection *mongo.Collection,
	token internal.NotificationToken,
	ctx context.Context,
) error {
	// Check if the token already exists
	filter := bson.D{{Key: "deviceId", Value: token.DeviceId}}
	res := collection.FindOne(ctx, filter)

	if res.Err() != nil {
		if res.Err() == mongo.ErrNoDocuments {
			// If token does not exist insert it
			token.ID = primitive.NewObjectID()
			_, err := collection.InsertOne(ctx, token)
			return err
		}
		return res.Err()
	}

	// If token exists update the timestamp to now
	_, err := collection.UpdateOne(ctx, filter, bson.M{"$set": bson.M{"timestamp": time.Now().UTC()}})
	return err
}
func InsertMessage(
	collection *mongo.Collection,
	ctx context.Context,
	message internal.Message,
) error {
	message.ID = primitive.NewObjectID()

	_, err := collection.InsertOne(ctx, message)
	if err != nil {
		return err
	}

	return nil
}

// Create new room
func InsertRoom(
	collection *mongo.Collection,
	ctx context.Context,
	room internal.Room,
) error {
	// Check if the room already exists

	room.ID = primitive.NewObjectID()
	_, err := collection.InsertOne(ctx, room)
	if err != nil {
		return err
	}

	return nil
}
func AddUserToRoom(
	collection *mongo.Collection,
	ctx context.Context,
	roomId string,
	uid string,
) error {
	roomObjectID, err := primitive.ObjectIDFromHex(roomId)
	if err != nil {
		return err // Return the error if the conversion fails
	}

	// Use the ObjectID in the filter
	filter := bson.D{{Key: "_id", Value: roomObjectID}}
	_, err = collection.UpdateOne(ctx, filter, bson.M{"$push": bson.M{"users": uid}})
	fmt.Println("Joined room")
	return err
}
func GetRooms(
	coll *mongo.Collection,
	ctx context.Context,
) ([]internal.Room, error) {
	filter := bson.D{}
	tokenCursor, err := coll.Find(ctx, filter)
	if err != nil {
		fmt.Printf("error in getting all rooms: %v", err)
		return nil, err
	}

	rooms := make([]internal.Room, 0)
	for tokenCursor.Next(ctx) {
		var room internal.Room
		err = tokenCursor.Decode(&room)
		rooms = append(rooms, room)
	}

	if err != nil {
		fmt.Printf("error in getting all rooms: %v", err)
		return nil, err
	}
	return rooms, nil
}

func GetRoomById(
	coll *mongo.Collection,
	ctx context.Context,
	id string,
) (internal.Room, error) {
	roomObjectID, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		return internal.Room{}, err // Return the error if the conversion fails
	}
	filter := bson.D{{Key: "_id", Value: roomObjectID}}
	tokenCursor, err := coll.Find(ctx, filter)
	if err != nil {
		fmt.Printf("error in getting all rooms: %v", err)
		return internal.Room{}, err
	}

	for tokenCursor.Next(ctx) {
		var room internal.Room
		err = tokenCursor.Decode(&room)
		return room, err
	}

	if err != nil {
		fmt.Printf("error in getting room: %v", err)
		return internal.Room{}, err
	}
	return internal.Room{}, err
}

func GetSpecificUserRooms(
	coll *mongo.Collection,
	ctx context.Context,
	uid string,
) ([]internal.Room, error) {
	//filter only those rooms, whose user array contains given id
	filter := bson.D{{Key: "users", Value: bson.D{{Key: "$in", Value: []string{uid}}}}}
	tokenCursor, err := coll.Find(ctx, filter)

	if err != nil {
		fmt.Printf("error in getting user rooms: %v", err)
		return nil, err
	}

	rooms := make([]internal.Room, 0)
	for tokenCursor.Next(ctx) {
		var room internal.Room
		err = tokenCursor.Decode(&room)
		rooms = append(rooms, room)
	}
	if err != nil {
		fmt.Printf("error in getting user rooms: %v", err)
		return nil, err
	}
	return rooms, nil

}

func GetNotificationTokens(
	coll *mongo.Collection,
	ctx context.Context,
	userId string,
) ([]string, error) {
	filter := bson.D{{Key: "userId", Value: userId}}
	tokenCursor, err := coll.Find(ctx, filter)
	if err != nil {
		return nil, err
	}

	tokens := make([]string, 0)
	for tokenCursor.Next(ctx) {
		var token internal.NotificationToken
		err = tokenCursor.Decode(&token)
		tokens = append(tokens, token.DeviceId)
	}

	if err != nil {
		return nil, err
	}
	return tokens, nil
}

func GetNotificationTokensForUsers(
	tokenCollection *mongo.Collection,
	roomCollection *mongo.Collection,
	ctx context.Context,
	roomId string,
) ([]string, error) {
	room, err := GetRoomById(roomCollection, ctx, roomId)

	if err != nil {
		fmt.Printf("Error in getting room by id: %v", err)
		return nil, err
	}
	tokens := make([]string, 0)

	for i := 0; i < len(room.Uids); i++ {
		userTokens, err := GetNotificationTokens(tokenCollection, ctx, room.Uids[i])
		if err != nil {
			fmt.Printf("Error in getting room by id: %v", err)
			return nil, err
		}
		tokens = append(tokens, userTokens...)
	}
	return tokens, nil

}
