package ui.flat.component;

import ui.flat.settings.FlatColorPalette;

import javax.swing.*;
import java.awt.*;

public class FlatPanel extends JPanel {
    public FlatPanel(FlatColorPalette palette) {
        this(new BorderLayout());
        this.setBackground(palette.getBackgroundColor());
        this.setForeground(palette.getForegroundColor());
    }

    public FlatPanel() {
        this(new BorderLayout());
    }

    public FlatPanel(LayoutManager layout) {
        super.setLayout(layout);
        this.setBackground(FlatColorPalette.DEFAULT_PALETTE.getBackgroundColor());
        this.setForeground(FlatColorPalette.DEFAULT_PALETTE.getForegroundColor());
    }

    public FlatPanel addTop(JComponent component) {
        super.add(component, BorderLayout.NORTH);
        return this;
    }
    public FlatPanel addBottom(JComponent component) {
        super.add(component, BorderLayout.SOUTH);
        return this;
    }
    public FlatPanel addLeft(JComponent component) {
        super.add(component, BorderLayout.WEST);
        return this;
    }
    public FlatPanel addRight(JComponent component) {
        super.add(component, BorderLayout.EAST);
        return this;
    }
    public FlatPanel addCenter(JComponent component) {
        super.add(component, BorderLayout.CENTER);
        return this;
    }
    public FlatPanel add(JComponent component) {
        super.add(component);
        return this;
    }
}
