package internal

import (
	"context"
	"time"

	"firebase.google.com/go/v4/messaging"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Notification token schema
type NotificationToken struct {
	ID        primitive.ObjectID `bson:"_id" json:"id"`
	UserId    string             `bson:"userId" json:"userId"`
	DeviceId  string             `bson:"deviceId" json:"deviceId"`
	Timestamp time.Time          `bson:"timestamp" json:"timestamp"`
}

func SendNotification(
	fcmClient *messaging.Client,
	ctx context.Context,
	tokens []string,
	userId, message string,
) error {
	//Send to One Token
	_, err := fcmClient.Send(ctx, &messaging.Message{
		Token: tokens[0],
		Data: map[string]string{
			message: message,
		},
	})
	if err != nil {
		return err
	}

	//Send to Multiple Tokens
	_, err = fcmClient.SendEachForMulticast(ctx, &messaging.MulticastMessage{
		Data: map[string]string{
			message: message,
		},
		Tokens: tokens,
	})
	return err
}
