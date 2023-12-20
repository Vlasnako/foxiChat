package db

import (
	"context"
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
