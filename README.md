# DragonCon Reminder Bot

<img width="262.5" height="203.25" alt="image" src="https://github.com/user-attachments/assets/48686167-198c-455a-8d02-d5da69e89c8c" />

[DragonCon](https://www.dragoncon.org/) is a multigenre fan-run convention that occurs every year during Labor Day weekend in downtown Atlanta. DragonCon memberships (also referred to as tickets) go on sale up to a year in advance. Throughout the year on a quarterly basis, DragonCon increases their 5-day membership prices leading up to the event, incentivizing attendees to purchase their memberships as early as possible.

This bot will automatically post reminders of DragonCon price increases every 3 months starting on 3/1/2026 to a Discord server and channel of your choosing.

## Usage

This bot was developed on macOS and is currently only supported on macOS and Linux operating systems.

### 1. Prerequisites
- Install [Homebrew](https://brew.sh/)
- Install `gradle` by typing the following in a Terminal window:
```
brew install gradle
```
- Install Docker by typing the following in a Terminal window:
```
brew install docker
```
- Install Colima (optional for macOS) by typing the following in a Terminal window:
```
brew install colima
```
- Install `kubectl` by typing the following in a Terminal window:
```
brew install kubectl
```
- Install `helm` by typing the following in a Terminal window:
```
brew install helm
```

#### Set environment variables
1. Grab a [Discord Bot Token](https://discordgsm.com/guide/how-to-get-a-discord-bot-token) from your Discord server.
2. Grab a [Discord Channel ID](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID#h_01HRSTXPS5FMK2A5SMVSX4JW4E) from the Discord channel that you'd like the bot to post reminders to.
3. Use the following token values to update your environment variables by using the example file provided:
```bash
   # Copy the example file to a local .env
   cp .env.example .env

   # Open the .env file to edit with credentials
   vim .env
```
4. You may also need to run the following commands in a Terminal window:
```bash
export DISCORD_TOKEN=[YOUR TOKEN HERE]

export DISCORD_CHANNEL_ID=[YOUR CHANNEL ID HERE]
```

### 2. Build and Run Locally (Quickstart)
Run the following commands in a Terminal window from the root `dragoncon-reminder-bot` directory:

```bash
# Build the JAR file
make build

# Build the Docker image with a version tag
make docker-build VERSION=[VERSION NUMBER]

# Run the bot in Docker
make docker-run

# Stop the bot in Docker
make docker-stop

# List all make commands
make help
```

### 3. Discord Slash Commands
In Discord, you may use the following commands outside of the quarterly automated reminders:

- `/list-all-deadlines`: Lists all deadlines on the DragonCon site regardless of whether dates are already expired.
<img width="484" height="224" alt="image" src="https://github.com/user-attachments/assets/a403f382-551c-4cdd-b2a1-913182749907" />

- `/next-deadline`: Lists details on the upcoming deadline.
<img width="384" height="218" alt="image" src="https://github.com/user-attachments/assets/daf77f11-8dc1-4929-af03-7e4911ab396d" />

### 4. Deployment
Please not that this bot is currently deployed on an in-house server running a Kubernetes cluster.
The below steps assume a similar setup.

#### Prerequisites
1. Copy / create a Kubernetes config file to be able to use `kubectl`
2. Run the following command to confirm `kubectl` is running:
```bash
kubectl get po
```
3. Create `dragoncon-reminder-bot-values.yml` in the root directory by typing:
```bash
vim dragoncon-reminder-bot-values.yml
```
4. Add the following information to `dragoncon-reminder-bot-values.yml` and save it
```bash
discord:
  # Discord bot token - REQUIRED
  token: "[DISCORD TOKEN]"
  # Discord channel ID where messages will be sent - REQUIRED
  channelId: "[DISCORD CHANNEL ID]"
```

#### Deployment Steps
1. Run the following commands in a Terminal window from the root `dragoncon-reminder-bot` directory:

```bash
# Build the JAR file
make build

# Build the Docker image with a version tag
make docker-build VERSION=[VERSION NUMBER]

# Push the Docker image with a version tag
make docker-push VERSION=[VERSION NUMBER]
```

2. Update `./helm/values.yaml` under the `image:` section, next to `tag:` to match the version number specified above 
3. Run the following command to deploy:
```bash
helm upgrade --install dragoncon-reminder-bot ./helm --values dragoncon-reminder-bot-values.yml
```
4. Verify deployment by running the following command and observing that `dragoncon-reminder-bot` exists in the list of services running and reads `1/1` under the `READY` column:
```bash
kubectl get po
```
5. Deployment may also be verified in Discord directly by typing one of the slash commands listed above in a channel

#### View Logs
1. Run the following command to grab the pod name:
```bash
kubectl get po
```
2. Copy the `dragoncon-reminder-bot` pod name
3. Run the following command to view the logs:
```bash
kubectl logs dragoncon-reminder-bot-[IDENTIFIER]
```

## Troubleshooting

### Building in VS Code, unrecognized dependencies
1. Open the Command Palette in VS Code by using the keyboard shortcut `Cmd + Shift + P`.
2. Run the following command: `Java: Reload Projects`.
3. If the above command does not resolve the issue, please try the following command: `Java: Clean Java Language Server Workspace`.

### Docker command not found

```bash
# Make sure Docker CLI is installed
brew install docker docker-compose

# Add to your shell profile (~/.zshrc or ~/.bashrc)
export PATH="/usr/local/bin:$PATH"
```

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
