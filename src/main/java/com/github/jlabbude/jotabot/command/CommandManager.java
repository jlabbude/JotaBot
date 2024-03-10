package com.github.jlabbude.jotabot.command;

import com.github.jlabbude.jotabot.command.commands.JotaAvatar;
import com.github.jlabbude.jotabot.command.commands.JotaGrito;
import com.github.jlabbude.jotabot.command.commands.JotaJoin;
import com.github.jlabbude.jotabot.command.commands.JotaPersistentJoin;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, ChatCommand> commands = new HashMap<>();

    public CommandManager() {
        commands.put("jotave", new JotaGrito());
        commands.put("jotajoin", new JotaJoin());
        commands.put("join", new JotaPersistentJoin());
        commands.put("jotafoto", new JotaAvatar());
    }

    public Mono<Void> executeCommand(String commandName, MessageCreateEvent event) {
        return Mono.justOrEmpty(commands.get(commandName))
            .flatMap(command -> command.execute(commandName, event));
    }


}
