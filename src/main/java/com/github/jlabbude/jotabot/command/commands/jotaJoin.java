package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.SlashCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Mono;

public class jotaJoin implements SlashCommand {
    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {

        Message message = event.getMessage();

        return Mono.justOrEmpty(message.getUserMentions().stream().findFirst())
                .flatMap(user -> event.getGuild()
                        .flatMap(guild -> guild.getMemberById(user.getId()))
                        .flatMap(Member::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        .flatMap(VoiceChannel::join)
                        .then()
                )
                .then();
    }
}
