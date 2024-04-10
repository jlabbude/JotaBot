# JotaBot
A Discord bot to track the time one of my friends took to start screensharing


## Screenshots

!["Jotave took 0 hours, 48 minutes, 7 seconds to start screensharing"](https://i.imgur.com/vIBivSU.png)

## Making it work

### IDs to track

On the main class, you should replace the variables

`insertUserId` 

`insertGuildId`

`insertMsgChId`

with their corresponding value. It's worth mentioning that `insertUserId` should be copied from inside the guild you're trying to implement the bot, since the user id is intrinsically attached to the guild id.


### Building the gateway

The API key is attached to the `args[]` array that Java mandates to be included in the main class, for security reasons. With that in mind you should either replace

`GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()`

with

`GatewayDiscordClient client = DiscordClientBuilder.create("YOUR_API_KEY").build()`

or include your API key as a enviroment variable/command line argument.
