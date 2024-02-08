package com.github.jlabbude.jotabot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface ChatCommand {
    Mono<Void> execute(String commandName, MessageCreateEvent event);
}