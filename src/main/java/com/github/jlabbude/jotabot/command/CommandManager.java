package com.github.jlabbude.jotabot.command;

import com.github.jlabbude.jotabot.command.commands.jotaJoin;
import com.github.jlabbude.jotabot.command.commands.jotaGrito;
import com.github.jlabbude.jotabot.command.commands.jotaStream;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, ChatCommand> commands = new HashMap<>();

    public CommandManager() {
        commands.put("jotave", new jotaGrito());
        jotaStream streamCommand = new jotaStream();
        commands.put("jotajoin", new jotaJoin(streamCommand, insertUserID);
    }

    public Mono<Void> executeCommand(String commandName, MessageCreateEvent event) {
        return Mono.justOrEmpty(commands.get(commandName))
                .flatMap(command -> command.execute(commandName, event));
    }
}
