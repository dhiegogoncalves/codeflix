package utils_test

import (
	"encoder/framework/utils"
	"testing"

	"github.com/stretchr/testify/require"
)

func TestIsJson(t *testing.T) {
	json := `{
		"id": "adea2635-5e0a-4697-a9a9-2457b9d222f5",
		"file_path": "video.mp4",
		"status": "pending"
	}`

	err := utils.IsJson(json)
	require.Nil(t, err)

	json = ``

	err = utils.IsJson(json)
	require.Error(t, err)
}
