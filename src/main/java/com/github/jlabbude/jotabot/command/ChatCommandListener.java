package com.github.jlabbude.jotabot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class ChatCommandListener {
    public static Mono<Void> execute(MessageCreateEvent event, CommandManager commandManager) {
        String content = event.getMessage().getContent();
        String commandName = extractCommandName(content);

        return commandManager.executeCommand(commandName, event);
    }

    private static String extractCommandName(String content) {
        return content.split(" ")[0].substring(1);
    }
}
