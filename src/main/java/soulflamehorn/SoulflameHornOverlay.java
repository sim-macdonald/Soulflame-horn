package soulflamehorn;

import lombok.Setter;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

@Setter
public class SoulflameHornOverlay extends OverlayPanel {
    private SoulflameHornPlugin plugin;
    private SoulflameHornConfig config;

    @Inject
    private SoulflameHornOverlay(SoulflameHornPlugin plugin, SoulflameHornConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.displayPanel() || (!config.alwaysShowPanel() && !plugin.isEnticeBuffActive()))
        {
            return null;
        }
        Color colour = config.messageColour();

        graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));

        panelComponent.getChildren().clear();

        // Soulflame Horn Buff Status
        panelComponent.getChildren().add(LineComponent.builder().left("Soulflame Horn").right(plugin.isEnticeBuffActive() ? String.valueOf(plugin.getEnticeBuffTicks()) : "").leftColor(colour).rightColor(colour).build());

        // Additional Info
        if (config.displayHornRadius())
        {
            panelComponent.getChildren().add(LineComponent.builder().left("Radius").right(String.valueOf(plugin.getRadius())).leftColor(colour).rightColor(colour).build());
        }

        if (config.displayMaxPlayers())
        {
            panelComponent.getChildren().add(LineComponent.builder().left("Max Players").right(String.valueOf(plugin.getMaxPlayers())).leftColor(colour).rightColor(colour).build());
        }

        return super.render(graphics);
    }
}
