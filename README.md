# DragonCon Reminder Bot

This is a simple application that posts reminders in a Discord channel for when ticket (membership) prices go up for DragonCon.

The bot will automatically post reminders of DragonCon price increases every 3 months starting on 3/1/2026 to a server and channel of your choosing.

## Usage

### 1. Setup the Bot

```bash
# Run the setup script - this will automatically install colima
./setup-colima.sh

# Follow the prompts to configure your .env file
```

### 2. Build and Run

```bash
# Build everything
./setup-colima.sh build

# Start the bot
./setup-colima.sh start

# View logs
docker-compose logs -f

# Stop the bot
./setup-colima.sh stop
```

### 3. Discord Slash Commands
In your Discord server, you may use the following commands outside of the reminder windows:

- `/list-all-deadlines`: Lists all deadlines on the DragonCon site regardless of whether dates are already expired.
- `/next-deadline`: Lists the upcoming deadline.

## Troubleshooting

### Colima won't start

```bash
# Delete and recreate
colima delete

./setup-colima.sh start
```

### Docker command not found

```bash
# Make sure Docker CLI is installed
brew install docker docker-compose

# Add to your shell profile (~/.zshrc or ~/.bashrc)
export PATH="/usr/local/bin:$PATH"
```

### Cannot connect to Docker daemon

```bash
# Check Colima is running
colima status

# If running, set the Docker context
docker context use colima

# Verify
docker context ls
```

### Permission denied

```bash
# Restart Colima
colima restart

# Or delete and recreate
colima delete
colima start
```

### Out of disk space

```bash
# Stop Colima
colima stop

# Start with more disk space
colima start --cpu 4 --memory 4 --disk 200
```

### Check Colima logs

```bash
colima logs
```

## Additional Resources

- [Colima GitHub](https://github.com/abiosoft/colima)
- [Colima Documentation](https://github.com/abiosoft/colima/blob/main/README.md)
- [Docker Documentation](https://docs.docker.com/)