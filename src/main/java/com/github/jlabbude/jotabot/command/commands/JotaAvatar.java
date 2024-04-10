package com.github.jlabbude.jotabot.command.commands;

import com.github.jlabbude.jotabot.JotaBot;
import com.github.jlabbude.jotabot.command.ChatCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Image;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.github.jlabbude.jotabot.JotaBot.insertUserId;

public class JotaAvatar implements ChatCommand {

    @Override
    public Mono<Void> execute(String commandName, MessageCreateEvent event) {
        return event.getGuild()
            .flatMap(guild -> guild.getMemberById(Snowflake.of(insertUserId))
            .map(member ->
                member.getAvatar()
                    .subscribe(image -> {
                        BufferedImage bufferedImage;
                        BufferedImage bufferedNoSign;
                        try {
                            bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getData()));
                            InputStream inputStream = JotaBot.class.getResourceAsStream("/image/nosign.png");
                            assert inputStream != null;
                            bufferedNoSign = ImageIO.read(inputStream);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        BufferedImage combined = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics g = combined.getGraphics();
                        g.drawImage(bufferedImage, 0, 0, null);
                        g.drawImage(bufferedNoSign, 0, 0, null);
                        g.dispose();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ImageIO.write(combined, "png", baos);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        guild.getClient().edit().withAvatar(Image.ofRaw(baos.toByteArray(), Image.Format.PNG)).subscribe();
                    })))
            .then();
    }
}
