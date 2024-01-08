package com.github.jlabbude.jotabot.command;

import com.github.jlabbude.jotabot.command.commands.jotaJoin;
import com.github.jlabbude.jotabot.command.commands.jotaGrito;
import com.github.jlabbude.jotabot.command.commands.jotaStream;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, SlashCommand> commands = new HashMap<>();

    public CommandManager() {
        // Initialize the map with command names and their implementations
        commands.put("jotave", new jotaGrito());
        jotaStream streamCommand = new jotaStream();
        commands.put("jotajoin", new jotaJoin(streamCommand, 277898997975482379L));
        // Add more commands as needed
    }

    public Mono<Void> executeCommand(String commandName, MessageCreateEvent event) {
        // Get the command implementation from the map and execute it
        return Mono.justOrEmpty(commands.get(commandName))
                .flatMap(command -> command.execute(commandName, event));
    }
}