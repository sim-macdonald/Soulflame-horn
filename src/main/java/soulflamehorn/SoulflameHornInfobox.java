package soulflamehorn;

import net.runelite.client.ui.overlay.infobox.InfoBox;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class SoulflameHornInfobox extends InfoBox
{
    private final SoulflameHornPlugin plugin;
    private final SoulflameHornConfig config;

    @Inject
    SoulflameHornInfobox(BufferedImage image, SoulflameHornPlugin plugin, SoulflameHornConfig config)
    {
        super(image, plugin);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean render()
    {
        return config.displayInfobox() && (plugin.isEnticeBuffActive() || plugin.isSoulflameHornEquipped());
    }

    @Override
    public String getTooltip()
    {
        return "Soulflame Horn Buff";
    }

    @Override
    public String getText()
    {
        if (plugin.isEnticeBuffActive())
        {
            return String.valueOf(plugin.getEnticeBuffTicks());
        }
        else
        {
            return "";
        }
    }

    @Override
    public Color getTextColor()
    {
        return Color.GREEN;
    }
}
