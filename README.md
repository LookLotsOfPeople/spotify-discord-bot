# spotify-discord-bot
A Self-Hosted Discord Bot to Sync Music from Spotify to Discord

## Setting Up the Required Files
If you have run the bot release without looking here,
you will notice that the bot creates a config file in which there is a section for `token` and `ownerId`.
Both of these will be given by your discordapi page.

## Known Issues
Issue | State
----- | -----
Webpage Returns an Error After Account is Authorized | This is NOT a bug but rather intended. I am yet to find a cleaner way to show completion.
The Bot Cannot Get the Token Correctly from the Web Authentication | Please Disable Any Plugin Forcing Https to be Used for the http://localhost:8888. The Authentication Only Works Over Http.
