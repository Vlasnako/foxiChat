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

// Room schema
type Room struct {
	ID        primitive.ObjectID `bson:"_id" json:"id"`
	Name      string             `bson:"name" json:"name"`
	Uids      []string           `bson:"users" json:"users"`
	Timestamp time.Time          `bson:"timestamp" json:"timestamp"`
}

// User schema
type User struct {
	Email       string `json:"email"`
	PhoneNumber string `json:"phone_number"`
	Password    string `json:"password"`
	DisplayName string `json:"display_name"`
	PhotoUrl    string `json:"photo_url"`
}

// Sign in credentials
type SignInCred struct {
	Email    string `json:"email"`
	Password string `json:"password"`
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
