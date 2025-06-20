package soulflamehorn;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.RuneLite;

import javax.inject.Inject;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


import lombok.extern.slf4j.Slf4j;

import java.io.*;

@PluginDescriptor(
        name = "Soulflame Horn Buff",
        description = "Shows a message and overlay for the Soulflame Horn special attack. Also plays a sound.",
        tags = {"combat", "buff", "special"}
)

@Slf4j
public class SoulflameHornPlugin extends Plugin {

    @Inject
    private AudioPlayer audioPlayer;

    private boolean enticeBuff = false;
    @Getter
    private int enticeBuffTicks = 0;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SoulflameHornOverlay overlay;

    @Getter
    @Inject
    private SoulflameHornConfig config;

    @Provides
    SoulflameHornConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SoulflameHornConfig.class);
    }

    @Override
    protected void startUp()
    {
        if (config.enableOverlay())
        {
            overlayManager.add(overlay);
        }

        //create custom sound folder
        if (!SOUND_DIR.exists() && !SOUND_DIR.mkdirs())
        {
            log.warn("Failed to create sound directory at {}", SOUND_DIR.getAbsolutePath());
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        if (!(actor instanceof Player)) {
            return;
        }

        Player player = (Player) actor;

        if (player.equals(client.getLocalPlayer())) {
            return;
        }

        if (!config.enableBattlecry()) {
            return;
        }

        if (player.getAnimation() == 12158) {
            player.setOverheadText(config.specShout());
            player.setOverheadCycle(120);
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        String specMessage = event.getMessage().toLowerCase();

        if (specMessage.contains("encourages you with their soulflame horn") || (specMessage.contains("you encourage nearby allies, which also empowers your next melee")))
        {
           enticeBuff = true;
           //10 ticks = 6 seconds
           enticeBuffTicks = 10;

            playHornSound();
        }

        if (specMessage.contains("you encourage nearby allies, which also empowers your next melee") && config.enableBattlecry())
        {
            Player player = client.getLocalPlayer();
            if (player != null)
            {
                player.setOverheadText(config.specShout());
                player.setOverheadCycle(120);
            }

        }
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!enticeBuff) {
            return;
        }
        Skill skill = event.getSkill();

        // clear buff on melee shit, not sure how to make it work for defensive. someone smarter than me can figure out how to clear the buff.
        if (skill == Skill.ATTACK || skill == Skill.STRENGTH){
            enticeBuff = false;
            enticeBuffTicks = 0;
        }
    }

    //countdown
    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (enticeBuff)
        {
            enticeBuffTicks--;
            if (enticeBuffTicks <= 0)
            {
                enticeBuff = false;
            }
        }
    }

    public boolean isEnticeBuffActive()
    {
        return enticeBuff;
    }


    //directory for custom sound
    private static final File SOUND_DIR = new File(RuneLite.RUNELITE_DIR, "soulflamehorn");

    private void playHornSound()
    {
        if (!config.enableSound())
        {
            return;
        }

        String customSoundFileName = config.customHornSoundFilename().trim();
        File customSound = new File(SOUND_DIR, new File(customSoundFileName).getName());

        if (!customSoundFileName.isEmpty() && config.enableCustomSound())
        {
            try (InputStream stream = new BufferedInputStream(new FileInputStream(customSound)))
            {
                audioPlayer.play(stream, config.soundVolume());
            }
            catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
            {
                log.warn("Failed to play custom horn sound: {}", customSound.getAbsolutePath(), e);
            }
        }
        else {
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream("soulflamehorn/party-horn-68443.wav"))
            {
                if (stream == null) {
                    log.warn("Default horn sound not found");
                    return;
                }

                audioPlayer.play(stream, config.soundVolume());
            }
            catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
            {
                log.warn("Failed to play Horn sound", e);
            }
        }
    }

}