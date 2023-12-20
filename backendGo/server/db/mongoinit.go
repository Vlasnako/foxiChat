package db

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.mongodb.org/mongo-driver/mongo/readpref"
)

func Init(ctx context.Context, URI string) (*mongo.Client, error) {
	client, err := mongo.Connect(ctx, options.Client().ApplyURI(URI))

	if err != nil {
		return nil, err
	}
	// Pinging the db
	if err := client.Ping(ctx, readpref.Primary()); err != nil {
		return nil, err
	}
	fmt.Println("Connection to mongoDb established")
	return client, nil
}
