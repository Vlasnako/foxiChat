package internal

import (
	"context"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/messaging"
	"google.golang.org/api/option"
)

var FirebaseApp *firebase.App

func FirebaseInit(ctx context.Context) error {
	// Use the path to your service account credential json file
	opt := option.WithCredentialsFile("C:\\Users\\VlasN\\Documents\\keyGolang\\foxichat-eaffd-firebase-adminsdk-dri81-5e0b063815.json")
	// Create a new firebase app
	config := &firebase.Config{ProjectID: "foxichat-eaffd"}
	app, err := firebase.NewApp(ctx, config, opt)
	if err != nil {
		return err
	}
	FirebaseApp = app
	return nil
}

func CreateFCMClient(ctx context.Context) (*messaging.Client, error) {
	// Get the FCM object
	fcmClient, err := FirebaseApp.Messaging(ctx)
	if err != nil {
		return nil, err
	}
	return fcmClient, nil
}
