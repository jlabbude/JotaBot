package com.github.jlabbude.jotabot;

import com.github.jlabbude.jotabot.command.CommandManager;
import com.github.jlabbude.jotabot.command.SlashCommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashSet;
import java.util.Set;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;

public class JotaBot {

    public static void main(String[] args) {

        CommandManager commandManager = new CommandManager();

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
                .login()
                .block();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getContent().startsWith("&"))
                .flatMap(event -> SlashCommandListener.execute(event, commandManager))
                .subscribe();

        client.onDisconnect().block();
    }
}
