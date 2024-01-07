package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.SlashCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class jotaGrito implements SlashCommand {
    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {

        return event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("JOTAVEEEE, JOTAVEEEEEEEEEEEE"))
                .then();
    }
}
