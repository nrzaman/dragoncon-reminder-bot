# DragonCon Reminder Bot

[DragonCon](https://www.dragoncon.org/) is a multigenre fan-run convention that occurs every year during Labor Day weekend in downtown Atlanta. DragonCon memberships (also referred to as tickets) go on sale up to a year in advance. Throughout the year on a quarterly basis, DragonCon increases their 5-day membership prices leading up to the event, incentivizing attendees to purchase their memberships as early as possible.

This bot will automatically post reminders of DragonCon price increases every 3 months starting on 3/1/2026 to a Discord server and channel of your choosing.

## Usage

This bot was developed on macOS and is currently only supported on macOS and Linux operating systems.

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
In Discord, you may use the following commands outside of the quarterly automated reminders:

- `/list-all-deadlines`: Lists all deadlines on the DragonCon site regardless of whether dates are already expired.
<img width="484" height="224" alt="image" src="https://github.com/user-attachments/assets/a403f382-551c-4cdd-b2a1-913182749907" />

- `/next-deadline`: Lists details on the upcoming deadline.
<img width="384" height="218" alt="image" src="https://github.com/user-attachments/assets/daf77f11-8dc1-4929-af03-7e4911ab396d" />

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
