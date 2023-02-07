package services_test

import (
	"encoder/application/repositories"
	"encoder/application/services"
	"encoder/domain"
	"encoder/framework/database"
	"log"
	"os"
	"testing"
	"time"

	"github.com/google/uuid"
	"github.com/joho/godotenv"
	"github.com/stretchr/testify/require"
)

func init() {
	err := godotenv.Load("../../.env")
	if err != nil {
		log.Fatalf("Error loading .env file: %v", err)
	}
}

func prepare() (*domain.Video, repositories.VideoRepositoryDb) {
	db := database.NewDbTest()
	defer db.Close()

	video := domain.NewVideo()
	video.ID = uuid.NewString()
	video.FilePath = "video.mp4"
	video.CreatedAt = time.Now()

	r := repositories.VideoRepositoryDb{Db: db}

	return video, r
}

func TestVideoServiceDownload(t *testing.T) {
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

	err = service.Finish()
	require.Nil(t, err)
}
