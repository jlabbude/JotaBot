package com.github.jlabbude.jotabot;

import com.github.jlabbude.jotabot.command.ChatCommandListener;
import com.github.jlabbude.jotabot.command.CommandManager;
import com.github.jlabbude.jotabot.command.commands.JotaJoin;
import com.github.jlabbude.jotabot.command.commands.JotaPersistentJoin;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;

public class JotaBot {

    //For my application/intention with this bot, it's better to hardcode
    //all of this stuff.
    public static final long insertUserId = 0L;
    public static final long insertGuildId = 0L;
    public static final long insertMsgChId = 0L;

    public static void main(String[] args) {

        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
            .login()
            .block();

        //stupid fix, but I don't want to mess with this bot any longer so this'll do anyway

        client.getMemberById(Snowflake.of(insertGuildId), Snowflake.of(insertUserId))
            .flatMap(Member::asFullMember).flatMap(member -> member.getVoiceState())
            .flatMap(VoiceState::getChannel)
            .flatMap(VoiceChannel::join)
            .subscribe();

        client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
            .flatMap(event -> new JotaPersistentJoin()
                .execute("join", new MessageCreateEvent(client, null, null, insertGuildId, null)))
            .subscribe();

        client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
            .flatMap(event -> new JotaJoin()
                    .execute("jotajoin", new MessageCreateEvent(client, null, null, insertGuildId, null)))
            .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
            .filter(event -> event.getMessage().getContent().startsWith("&"))
            .flatMap(event -> ChatCommandListener.execute(event, new CommandManager()))
            .subscribe();

        client.onDisconnect().block();
    }
}
