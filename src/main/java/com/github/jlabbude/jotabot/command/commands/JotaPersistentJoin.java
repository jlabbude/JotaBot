package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.ChatCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Mono;

import static com.github.jlabbude.jotabot.JotaBot.insertUserId;

public class JotaPersistentJoin implements ChatCommand {
    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {
        return event.getGuild()
            .flatMap(guild ->
                guild.getMemberById(Snowflake.of(insertUserId))
                    .flatMap(Member::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        .flatMap(VoiceChannel::join)
                        .flatMap(voiceConnection -> {
                            event.getClient().getEventDispatcher().on(VoiceStateUpdateEvent.class)
                                .filter(voiceStateUpdateEvent ->
                                    voiceStateUpdateEvent.isLeaveEvent() || voiceStateUpdateEvent.isMoveEvent())

                                .subscribe(voiceStateUpdateEvent -> event.getGuild()
                                    .flatMap(guild2 -> guild.getMemberById(Snowflake.of(insertUserId))
                                        .flatMap(Member::getVoiceState))

                                    .flatMap(VoiceState::getChannel)
                                    .flatMap(VoiceChannel::join)
                                    .subscribe());

                            return Mono.empty();
                        }))
            .then();
    }
}
