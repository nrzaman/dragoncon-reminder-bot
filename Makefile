.PHONY: help build test docker-build docker-run docker-stop docker-logs clean colima-start colima-stop colima-status

# Docker image configuration
IMAGE_NAME = nrzaman/dragoncon-reminder-bot
VERSION ?= latest

help:
	@echo "DragonCon Reminder Bot - Available Commands:"
	@echo ""
	@echo "Build & Test:"
	@echo "  make build         - Build the JAR file"
	@echo "  make test          - Run all tests"
	@echo "  make clean         - Clean build artifacts"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build  - Build the Docker image (VERSION=v1.0.0 for specific version)"
	@echo "  make docker-run    - Run the bot in Docker"
	@echo "  make docker-stop   - Stop the Docker container"
	@echo "  make docker-logs   - View Docker logs"
	@echo "  make docker-push   - Push image to Docker Hub (pushes VERSION and latest)"
	@echo ""
	@echo "Docker Compose:"
	@echo "  make up            - Start with docker-compose"
	@echo "  make down          - Stop docker-compose"
	@echo "  make logs          - View docker-compose logs"
	@echo ""

build:
	./gradlew build

test:
	./gradlew test

docker-build: build
	docker build -t $(IMAGE_NAME):$(VERSION) -t $(IMAGE_NAME):latest .

docker-run: docker-build
	docker run -d \
		--name dragoncon-bot \
		--restart unless-stopped \
		-e DISCORD_TOKEN="$(DISCORD_TOKEN)" \
		-e DISCORD_CHANNEL_ID="$(DISCORD_CHANNEL_ID)" \
		$(IMAGE_NAME):latest

docker-stop:
	docker stop dragoncon-bot || true
	docker rm dragoncon-bot || true

docker-logs:
	docker logs -f dragoncon-bot

docker-push:
	docker push $(IMAGE_NAME):$(VERSION)
	@if [ "$(VERSION)" != "latest" ]; then \
		docker push $(IMAGE_NAME):latest; \
	fi

up: build
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	./gradlew clean
	docker-compose down || true
	docker rm dragoncon-bot || true
	docker rmi $(IMAGE_NAME):latest || true
	docker rmi $(IMAGE_NAME):$(VERSION) || true

