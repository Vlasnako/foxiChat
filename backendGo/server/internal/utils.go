package internal

import (
	"errors"
	"regexp"
	"strings"
)

const EMAIL_REGEX = `^(?i)[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]+$`
const PHONE_REGEX = `^([+]\d{2})?\d{10}$`

func ValidateUser(u *User) error {
	if len(strings.TrimSpace(u.DisplayName)) == 0 {
		return errors.New("user display name invalid")
	}
	var regex = regexp.MustCompile(EMAIL_REGEX)
	if !regex.MatchString(u.Email) {
		return errors.New("user email invalid")
	}
	if len(strings.TrimSpace(u.Password)) < 6 {
		return errors.New("user password invalid")
	}
	var regexPhone = regexp.MustCompile(PHONE_REGEX)
	if !regexPhone.MatchString(u.PhoneNumber) {
		return errors.New("user phone number invalid")
	}
	return nil
}
