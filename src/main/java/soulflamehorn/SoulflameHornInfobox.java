package soulflamehorn;

import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ColorUtil;

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
        String buff = String.format("Soulflame Horn Buff: %s",
                plugin.isEnticeBuffActive()
                        ? ColorUtil.wrapWithColorTag("Active (" + plugin.getEnticeBuffTicks() + " ticks left)", Color.GREEN)
                        : ColorUtil.wrapWithColorTag("Inactive", Color.RED)
        );

        String rangeAndPlayers = String.format("</br>Range: %d</br>Max Players: %d",
                plugin.getRadius(),
                plugin.getMaxPlayers()
        );

        return String.format("%s%s", buff, plugin.isSoulflameHornEquipped() ? rangeAndPlayers : "");
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
