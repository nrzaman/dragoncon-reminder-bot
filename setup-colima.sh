#!/bin/bash
# Setup script for running DragonCon Reminder Bot with Colima

set -e

echo "🐳 DragonCon Reminder Bot - Colima Setup"
echo "========================================"
echo ""

# Check if Colima is installed
if ! command -v colima &> /dev/null; then
    echo "❌ Colima is not installed."
    echo ""
    echo "Install with Homebrew:"
    echo "  brew install colima docker docker-compose"
    echo ""
    exit 1
fi

echo "✅ Colima is installed"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker CLI is not installed."
    echo ""
    echo "Install with Homebrew:"
    echo "  brew install docker docker-compose"
    echo ""
    exit 1
fi

echo "✅ Docker CLI is installed"

# Check if Colima is running
if ! colima status &> /dev/null; then
    echo "⚠️  Colima is not running. Starting Colima..."
    echo ""

    # Try to start Colima
    echo "Starting Colima with 4 CPUs, 4GB RAM, 100GB disk..."
    if ! colima start --cpu 4 --memory 4 --disk 100 2>/dev/null; then
        echo ""
        echo "⚠️  Failed to start existing Colima instance."
        echo "This might be due to a corrupted VM state."
        echo ""
        read -p "Delete and recreate Colima VM? (y/N): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "Deleting Colima VM..."
            colima delete
            echo "Creating new Colima VM..."
            colima start --cpu 4 --memory 4 --disk 100
            echo "✅ Colima started successfully"
        else
            echo "❌ Colima setup cancelled"
            exit 1
        fi
    else
        echo "✅ Colima started successfully"
    fi
else
    echo "✅ Colima is already running"
fi

echo ""
echo "📋 Colima Status:"
colima status
echo ""

# Verify Docker context
CURRENT_CONTEXT=$(docker context show)
if [ "$CURRENT_CONTEXT" != "colima" ]; then
    echo "⚠️  Docker context is '$CURRENT_CONTEXT', switching to 'colima'..."
    docker context use colima
    echo "✅ Docker context switched to colima"
else
    echo "✅ Docker context is already set to colima"
fi

echo ""
echo "🔧 Checking for .env file..."

if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "✅ Created .env file"
        echo ""
        echo "⚠️  IMPORTANT: Edit .env and add your Discord credentials:"
        echo "   - DISCORD_TOKEN"
        echo "   - DISCORD_CHANNEL_ID"
        echo ""
        echo "Then run: ./setup-colima.sh build"
        exit 0
    else
        echo "❌ .env.example not found"
        exit 1
    fi
else
    echo "✅ .env file exists"
fi

# Check if we should build
if [ "$1" == "build" ]; then
    echo ""
    echo "🔨 Building the application..."
    ./gradlew build

    echo ""
    echo "🐳 Building Docker image..."
    docker build -t dragoncon-reminder-bot .

    echo ""
    echo "✅ Build complete!"
    echo ""
    echo "To start the bot, run:"
    echo "  docker-compose up -d"
    echo ""
    echo "To view logs:"
    echo "  docker-compose logs -f"

elif [ "$1" == "start" ]; then
    echo ""
    echo "🚀 Starting the bot..."
    docker-compose up -d

    echo ""
    echo "✅ Bot started!"
    echo ""
    echo "View logs with:"
    echo "  docker-compose logs -f"

elif [ "$1" == "stop" ]; then
    echo ""
    echo "🛑 Stopping the bot..."
    docker-compose down

    echo ""
    echo "✅ Bot stopped"

else
    echo ""
    echo "Next steps:"
    echo "  1. Ensure your .env file has the correct Discord credentials"
    echo "  2. Build and start: ./setup-colima.sh build"
    echo "  3. Start the bot: ./setup-colima.sh start"
    echo "  4. View logs: docker-compose logs -f"
    echo "  5. Stop the bot: ./setup-colima.sh stop"
    echo ""
fi
