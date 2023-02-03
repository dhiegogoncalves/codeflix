package database

import (
	"encoder/domain"
	"log"

	"github.com/jinzhu/gorm"
)

type Database struct {
	Db            *gorm.DB
	Dsn           string
	DsnTest       string
	DbType        string
	DbTypeTest    string
	Debug         bool
	AutoMigrateDb bool
	Env           string
}

func NewDb() *Database {
	return &Database{}
}

func NewDbTest() *gorm.DB {
	db := NewDb()
	db.Env = "test"
	db.DbTypeTest = "sqlite3"
	db.DsnTest = ":memory:"
	db.AutoMigrateDb = true
	db.Debug = true

	conn, err := db.Connect()
	if err != nil {
		log.Fatal("Test db error: %v", err)
	}

	return conn
}

func (d *Database) Connect() (*gorm.DB, error) {
	var err error

	if d.Env != "test" {
		d.Db, err = gorm.Open(d.DbType, d.Dsn)
	} else {
		d.Db, err = gorm.Open(d.DbTypeTest, d.DsnTest)
	}

	if err != nil {
		return nil, err
	}

	if d.Debug {
		d.Db.LogMode(true)
	}

	if d.AutoMigrateDb {
		d.Db.AutoMigrate(&domain.Video{}, &domain.Job{})
	}

	return d.Db, nil
}
