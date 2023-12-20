postgresinit:
			docker run --name postgres16 -p 5433:5432 -e POSTGRES_USER=root -e POSTGRES_PASSWORD=password -d postgres:16-alpine   
postgres: 
	docker exec -it postgres16 psql

createdb:
	docker exec -it posgres16 createdb --username=root --owner=root foxiChat

dropdb:
	docker exec -it postgres16 dropdb foxiChat

migrateup:
	migrate -path db/migrations -database "postgresql://root:password@localhost:5433/foxiChat?sslmode=disable" -verbose up

migratedown:
		migrate -path db/migrations -database "postgresql://root:password@localhost:5433/foxiChat?sslmode=disable" -verbose down


.PHONY: postgresinit postgres createdb dropdb migrateup migratedown