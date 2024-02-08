package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.ChatCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class jotaStream implements ChatCommand {
    private final long targetUserId = insertUseriD;

    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {
        return event.getGuild()
                .flatMap(guild -> guild.getMemberById(Snowflake.of(targetUserId)))
                        .flatMap(user -> event.getGuild()
                                .flatMap(guild -> guild.getMemberById(user.getId()))
                                .flatMap(Member::getVoiceState)
                                .flatMap(vs -> Mono.just(vs.isSelfStreaming()))
                        )
                        .repeatWhen(repeat -> repeat.delayElements(Duration.ofSeconds(1)))
                        .takeUntil(Boolean::booleanValue)
                        .then();
    }
}
