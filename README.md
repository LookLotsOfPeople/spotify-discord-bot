# spotify-discord-bot
A Self-Hosted Discord Bot to Sync Music from Spotify to Discord

## Setting Up the Required Files
If you have run the bot release without looking here,
you will notice that the bot creates a config file in which there is a section for `token` and `ownerId`.
Both of these will be given by your discordapi page.

## How to Use
1. Join a Voice Channel the Bot has Access and Permissions to Join
2. Double click on `spotifyDiscordBot.jar`
3. To Close: Direct Message the Bot `exit`.

## Known Issues
Issue | State
----- | -----
Webpage Returns an Error After Account is Authorized | This is NOT a Bug but Rather Intended. I am Yet to Find a Cleaner Way to Show Completion.
The Bot Cannot Get the Token Correctly from the Web Authentication | Please Disable Any Plugin Forcing Https to be Used for the http://localhost:8888. The Authentication Only Works Over Http.
