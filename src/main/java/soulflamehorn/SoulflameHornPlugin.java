package soulflamehorn;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.RuneLite;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.InventoryID;

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
public class SoulflameHornPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoboxManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SoulflameHornOverlay overlay;

    @Inject
    private SoulflameHornConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    //directory for sounds
    private static final File SOULFLAME_HORN_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "soulflamehorn");

    @Getter
    private int enticeBuffTicks = 0;

    @Getter
    private boolean enticeBuffActive = false;

    @Provides
    SoulflameHornConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SoulflameHornConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        
        SoulflameHornInfobox infobox = new SoulflameHornInfobox(itemManager.getImage(ItemID.SOULFLAME_HORN), this, config);
        infoboxManager.addInfoBox(infobox);

        //create custom sound folder
        if (!SOULFLAME_HORN_DIR.exists() && !SOULFLAME_HORN_DIR.mkdirs())
        {
            log.warn("Failed to create sound directory at {}", SOULFLAME_HORN_DIR.getAbsolutePath());
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }

    public boolean isSoulflameHornEquipped()
    {
        ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
        if (equipment == null)
        {
            return false;
        }

        Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weapon == null)
        {
            return false;
        }

        return weapon.getId() == ItemID.SOULFLAME_HORN;
    }

    public int getRadius()
    {
        return client.getVarbitValue(VarbitID.YAMA_HORN_RADIUS);
    }

    public int getMaxPlayers()
    {
        return client.getVarbitValue(VarbitID.YAMA_HORN_MAX_PLAYERS);
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

        if (player.getAnimation() == AnimationID.SOULFLAME_HORN_BLOW_03) {
            player.setOverheadText(config.battlecryMessage());
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
           enticeBuffActive = true;
           //10 ticks = 6 seconds
           enticeBuffTicks = 10;

            playHornSound();
        }

        if (specMessage.contains("you encourage nearby allies, which also empowers your next melee") && config.enableBattlecry())
        {
            Player player = client.getLocalPlayer();
            if (player != null)
            {
                player.setOverheadText(config.battlecryMessage());
                player.setOverheadCycle(120);
            }
        }

        if (specMessage.contains("you blow your horn into the wind. no-one nearby is able to listen") && config.enableSoundOnFail())
        {
            playHornSound();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!enticeBuffActive) {
            return;
        }
        Skill skill = event.getSkill();

        // clear buff on melee shit, not sure how to make it work for defensive. someone smarter than me can figure out how to clear the buff.
        if (skill == Skill.ATTACK || skill == Skill.STRENGTH){
            enticeBuffActive = false;
            enticeBuffTicks = 0;
        }
    }

    //countdown
    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (enticeBuffActive)
        {
            enticeBuffTicks--;
            if (enticeBuffTicks <= 0)
            {
                enticeBuffActive = false;
            }
        }
    }


    private void playHornSound()
    {
        if (!config.enableSound())
        {
            return;
        }

        String defaultSoundFileName = "party-horn-68443.wav";
        String customSoundFileName = config.customHornSoundFilename().trim();
        float gain = 20f * (float) Math.log10(config.soundVolume() / 100f);

        if (!customSoundFileName.isEmpty() && config.enableCustomSound()) {
            playSound(customSoundFileName, gain);
        } else {
            playSound(defaultSoundFileName, gain);
        }
    }
    private void playSound(String fileName, float gain)
    {
        try {
            File soundFile = new File(SOULFLAME_HORN_DIR, fileName);
            audioPlayer.play(soundFile, gain);
        }
        catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
        {
            log.warn("Failed to play sound: {}", fileName, e);
        }
    }
}