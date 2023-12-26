package auth

import (
	"context"
	"fmt"
	"log"
	"server/internal"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/auth"
)

// function to get the FirebaseAuth instance
func getUserAuthInfo(
	ctx context.Context,
	app *firebase.App,
	uid string,
) (*auth.UserRecord, error) {
	client, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("error getting Auth client: %v\n", err)
	}

	u, err := client.GetUser(ctx, uid)
	if err != nil {
		log.Fatalf("error getting user %s: %v\n", uid, err)
	}
	log.Printf("Successfully fetched user data: %v\n", u)
	return u, err
}

// Get Firebase user details by email
func getUserAuthInfoByEmail(
	ctx context.Context,
	app *firebase.App,
	email string,
) (*auth.UserRecord, error) {
	client, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("error getting Auth client: %v\n", err)
	}
	u, err := client.GetUserByEmail(ctx, email)

	if err != nil {
		log.Fatalf("error getting user by email %s: %v\n", email, err)
	}
	log.Printf("Successfully fetched user data: %v\n", u)

	return u, err
}

// Create new FIREBASE user
func CreateNewUser(
	ctx context.Context,
	app *firebase.App,
	email string,
	phoneNumber string,
	password string,
	displayName string,
	photoUrl string,

) (*auth.UserRecord, error) {
	client, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("error getting Auth client: %v\n", err)
	}
	params := (&auth.UserToCreate{}).
		Email(email).
		EmailVerified(false).
		PhoneNumber(phoneNumber).
		Password(password).
		DisplayName("John Doe").
		PhotoURL(photoUrl).
		Disabled(false)
	u, err := client.CreateUser(ctx, params)
	if err != nil {
		fmt.Printf("error creating user: %v\n", err)
	}
	log.Printf("Successfully created user: %v\n", u)
	return u, err
}

// Update Existing user
func UpdateUser(
	ctx context.Context,
	app *firebase.App,
	uid string,
	email string,
	phoneNumber string,
	password string,
	displayName string,
	photoUrl string,

) (*auth.UserRecord, error) {
	client, err := app.Auth(ctx)
	if err != nil {
		log.Fatalf("error getting Auth client: %v\n", err)
	}
	params := (&auth.UserToUpdate{}).
		Email(email).
		EmailVerified(false).
		PhoneNumber(phoneNumber).
		Password(password).
		DisplayName(displayName).
		PhotoURL(photoUrl).
		Disabled(false)
	u, err := client.UpdateUser(ctx, uid, params)
	if err != nil {
		log.Fatalf("error updating user: %v\n", err)
	}
	log.Printf("Successfully updated user: %v\n", u)
	return u, err
}

// Delete a user
func DeleteUser(
	ctx context.Context,
	app *firebase.App,
	uid string,
) error {
	client, err := app.Auth(ctx)

	if err != nil {
		log.Fatalf("error getting Auth client: %v\n", err)
	}
	err = client.DeleteUser(ctx, uid)
	if err != nil {
		log.Fatalf("error deleting user: %v\n", err)
	}
	log.Printf("Successfully deleted user: %s\n", uid)
	return nil
}

// On Sign In user
func SignInWithEmail(
	ctx context.Context,
	app *firebase.App,
	email string,
	signinAttempt internal.SignInCred,
) {
	//u, err := getUserAuthInfoByEmail(ctx, app, email)

}
