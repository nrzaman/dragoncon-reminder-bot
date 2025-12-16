.PHONY: help build test docker-build docker-run docker-stop docker-logs clean colima-start colima-stop colima-status

help:
	@echo "DragonCon Reminder Bot - Available Commands:"
	@echo ""
	@echo "Build & Test:"
	@echo "  make build         - Build the JAR file"
	@echo "  make test          - Run all tests"
	@echo "  make clean         - Clean build artifacts"
	@echo ""
	@echo "Colima (Docker for macOS):"
	@echo "  make colima-start  - Start Colima"
	@echo "  make colima-stop   - Stop Colima"
	@echo "  make colima-status - Check Colima status"
	@echo "  make colima-setup  - Run full Colima setup"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build  - Build the Docker image"
	@echo "  make docker-run    - Run the bot in Docker"
	@echo "  make docker-stop   - Stop the Docker container"
	@echo "  make docker-logs   - View Docker logs"
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
	docker build -t dragoncon-reminder-bot .

docker-run: docker-build
	docker run -d \
		--name dragoncon-bot \
		--restart unless-stopped \
		-e DISCORD_TOKEN="$(DISCORD_TOKEN)" \
		-e DISCORD_CHANNEL_ID="$(DISCORD_CHANNEL_ID)" \
		dragoncon-reminder-bot

docker-stop:
	docker stop dragoncon-bot || true
	docker rm dragoncon-bot || true

docker-logs:
	docker logs -f dragoncon-bot

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
	docker rmi dragoncon-reminder-bot || true

# Colima commands
colima-start:
	@echo "Starting Colima..."
	colima start --cpu 4 --memory 4 --disk 100
	@echo "Setting Docker context to colima..."
	docker context use colima

colima-stop:
	@echo "Stopping Colima..."
	colima stop

colima-status:
	@echo "Colima Status:"
	@colima status || echo "Colima is not running"
	@echo ""
	@echo "Docker Context:"
	@docker context show || echo "Docker not available"

colima-setup:
	@./setup-colima.sh
