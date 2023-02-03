package repositories_test

import (
	"encoder/application/repositories"
	"encoder/domain"
	"encoder/framework/database"
	"testing"
	"time"

	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
)

func TestJobRepositoryDbInsertAndFind(t *testing.T) {
	db := database.NewDbTest()
	defer db.Close()

	video := domain.NewVideo()
	video.ID = uuid.NewString()
	video.FilePath = "path"
	video.CreatedAt = time.Now()

	r := repositories.VideoRepositoryDb{Db: db}
	r.Insert(video)

	j, err := domain.NewJob("output_path", "Pending", video)
	require.Nil(t, err)

	re := repositories.JobRepositoryDb{Db: db}
	re.Insert(j)

	job, err := re.Find(j.ID)

	require.NotEmpty(t, job.ID)
	require.Nil(t, err)
	require.Equal(t, j.ID, job.ID)
	require.Equal(t, j.VideoID, video.ID)
}

func TestJobRepositoryDbUpdate(t *testing.T) {
	db := database.NewDbTest()
	defer db.Close()

	video := domain.NewVideo()
	video.ID = uuid.NewString()
	video.FilePath = "path"
	video.CreatedAt = time.Now()

	r := repositories.VideoRepositoryDb{Db: db}
	r.Insert(video)

	j, err := domain.NewJob("output_path", "Pending", video)
	require.Nil(t, err)

	re := repositories.JobRepositoryDb{Db: db}
	re.Insert(j)

	j.Status = "Complete"
	re.Update(j)

	job, err := re.Find(j.ID)

	require.NotEmpty(t, job.ID)
	require.Nil(t, err)
	require.Equal(t, j.ID, job.ID)
	require.Equal(t, j.Status, job.Status)
}
