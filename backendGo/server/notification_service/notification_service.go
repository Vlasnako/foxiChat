package notificationservice

import (
	"context"
	"fmt"
	"server/internal"

	"firebase.google.com/go/v4/messaging"
)

func SendOneNotificationToken(
	fcmClient *messaging.Client,
	ctx context.Context,
	token string,
	message string,
) error {
	//Send to One Token
	_, err := fcmClient.Send(ctx, &messaging.Message{
		Token: token,
		Data: map[string]string{
			message: message,
		},
	})

	return err

}
func SendMultipleNotificationTokens(
	fcmClient *messaging.Client,
	ctx context.Context,
	tokens []string,
	message internal.Message,
) error {

	//Send to Multiple Tokens
	_, err := fcmClient.SendEachForMulticast(ctx, &messaging.MulticastMessage{
		Data: map[string]string{
			"author_name": message.AuthorName,
			"author_id":   message.AuthorId,
			"room_id":     message.RoomId,
			"body":        message.Body,
			"timestamp":   message.Timestamp.String(),
		},
		Tokens: tokens,
	})
	fmt.Println("Sending tokens author:", message.AuthorName)
	return err
}
