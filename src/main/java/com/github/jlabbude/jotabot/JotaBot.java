package com.github.jlabbude.jotabot;

import com.github.jlabbude.jotabot.command.CommandManager;
import com.github.jlabbude.jotabot.command.SlashCommandListener;
import com.github.jlabbude.jotabot.command.commands.jotaJoin;
import com.github.jlabbude.jotabot.command.commands.jotaStream;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class JotaBot {

    public static void main(String[] args) {

        CommandManager commandManager = new CommandManager();

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
                .login()
                .block();

<<<<<<< HEAD
        new jotaJoin(new jotaStream(), insertUsedId)
                .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null))
=======
        new jotaJoin(new jotaStream(), insertUserId)
<<<<<<< HEAD
<<<<<<< HEAD
                .execute("jotajoin", new MessageCreateEvent(client, null, null, insertChannelId, null))
>>>>>>> 957fa19 (removed ids)
=======
                .execute("jotajoin", new MessageCreateEvent(client, null, null, 852717593236471829L, null))
>>>>>>> 3436ef8 (optimize imports)
=======
                .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null))
>>>>>>> 6b3dac5 (Update JotaBot.java)
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().startsWith("&"))
                .flatMap(event -> SlashCommandListener.execute(event, commandManager))
                .subscribe();

        client.onDisconnect().block();
    }
}
