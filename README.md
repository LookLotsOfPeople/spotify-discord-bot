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
The Bot Does Not Do Anything for 10+ Seconds After Authenticating | I Have Not Figured Out Why, but the Bot is Failing to Make the Config File. To Bypass, Open Command Prompt as Administrator and then Launch the Bot Through It. If You Do Not Trust My Bot... Well Just Build from Readable Source or Wait Until I Resolve this Sometime in the Future. The Bot is Exactly what is Seen in the Zipped Source that is with the Bot.
