package ui.flat.component;

import ui.flat.settings.BorderDimensions;
import ui.flat.settings.FlatColorPalette;
import ui.flat.settings.FlatComponentListener;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FlatButton extends JButton {
    public FlatButton(String name, ActionListener onClick) {
        this(name, FlatColorPalette.DEFAULT_PALETTE, onClick);
    }
    public FlatButton(String name, FlatColorPalette palette, ActionListener onClick) {
        super(name);
        new FlatComponentListener(this, palette, new BorderDimensions(0,0,0,0),
                new BorderDimensions(4,4,4,4));
        this.addActionListener(onClick);
    }
}
