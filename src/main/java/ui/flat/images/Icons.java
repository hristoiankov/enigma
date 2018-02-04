package ui.flat.images;

import ui.flat.settings.FlatColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * This class defines programmatically generated icons
 */
public class Icons {
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;
    private static final int ICON_MARGIN = 4;
    public static final ImageIcon CLOSE_ICON_DARK_GRAY_LIGHT_GRAY =
            Icons.createCloseIcon(FlatColorPalette.DARK_GRAY, FlatColorPalette.LIGHT_GRAY);
    public static final ImageIcon CLOSE_ICON_LIGHT_GRAY_DARK_GRAY =
            Icons.createCloseIcon(FlatColorPalette.LIGHT_GRAY, FlatColorPalette.DARK_GRAY);
    public static final ImageIcon CLOSE_ICON_BLACK_LIGHT_GRAY =
            Icons.createCloseIcon(FlatColorPalette.BLACK, FlatColorPalette.LIGHT_GRAY);
    public static final ImageIcon CLOSE_ICON_RED_LIGHT_GRAY =
            Icons.createCloseIcon(FlatColorPalette.RED, FlatColorPalette.LIGHT_GRAY);
    public static final ImageIcon MAXIMIZE_ICON_DARK_GRAY_LIGHT_GRAY =
            Icons.createMaximizeIcon(FlatColorPalette.DARK_GRAY, FlatColorPalette.LIGHT_GRAY);
    public static final ImageIcon MAXIMIZE_ICON_LIGHT_GRAY_DARK_GRAY =
            Icons.createMaximizeIcon(FlatColorPalette.LIGHT_GRAY, FlatColorPalette.DARK_GRAY);
    public static final ImageIcon MINIMIZE_ICON_DARK_GRAY_LIGHT_GRAY =
            Icons.createMinimizeIcon(FlatColorPalette.DARK_GRAY, FlatColorPalette.LIGHT_GRAY);
    public static final ImageIcon MINIMIZE_ICON_LIGHT_GRAY_DARK_GRAY =
            Icons.createMinimizeIcon(FlatColorPalette.LIGHT_GRAY, FlatColorPalette.DARK_GRAY);

    /**
     * Create close icon
     *
     * @return
     */
    public static ImageIcon createCloseIcon(final Color backgroundColor, final Color foregroundColor) {
        BufferedImage image = new BufferedImage(Icons.ICON_WIDTH, Icons.ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fill(new Rectangle2D.Float(0, 0, Icons.ICON_WIDTH, Icons.ICON_HEIGHT));
        // draw the X
        g.setColor(foregroundColor);
        g.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_MARGIN, Icons.ICON_WIDTH - Icons.ICON_MARGIN,
                Icons.ICON_HEIGHT - Icons.ICON_MARGIN);
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN,
                Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_MARGIN);

        return new ImageIcon(image);
    }

    /**
     * Create maximize icon
     *
     * @return
     */
    public static ImageIcon createMaximizeIcon(final Color backgroundColor, final Color foregroundColor) {
        BufferedImage image = new BufferedImage(Icons.ICON_WIDTH, Icons.ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fill(new Rectangle2D.Float(0, 0, Icons.ICON_WIDTH, Icons.ICON_HEIGHT));
        // draw the X
        g.setColor(foregroundColor);
        g.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_MARGIN,
                Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_MARGIN);
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_MARGIN,
                Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN);
        g.drawLine(Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_MARGIN,
                Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN);
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN,
                Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN);

        return new ImageIcon(image);
    }

    /**
     * Create minimize icon
     *
     * @return
     */
    public static ImageIcon createMinimizeIcon(final Color backgroundColor, final Color foregroundColor) {
        BufferedImage image = new BufferedImage(Icons.ICON_WIDTH, Icons.ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fill(new Rectangle2D.Float(0, 0, Icons.ICON_WIDTH, Icons.ICON_HEIGHT));
        // draw the X
        g.setColor(foregroundColor);
        g.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN,
                Icons.ICON_WIDTH - Icons.ICON_MARGIN, Icons.ICON_HEIGHT - Icons.ICON_MARGIN);

        return new ImageIcon(image);
    }
}
