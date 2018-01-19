package ui.flat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FlatScrollBarUI extends BasicScrollBarUI{
	
	private Color trackColor;
	private Color thumbColor;
    private List<ScrollbarMarker> markers;
    private int orientation;

	public FlatScrollBarUI(int orientation) {
		this(orientation, new Color(0,0,0), new Color(60,60,60));
	}
	
	public FlatScrollBarUI(int orientation, Color trackColor, Color thumbColor) {
		super();
		this.trackColor = trackColor;
		this.thumbColor = thumbColor;
        this.orientation = orientation;
        markers = new ArrayList<>();
	}

    static public class ScrollbarMarker {
        private float position;
        private Color color;
        private int category;
        private int width;

        public ScrollbarMarker(float position, final Color color, int category) {
            this.position = position;
            this.color = color;
            this.category = category;
            this.width = 2;
        }

    }

    /**
     * Add a marker to the scroll bar at the provided location
     * @param marker
     */
    public void addMarker(ScrollbarMarker marker) {
        if(marker == null) return;
        markers.add(marker);
    }

    /**
     * Clear all markers of a given category
     * @param category
     */
    public void clearMarkers(int category) {
        markers = markers.stream()
                .filter(marker -> marker.category != category)
                .collect(Collectors.toList());
    }

    /**
     * Clear all markers from the scrollbar
     */
    public void clearAllMarkers() {
        markers.clear();
    }

    /**
     * Draw the provided marker onto the scrollbar
     * @param g
     * @param marker
     */
    private void drawMarker(Graphics g, ScrollbarMarker marker, Rectangle trackBounds) {
        if(this.orientation == JScrollBar.HORIZONTAL) {
            g.setColor(marker.color);
            int x = Math.round(trackBounds.width * marker.position);
            if(x + marker.width > trackBounds.width) x = x - marker.width;
            g.fillRect(x, 0, marker.width, trackBounds.height);
        } else {
            g.setColor(marker.color);
            int y = Math.round(trackBounds.height * marker.position);
            if(y + marker.width > trackBounds.height) y = y - marker.width;
            g.fillRect(0, y, trackBounds.width, marker.width);
        }
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.translate(trackBounds.x, trackBounds.y);
    	g.setColor(trackColor);
        g.fillRect(0, 0, trackBounds.width, trackBounds.height);
        g.translate(trackBounds.x, trackBounds.y);

        markers.stream().forEach(marker -> {
            drawMarker(g, marker, trackBounds);
        });
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(thumbColor);
        g.fillRect(2, 2, thumbBounds.width - 4, thumbBounds.height-4);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
    	return createEmptyButton();
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
    	return createEmptyButton();
    }

    private static JButton createEmptyButton() {
    	JButton emptyButton = new JButton();
        Dimension zeroDim = new Dimension(0,0);
        emptyButton.setPreferredSize(zeroDim);
        emptyButton.setMinimumSize(zeroDim);
        emptyButton.setMaximumSize(zeroDim);
        return emptyButton;
    }
}
