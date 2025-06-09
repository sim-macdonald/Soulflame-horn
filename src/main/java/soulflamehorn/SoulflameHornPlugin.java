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
import net.runelite.client.util.AudioPlayer;

import javax.inject.Inject;


import lombok.extern.slf4j.Slf4j;

@PluginDescriptor(
        name = "Soulflame Horn Buff",
        description = "Shows a message and overlay for the Soulflame Horn special attack.",
        tags = {"combat", "buff", "special"}
)

@Slf4j
public class SoulflameHornPlugin extends Plugin {

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


    private void playHornSound()
    {
        if (!config.enableSound()) {
            return;
        }

        try
        {
            AudioPlayer.playSound(getClass().getResource("/soulflamehorn/party-horn-68443.wav"), config.soundVolume());
        }
        catch (Exception e)
        {
            log.warn("Failed to play Horn sound", e);
        }
    }

}