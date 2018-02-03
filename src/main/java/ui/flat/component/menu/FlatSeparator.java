package ui.flat.component.menu;

import ui.flat.settings.FlatColorPalette;

import javax.swing.*;

public class FlatSeparator extends JSeparator {
    public FlatSeparator(FlatColorPalette palette) {
        super();
        this.setBackground(palette.getBackgroundColor());
        this.setForeground(palette.getForegroundColor());
    }
}