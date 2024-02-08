package com.github.jlabbude.jotabot;

import com.github.jlabbude.jotabot.command.CommandManager;
import com.github.jlabbude.jotabot.command.ChatCommandListener;
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

        new jotaJoin(new jotaStream(), insertUserId)
                .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().startsWith("&"))
                .flatMap(event -> ChatCommandListener.execute(event, commandManager))
                .subscribe();

        client.onDisconnect().block();
    }
}
