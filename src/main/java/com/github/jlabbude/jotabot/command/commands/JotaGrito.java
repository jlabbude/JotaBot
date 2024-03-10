package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.ChatCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class JotaGrito implements ChatCommand {
    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {

        return event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("https://tenor.com/view/astrojv-madshours-gif-18804356"))
            .then();
    }
}
