package ui.flat.settings;

import ui.flat.settings.BorderDimensions;
import ui.flat.settings.FlatColorPalette;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Flat Component Listener
 * This class makes sure the JComponent is drawn with the chosen colors
 *
 */
public class FlatComponentListener implements MouseListener {
    private JComponent component;
    private FlatColorPalette palette;
    private BorderDimensions borderDimensions;
    private BorderDimensions paddingDimensions;
    private Border defaultBorder;
    private Border hoverBorder;

    /**
     * Passed in JComponents will automatically have this class set as their mouse listener
     *
     * @param component
     * @param palette
     * @param borderDimensions
     * @param paddingDimensions
     */
    public FlatComponentListener(JComponent component, FlatColorPalette palette,
                                 BorderDimensions borderDimensions, BorderDimensions paddingDimensions) {
        this.component = component;
        this.palette = palette;
        this.borderDimensions = borderDimensions;
        this.paddingDimensions = paddingDimensions;
        initBorders();
        component.addMouseListener(this);
        component.setOpaque(true);
        initComponent();
    }

    private void initBorders() {
        Border border = BorderFactory.createMatteBorder(
                borderDimensions.getTop(), borderDimensions.getLeft(),
                borderDimensions.getBottom(), borderDimensions.getRight(), palette.getBorderColor());
        Border highlightBorder = BorderFactory.createMatteBorder(
                borderDimensions.getTop(), borderDimensions.getLeft(),
                borderDimensions.getBottom(), borderDimensions.getRight(), palette.getBorderColor());
        Border padding = new EmptyBorder(paddingDimensions.getTop(), paddingDimensions.getLeft(),
                paddingDimensions.getBottom(), paddingDimensions.getRight());
        defaultBorder = new CompoundBorder(border, padding);
        hoverBorder = new CompoundBorder(highlightBorder, padding);
    }

    private void initComponent() {
        component.setBorder(defaultBorder);
        component.setBackground(palette.getBackgroundColor());
        component.setForeground(palette.getForegroundColor());
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        component.setBorder(hoverBorder);
        component.setBackground(palette.getHoverBackgroundColor());
        component.setForeground(palette.getHoverForegroundColor());
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        initComponent();
    }
    @Override
    public void mouseClicked(MouseEvent arg0) {}
    @Override
    public void mousePressed(MouseEvent arg0) {}
    @Override
    public void mouseReleased(MouseEvent arg0) {}
}
