package ui.flat.component.menu;

import ui.flat.settings.BorderDimensions;
import ui.flat.settings.FlatColorPalette;
import ui.flat.settings.FlatComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FlatMenuItem extends JMenuItem {
    public FlatMenuItem(String name, ActionListener onClick) {
        super(name);
        new FlatComponentListener(this, FlatColorPalette.DEFAULT_PALETTE, new BorderDimensions(0,0,0,0),
                new BorderDimensions(4,4,4,4));
        this.addActionListener(onClick);
    }

    public FlatMenuItem setPreferredWidth(int width) {
        this.setPreferredSize(new Dimension(200, this.getPreferredSize().height));
        return this;
    }

    public FlatMenuItem setShortcut(KeyStroke keyStroke) {
        super.setAccelerator(keyStroke);
        return this;
    }
}
