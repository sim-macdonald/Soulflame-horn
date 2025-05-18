package soulflamehorn;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("soulflamehorn")
public interface SoulflameHornConfig extends Config {

    @ConfigItem(
            keyName = "colour",
            name = "Colour",
            description = "Colour of the buff message text.",
            position = 1
    )
    default Color messageColour() {return Color.GREEN;}

    @ConfigItem(
            keyName = "fontSize",
            name = "Font Size",
            description = "Size of the text in the overlay.",
            position = 2
    )
    default int fontSize() {return 16;}

    @ConfigItem(
            keyName = "specShout",
            name = "Battlecry",
            description = "What you character says when you soulflame horn specs.",
            position = 4
    )
    default String specShout() {return "Tuturu!";}

    @ConfigItem(
            keyName = "enableOverlay",
            name = "Enable Buff Overlay",
            description = "Show the Soulflame Horn buff overlay. Doesn't flag the buff as used if you are on defensive stance.",
            position = 0
    )
    default boolean enableOverlay() { return true; }

    @ConfigItem(
            keyName = "enableBattlecry",
            name = "Enable Battlecry",
            description = "Whether your character says a message when using the horn specs.",
            position = 3
    )
    default boolean enableBattlecry() { return true; }

    @ConfigItem(
            keyName = "enableSound",
            name = "Enable Sound",
            description = "Plays a silly sound when the spec is used.",
            position = 5
    )
    default boolean enableSound() { return false; }

    @ConfigItem(
            keyName = "soundVolume",
            name = "Volume",
            description = "Volume of the horn sound.",
            position = 6
    )
    @Range(min = 0, max = 100)
    default int soundVolume() { return 50; }
}
