package soulflamehorn;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("soulflamehorn")
public interface SoulflameHornConfig extends Config {

    @ConfigSection(
            name = "Overlay",
            description = "Overlay settings.",
            position = 0
    )
    String overlaySettingsSection = "overlaySettings";

    // Overlay Settings Section

    @ConfigItem(
            keyName = "displayInfobox",
            name = "Display Buff Infobox",
            description = "Show the Soulflame Horn infobox.",
            section = overlaySettingsSection,
            position = 0
    )
    default boolean displayInfobox() { return true; }

    @ConfigItem(
            keyName = "displayPanel",
            name = "Display Buff Panel",
            description = "Show the Soulflame Horn buff panel. Doesn't flag the buff as used if you are on defensive stance.",
            section = overlaySettingsSection,
            position = 1
    )
    default boolean displayPanel() { return false; }

    @ConfigItem(
            keyName = "colour",
            name = "Colour",
            description = "Colour of the text in the overlay.",
            section = overlaySettingsSection,
            position = 2
    )
    default Color messageColour() {return Color.GREEN;}

    @ConfigItem(
            keyName = "fontSize",
            name = "Font Size",
            description = "Size of the text in the overlay.",
            section = overlaySettingsSection,
            position = 3
    )
    default int fontSize() {return 16;}

    @ConfigItem(
            keyName = "alwaysShowPanel",
            name = "Always Show Panel",
            description = "Always show the buff panel, even when the buff is not active.",
            section = overlaySettingsSection,
            position = 4
    )
    default boolean alwaysShowPanel() { return false; }

    // Other Settings

    @ConfigItem(
            keyName = "enableBattlecry",
            name = "Enable Battlecry",
            description = "Display a message over the character using the horn special attack.",
            position = 3
    )
    default boolean enableBattlecry() { return true; }

    @ConfigItem(
            keyName = "battlecryMessage",
            name = "Battlecry",
            description = "The displayed message when battlecry is enabled.",
            position = 4
    )
    default String battlecryMessage() {return "Tuturu!";}

    @ConfigItem(
            keyName = "enableSound",
            name = "Enable Sound",
            description = "Play a sound when the special attack is used.",
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

    @ConfigItem(
            keyName = "enableCustomSound",
            name = "Enable Custom Sound",
            description = "Play a custom horn sound. Enable Sound must also be on.",
            position = 7
    )
    default boolean enableCustomSound() { return false; }

    @ConfigItem(
            keyName = "customHornSoundFilename",
            name = "Custom Horn Sound Filename",
            description = "Name of a .wav file to play (must be placed in ~/.runelite/soulflamehorn). Include the .wav in the name when entering, for example hornsound.wav (name is case sensitive)",
            position = 8
    )
    default String customHornSoundFilename() { return ""; }
}
