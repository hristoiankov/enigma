package ui.flat;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Hristo on 5/1/2016.
 */
public class FlatScrollBar extends JScrollBar {

    FlatScrollBarUI scrollbarUI;

    /**
     * Create a flat scroll bar
     *
     * @param orientation
     *  JScrollBar.HORIZONTAL
     *  JScrollBar.VERTICAL
     */
    public FlatScrollBar(int orientation) {
        super(orientation);
        scrollbarUI = new FlatScrollBarUI(orientation);
        this.setUI(scrollbarUI);
    }

    /**
     * Set a marker on the scroll bar at the provided location
     * @param position 0 - 1.0 (0% - 100% length)
     * @param color
     */
    public void addMarker(float position, final Color color, int category) {
        scrollbarUI.addMarker(
                new FlatScrollBarUI.ScrollbarMarker(position, color, category));
    }

    /**
     * Clear all markers of a given category
     * @param category
     */
    public void clearMarkers(int category) {
        scrollbarUI.clearMarkers(category);
    }

    /**
     * Clear all markers from the scrollbar
     */
    public void clearAllMarkers() {
        scrollbarUI.clearAllMarkers();
    }

}
