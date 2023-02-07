package repositories

import (
	"encoder/domain"
	"fmt"

	"github.com/google/uuid"
	"github.com/jinzhu/gorm"
)

type VideoRepository interface {
	Insert(video *domain.Video) (*domain.Video, error)
	Find(id string) (*domain.Video, error)
}

type VideoRepositoryDb struct {
	Db *gorm.DB
}

func NewVideoRepository(db *gorm.DB) *VideoRepositoryDb {
	return &VideoRepositoryDb{Db: db}
}

func (r VideoRepositoryDb) Insert(video *domain.Video) (*domain.Video, error) {
	if video.ID == "" {
		video.ID = uuid.NewString()
	}

	err := r.Db.Create(video).Error
	if err != nil {
		return nil, err
	}

	return video, nil
}

func (r VideoRepositoryDb) Find(id string) (*domain.Video, error) {
	var video domain.Video

	r.Db.Preload("Jobs").First(&video, "id = ?", id)
	if video.ID == "" {
		return nil, fmt.Errorf("video does not exist")
	}

	return &video, nil
}
