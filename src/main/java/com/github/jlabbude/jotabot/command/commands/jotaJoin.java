package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.SlashCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import org.apache.commons.lang3.time.StopWatch;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class jotaJoin implements SlashCommand {

    private final jotaStream streamCommand;

    public jotaJoin(jotaStream streamCommand) {
        this.streamCommand = streamCommand;
    }

    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {


        AtomicReference<StopWatch> jotatimer = new AtomicReference<>();

        Message message = event.getMessage();

        return Mono.justOrEmpty(message.getUserMentions().stream().findFirst())
                .flatMap(user -> event.getGuild()
                        .flatMap(guild -> guild.getMemberById(user.getId()))
                        .flatMap(Member::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        .flatMap(voiceChannel -> {
                            jotatimer.set(StopWatch.createStarted());
                            return voiceChannel.join();
                        })
                )
                .flatMap(voiceConnection -> streamCommand.execute("streamCommand", event)
                        .then(Mono.fromRunnable(() -> {

                            jotatimer.get().stop();

                            String elapsedString = jotatimer.get().toString();

                            message.getChannel()
                                .flatMap(channel -> channel.createMessage(elapsedString))
                                .subscribe();

                            System.out.println("Elapsed time: " + elapsedString);
                        }
                        )
                        )

                        .then(voiceConnection.disconnect())
                        .then()

                );
    }
}