package ui.flat.component;

import ui.flat.settings.FlatColorPalette;

import javax.swing.*;

public class FlatTextField extends JTextField {
    public FlatTextField(FlatColorPalette palette) {
        super();
        this.setBackground(palette.getBackgroundColor());
        this.setForeground(palette.getForegroundColor());
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, palette.getBorderColor()));
        this.setCaretColor(palette.getForegroundColor());
    }
}
