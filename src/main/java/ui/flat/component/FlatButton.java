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

    /**
     * Set the default icon
     *
     * @param icon
     * @return
     */
    public FlatButton setIcon(final ImageIcon icon) {
        super.setIcon(icon);
        return this;
    }

    /**
     * Set the rollover icon
     *
     * @param icon
     * @return
     */
    public FlatButton setRolloverIcon(final ImageIcon icon) {
        super.setRolloverIcon(icon);
        return this;
    }

    /**
     * Set the pressed icon
     *
     * @param icon
     * @return
     */
    public FlatButton setPressedIcon(final ImageIcon icon) {
        super.setPressedIcon(icon);
        return this;
    }
}
