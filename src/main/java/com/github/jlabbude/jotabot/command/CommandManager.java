package com.github.jlabbude.jotabot.command;

import com.github.jlabbude.jotabot.command.commands.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, ChatCommand> commands = new HashMap<>();

    public CommandManager() {
        commands.put("jotave", new JotaGrito());
        JotaStream streamCommand = new JotaStream();
        commands.put("jotajoin", new JotaJoin(streamCommand));
        commands.put("join", new JotaPersistentJoin());
        commands.put("jotafoto", new JotaAvatar());
    }

    public Mono<Void> executeCommand(String commandName, MessageCreateEvent event) {
        return Mono.justOrEmpty(commands.get(commandName))
            .flatMap(command -> command.execute(commandName, event));
    }


}
