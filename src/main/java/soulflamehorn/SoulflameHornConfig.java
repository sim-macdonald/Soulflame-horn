package soulflamehorn;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("soulflamehorn")
public interface SoulflameHornConfig extends Config {

    // General Settings Section

    @ConfigSection(
            name = "General Settings",
            description = "General settings.",
            position = 0
    )
    String generalSettingsSection = "generalSettings";

    @ConfigItem(
            keyName = "enableBattlecry",
            name = "Enable Battlecry",
            description = "Display a message over the character using the horn special attack.",
            section = generalSettingsSection,
            position = 0
    )
    default boolean enableBattlecry() { return true; }

    @ConfigItem(
            keyName = "specShout",
            name = "Battlecry",
            description = "The displayed message when battlecry is enabled.",
            section = generalSettingsSection,
            position = 1
    )
    default String battlecryMessage() {return "Tuturu!";}

    // Overlay Settings Section

    @ConfigSection(
            name = "Overlay",
            description = "Overlay settings.",
            position = 1
    )
    String overlaySettingsSection = "overlaySettings";

    @ConfigItem(
            keyName = "displayInfobox",
            name = "Display Buff Infobox",
            description = "Show the Soulflame Horn infobox.",
            section = overlaySettingsSection,
            position = 0
    )
    default boolean displayInfobox() { return true; }

    @ConfigItem(
            keyName = "enableOverlay",
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

    // Sound Settings Section

    @ConfigSection(
            name = "Sound Settings",
            description = "Sound settings.",
            position = 2
    )
    String soundSettingsSection = "soundSettings";

    @ConfigItem(
            keyName = "enableSound",
            name = "Enable Sound",
            description = "Play a sound when the special attack is used.",
            section = soundSettingsSection,
            position = 0
    )
    default boolean enableSound() { return false; }

    @ConfigItem(
            keyName = "soundVolume",
            name = "Volume",
            description = "Volume of the horn sound.",
            section = soundSettingsSection,
            position = 1
    )
    @Range(max = 200)
    default int soundVolume() { return 50; }

    @ConfigItem(
            keyName = "enableCustomSound",
            name = "Enable Custom Sound",
            description = "Play a custom horn sound. Enable Sound must also be on.",
            section = soundSettingsSection,
            position = 2
    )
    default boolean enableCustomSound() { return false; }

    @ConfigItem(
            keyName = "customHornSoundFilename",
            name = "Custom Horn Sound Filename",
            description = "Name of a .wav file to play (must be placed in ~/.runelite/soulflamehorn). Include the .wav in the name when entering, for example hornsound.wav (name is case sensitive)",
            section = soundSettingsSection,
            position = 3
    )
    default String customHornSoundFilename() { return ""; }

    @ConfigItem(
            keyName = "enableSoundOnFail",
            name = "Enable Sound On Fail",
            description = "Play the horn sound even if the special attack fails, allowing you to test it out without other players around.",
            section = soundSettingsSection,
            position = 4
    )
    default boolean enableSoundOnFail() { return false; }

    // Other Settings
}
