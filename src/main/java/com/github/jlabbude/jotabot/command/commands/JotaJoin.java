package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.command.ChatCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.apache.commons.lang3.time.StopWatch;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.jlabbude.jotabot.JotaBot.insertMsgChId;
import static com.github.jlabbude.jotabot.JotaBot.insertUserId;

public class JotaJoin implements ChatCommand {

    private final JotaStream streamCommand;

    public JotaJoin(JotaStream streamCommand) {
        this.streamCommand = streamCommand;
    }

    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {
        AtomicReference<StopWatch> jotatimer = new AtomicReference<>();

        Snowflake targetChannelSnowflake = Snowflake.of(insertMsgChId);

        Mono<TextChannel> channelTarget = event.getClient().getChannelById(targetChannelSnowflake).cast(TextChannel.class);

        return event.getGuild()
            .flatMap(guild -> guild.getMemberById(Snowflake.of(insertUserId)))
            .flatMap(Member::getVoiceState)
            .filter(vs -> !vs.isSelfStreaming())
            .switchIfEmpty(Mono.empty())
            .flatMap(VoiceState::getChannel)
            .flatMap(voiceChannel -> {
                jotatimer.set(StopWatch.createStarted());
                return voiceChannel.getVoiceConnection();
            })
            .flatMap(voiceConnection -> {
                event.getClient().getEventDispatcher().on(VoiceStateUpdateEvent.class)
                    .filter(voiceStateUpdateEvent ->
                    voiceStateUpdateEvent.getCurrent().getUserId().equals(Snowflake.of(insertUserId))
                    && (voiceStateUpdateEvent.isJoinEvent() || voiceStateUpdateEvent.isMoveEvent()))

                    .subscribe(voiceStateUpdateEvent -> event.getGuild()
                        .flatMap(guild -> guild.getMemberById(Snowflake.of(insertUserId))
                            .flatMap(Member::getVoiceState))
                        .flatMap(VoiceState::getChannel)
                        .flatMap(VoiceChannel::join)
                        .subscribe());

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
                    .then(Mono.defer(() -> execute("jotajoin", event)));
            })
            .repeatWhenEmpty(repeat -> repeat.delayElements(Duration.ofSeconds(1)));
    }
}