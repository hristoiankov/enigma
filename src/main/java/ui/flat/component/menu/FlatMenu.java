package ui.flat.component.menu;

import ui.flat.settings.BorderDimensions;
import ui.flat.settings.FlatColorPalette;
import ui.flat.settings.FlatComponentListener;

import javax.swing.*;
import java.awt.*;

public class FlatMenu extends JMenu {
    public FlatMenu(String name) {
        super(name);
        new FlatComponentListener(this, FlatColorPalette.DEFAULT_PALETTE, new BorderDimensions(0,0,0,0),
                new BorderDimensions(4,4,4,4));
    }

    /**
     * Add Component to the menu
     *
     * @param item
     * @return
     */
    @Override
    public FlatMenu add(Component item) {
        super.add(item);
        return this;
    }

    /**
     * Add JMenuItem to the menu
     *
     * @param item
     * @return
     */
    @Override
    public FlatMenu add(JMenuItem item) {
        super.add(item);
        return this;
    }

    /**
     *
     * @param defaultIcon
     * @return
     */
    public FlatMenu setMenuIcon(javax.swing.Icon defaultIcon) {
        super.setIcon(defaultIcon);
        return this;
    }

}
