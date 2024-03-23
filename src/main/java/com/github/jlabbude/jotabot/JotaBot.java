package com.github.jlabbude.jotabot;

import com.github.jlabbude.jotabot.command.ChatCommandListener;
import com.github.jlabbude.jotabot.command.CommandManager;
import com.github.jlabbude.jotabot.command.commands.JotaJoin;
import com.github.jlabbude.jotabot.command.commands.JotaPersistentJoin;
import com.github.jlabbude.jotabot.command.commands.JotaStream;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class JotaBot {

    //For my application/intention with this bot, it's better to hardcode
    //all of this stuff.
    public static final long insertUserId = 0L;
    public static final long insertGuildId = 0L;
    public static final long insertMsgChId = 0L;

    public static void main(String[] args) {

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
            .login()
            .block();

        new JotaJoin(new JotaStream())
                .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null))
                .subscribe();

        new JotaPersistentJoin()
            .execute("join", new MessageCreateEvent(client, null, null, insertGuildId, null))
            .subscribe();

        new JotaJoin(new JotaStream())
            .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null))
            .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
            .filter(event -> event.getMessage().getContent().startsWith("&"))
            .flatMap(event -> ChatCommandListener.execute(event, new CommandManager()))
            .subscribe();

        client.onDisconnect().block();
    }
}
