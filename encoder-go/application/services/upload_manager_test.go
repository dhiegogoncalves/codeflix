package services_test

import (
	"encoder/application/services"
	"log"
	"os"
	"testing"

	"github.com/joho/godotenv"
	"github.com/stretchr/testify/require"
)

func init() {
	err := godotenv.Load("../../.env")
	if err != nil {
		log.Fatalf("Error loading .env file: %v", err)
	}
}

func TestVideoServiceUpload(t *testing.T) {
	video, r := prepare()

	service := services.NewVideoService()
	service.Video = video
	service.VideoRepository = r

	err := service.Download(os.Getenv("INPUT_BUCKET_NAME"))
	require.Nil(t, err)

	err = service.Fragment()
	require.Nil(t, err)

	err = service.Encode()
	require.Nil(t, err)

	vu := services.NewVideoUpload()
	vu.OutputBucket = os.Getenv("OUTPUT_BUCKET_NAME")
	vu.VideoPath = os.Getenv("LOCAL_STORAGE_PATH") + "/" + video.ID

	doneUpload := make(chan string)
	go vu.ProcessUpload(50, doneUpload)

	result := <-doneUpload
	require.Equal(t, result, "upload completed")
}
