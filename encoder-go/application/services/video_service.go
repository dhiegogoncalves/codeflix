package services

import (
	"context"
	"encoder/application/repositories"
	"encoder/domain"
	"io"
	"log"
	"os"
	"os/exec"

	"cloud.google.com/go/storage"
)

type VideoService struct {
	Video           *domain.Video
	VideoRepository repositories.VideoRepository
}

func NewVideoService() VideoService {
	return VideoService{}
}

func (s *VideoService) Download(bucketName string) error {
	ctx := context.Background()
	client, err := storage.NewClient(ctx)
	if err != nil {
		return err
	}

	bkt := client.Bucket(bucketName)
	obj := bkt.Object(s.Video.FilePath)
	r, err := obj.NewReader(ctx)
	if err != nil {
		return err
	}
	defer r.Close()

	body, err := io.ReadAll(r)
	if err != nil {
		return err
	}

	f, err := os.Create(os.Getenv("LOCAL_STORAGE_PATH") + "/" + s.Video.ID + ".mp4")
	if err != nil {
		return err
	}

	_, err = f.Write(body)
	if err != nil {
		return err
	}
	defer f.Close()

	log.Printf("video %v has been stored", s.Video.ID)

	return nil
}

func (s *VideoService) Fragment() error {
	videoLocalStoragePath := os.Getenv("LOCAL_STORAGE_PATH") + "/" + s.Video.ID

	err := os.Mkdir(videoLocalStoragePath, os.ModePerm)
	if err != nil {
		return nil
	}

	source := videoLocalStoragePath + ".mp4"
	target := videoLocalStoragePath + ".frag"

	cmd := exec.Command("mp4fragment", source, target)
	output, err := cmd.CombinedOutput()
	if err != nil {
		return err
	}

	printOutput(output)

	return nil
}

func printOutput(output []byte) {
	if len(output) > 0 {
		log.Printf("=====> Output: %s\n", string(output))
	}
}

func (s *VideoService) Encode() error {
	videoLocalStoragePath := os.Getenv("LOCAL_STORAGE_PATH") + "/" + s.Video.ID

	cmdArgs := []string{}
	cmdArgs = append(cmdArgs, videoLocalStoragePath+".frag")
	cmdArgs = append(cmdArgs, "--use-segment-timeline")
	cmdArgs = append(cmdArgs, "-o")
	cmdArgs = append(cmdArgs, videoLocalStoragePath)
	cmdArgs = append(cmdArgs, "-f")
	cmdArgs = append(cmdArgs, "--exec-dir")
	cmdArgs = append(cmdArgs, "/opt/bento4/bin/")
	cmd := exec.Command("mp4dash", cmdArgs...)

	output, err := cmd.CombinedOutput()
	if err != nil {
		return err
	}

	printOutput(output)

	return nil
}

func (s *VideoService) Finish() error {
	videoLocalStoragePath := os.Getenv("LOCAL_STORAGE_PATH") + "/" + s.Video.ID

	err := os.Remove(videoLocalStoragePath + ".mp4")
	if err != nil {
		log.Println("error removing mp4", s.Video.ID, ".mp4")
		return err
	}

	err = os.Remove(videoLocalStoragePath + ".frag")
	if err != nil {
		log.Println("error removing frag", s.Video.ID, ".frag")
		return err
	}

	err = os.RemoveAll(videoLocalStoragePath)
	if err != nil {
		log.Println("error removing directory", s.Video.ID)
		return err
	}

	log.Println("files have been removed: ", s.Video.ID)

	return nil
}

func (s *VideoService) InsertVideo() error {
	_, err := s.VideoRepository.Insert(s.Video)

	if err != nil {
		return err
	}

	return nil
}
