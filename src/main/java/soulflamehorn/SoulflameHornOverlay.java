package soulflamehorn;

import lombok.Setter;
import net.runelite.api.Client;
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
        if (!plugin.isEnticeBuffActive() || !config.enableOverlay()) {
            return null;
        }
        Color colour = config.messageColour();

        graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(LineComponent.builder().left("Soulflame Horn").right(plugin.getEnticeBuffTicks() + "").leftColor(colour).rightColor(colour).build());

        return super.render(graphics);
    }
}
