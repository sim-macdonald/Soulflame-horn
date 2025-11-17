package soulflamehorn;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.GeneralPath;

@Slf4j
@Setter
public class SoulflameHornOverlay extends OverlayPanel {

    private final SoulflameHornPlugin plugin;
    private final SoulflameHornConfig config;
    private final Client client;

    @Inject
    private SoulflameHornOverlay(Client client, SoulflameHornPlugin plugin, SoulflameHornConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        int overlayRendered = renderOverlay(graphics);
        int radiusRendered = renderRadius(graphics, plugin.getRadius());
        if (overlayRendered == -1 && radiusRendered == -1)
        {
            return null;
        }
        return super.render(graphics);
    }

    private int renderOverlay(Graphics2D graphics)
    {
        if (!config.displayPanel() || (!config.alwaysShowPanel() && !plugin.isEnticeBuffActive()))
        {
            return -1;
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

        return 0;
    }

    private int renderRadius(Graphics2D graphics, int radius)
    {
        if (!plugin.isSoulflameHornEquipped() || !config.showHornRadiusOutline() || radius <= 0 || client.getLocalPlayer() == null)
        {
            return -1;
        }
        WorldPoint playerLocation = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

        graphics.setStroke(new BasicStroke((float) config.hornRadiusBorderWidth()));
        graphics.setColor(config.hornRadiusBorderColor());

        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        final int startX = playerLocation.getX() - radius;
        final int startY = playerLocation.getY() - radius;
        final int z = playerLocation.getPlane();

        Point initialPoint = coordinatesToPoint(startX, startY, z);
        if (initialPoint == null)
        {
            return -1;
        }
        path.moveTo(initialPoint.getX(), initialPoint.getY());
        lineTo(path, startX + 1 + radius * 2, startY, z);
        lineTo(path, startX + 1 + radius * 2, startY + 1 + radius * 2, z);
        lineTo(path, startX, startY + 1 + radius * 2, z);
        path.closePath();

        graphics.draw(path);
        return 0;
    }

    private void lineTo(GeneralPath path, int x, int y, int z)
    {
        Point point = coordinatesToPoint(x, y, z);
        if (point != null)
        {
            path.lineTo(point.getX(), point.getY());
        }
    }

    private Point coordinatesToPoint(int x, int y, int z)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client.getTopLevelWorldView(), x, y);

        if (localPoint == null)
        {
            log.warn("Cannot render radius overlay: localPoint is null for worldPoint=({}, {}, {})",
                    x, y, z);
            return null;
        }
        return Perspective.localToCanvas(
                client,
                new LocalPoint(localPoint.getX() - Perspective.LOCAL_TILE_SIZE / 2,
                        localPoint.getY() - Perspective.LOCAL_TILE_SIZE / 2,
                        client.getTopLevelWorldView()),
                z
        );
    }
}
