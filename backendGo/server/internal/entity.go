package internal

import (
	"time"

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

// Message schema
type Message struct {
	ID         primitive.ObjectID `bson:"_id" json:"id"`
	RoomId     string             `bson:"room_id" json:"room_id"`
	AuthorId   string             `bson:"author_id" json:"author_id"`
	AuthorName string             `bson:"author_name" json:"author_name"`
	Body       string             `bson:"body" json:"body"`
	Timestamp  time.Time          `bson:"timestamp" json:"timestamp"`
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
