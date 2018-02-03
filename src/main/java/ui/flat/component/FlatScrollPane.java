package ui.flat.component;

import ui.flat.component.scrollbar.FlatScrollBar;
import ui.flat.settings.FlatColorPalette;

import javax.swing.*;
import java.awt.*;

public class FlatScrollPane extends JScrollPane {
    public FlatScrollPane(final Component contentPane, final FlatColorPalette palette,
                          final FlatScrollBar verticalBar,
                          final FlatScrollBar horizontalBar) {
        super(contentPane);
        this.setVerticalScrollBar(verticalBar);
        this.setHorizontalScrollBar(horizontalBar);
        this.getVerticalScrollBar().setPreferredSize(new Dimension(10,0));
        this.getHorizontalScrollBar().setPreferredSize(new Dimension(0,10));
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.setBorder(null);
    }
}
