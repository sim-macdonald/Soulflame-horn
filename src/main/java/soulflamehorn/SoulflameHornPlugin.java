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

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

@PluginDescriptor(
        name = "Soulflame Horn Buff",
        description = "Shows a message and overlay for the Soulflame Horn special attack.",
        tags = {"combat", "buff", "special"}
)

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

        if (!config.enableBattlecry()) {
            return;
        }

        Player player = (Player) actor;


        if (player.getAnimation() == 12158)
        {
            if (player.equals(client.getLocalPlayer())) {


                ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
                if (equipment == null) {
                    return;
                }

                Item weaponSlot = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
                if (weaponSlot == null || weaponSlot.getId() != 30759) {
                    return;
                }

                client.addChatMessage(ChatMessageType.PUBLICCHAT, client.getLocalPlayer().getName(), config.specShout(), null);

                enticeBuff = true;

                //10 ticks = 6 seconds
                enticeBuffTicks = 10;

                //tuturu
                playHornSound();
            }
            else{
                player.setOverheadText(config.specShout());
                player.setOverheadCycle(120); //
                }
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        String specMessage = event.getMessage().toLowerCase();

        if (specMessage.contains("encourages you with their soulflame horn"))
        {
           enticeBuff = true;
           //10 ticks = 6 seconds
           enticeBuffTicks = 10;

           //tuturu
            playHornSound();
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
        try (InputStream audioSrc = getClass().getResourceAsStream("/soulflamehorn/Tuturu.wav"))
        {
            if (!config.enableSound()) {
                return;
            }

            if (audioSrc == null)
            {
                System.err.println("Soulflame horn sound not found.");
                return;
            }

            InputStream bufferedInput = new BufferedInputStream(audioSrc);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedInput);

            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);

            setVolume(clip, config.soundVolume());

            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //volume setting helper function
    private void setVolume(Clip clip, int volume)
    {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
            return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();

        float gain;
        if (volume == 0)
        {
            gain = min;
        }
        else
        {
            float percent = volume / 100f;
            gain = (float) (Math.log10(percent) * 20);
            gain = Math.max(min, Math.min(gain, max));
        }

        gainControl.setValue(gain);
    }

}
