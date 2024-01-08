package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.SlashCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import org.apache.commons.lang3.time.StopWatch;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

public class jotaJoin implements SlashCommand {

    private final jotaStream streamCommand;
    private final long targetUserId;  // Replace with the actual ID of the target user

    public jotaJoin(jotaStream streamCommand, long targetUserId) {
        this.streamCommand = streamCommand;
        this.targetUserId = targetUserId;
    }

    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {
        AtomicReference<StopWatch> jotatimer = new AtomicReference<>();

        Snowflake targetChannelSnowflake = Snowflake.of(852717593236471832L);
        Mono<TextChannel> channelTarget = event.getClient().getChannelById(targetChannelSnowflake)
                .cast(TextChannel.class);

        return event.getGuild()
                .flatMap(guild -> guild.getMemberById(Snowflake.of(targetUserId)))
                .flatMap(Member::getVoiceState)
                .filter(vs -> !vs.isSelfStreaming())
                .switchIfEmpty(Mono.empty())
                .flatMap(VoiceState::getChannel)
                .flatMap(voiceChannel -> {
                    jotatimer.set(StopWatch.createStarted());
                    return voiceChannel.join();
                })
                .flatMap(voiceConnection -> {
                    event.getClient().getEventDispatcher().on(VoiceStateUpdateEvent.class)
                            .filter(voiceStateUpdateEvent ->
                                    voiceStateUpdateEvent.getCurrent().getUserId().equals(Snowflake.of(targetUserId)))
                            .subscribe(voiceStateUpdateEvent -> {
                                if (voiceStateUpdateEvent.getCurrent().getChannelId().isEmpty()) {
                                    voiceConnection.disconnect();
                                }
                            });

                    return streamCommand.execute("streamCommand", event)
                            .then(Mono.fromRunnable(() -> {
                                jotatimer.get().stop();

                                Duration elapsedDuration = Duration.ofMillis(jotatimer.get().getTime());

                                long hours = elapsedDuration.toHours();
                                long minutes = elapsedDuration.toMinutesPart();
                                long seconds = elapsedDuration.toSecondsPart();

                                String formattedElapsedTime = String.format("Jotave demorou %d horas, %d minutos, %d segundos para compartilhar a tela.",
                                        hours, minutes, seconds);

                                channelTarget.subscribe(textChannel ->
                                        textChannel.createMessage(formattedElapsedTime).subscribe());

                            }))
                            .then(voiceConnection.disconnect())
                            .then(Mono.defer(() -> execute("jotajoin", event)));
                })
                .repeatWhenEmpty(repeat -> repeat.delayElements(Duration.ofSeconds(1)));
    }
}