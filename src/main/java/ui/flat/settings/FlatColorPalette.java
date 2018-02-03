package ui.flat.settings;

import java.awt.*;

/**
 * FlatColorPalette
 */
public class FlatColorPalette {
    public static final LockedFlatColorPalette DEFAULT_PALETTE = new LockedFlatColorPalette();

    private Color backgroundColor;
    private Color foregroundColor;
    private Color borderColor;
    private Color hoverBackgroundColor;
    private Color hoverForegroundColor;
    private Color hoverBorderColor;

    public FlatColorPalette() {
        this.backgroundColor = new Color(60,60,60);
        this.foregroundColor = new Color(200,200,200);
        this.borderColor = new Color(60,60,60);
        this.hoverBackgroundColor = new Color(200,200,200);
        this.hoverForegroundColor = new Color(60,60,60);
        this.hoverBorderColor = new Color(200,200,200);
    }

    public FlatColorPalette clone() {
        FlatColorPalette clone = new FlatColorPalette();
        clone.backgroundColor = this.backgroundColor;
        clone.foregroundColor = this.foregroundColor;
        clone.borderColor = this.borderColor;
        clone.hoverBackgroundColor = this.hoverBackgroundColor;
        clone.hoverForegroundColor = this.hoverForegroundColor;
        clone.hoverBorderColor = this.hoverBorderColor;
        return clone;
    }

    public static class LockedFlatColorPalette extends FlatColorPalette {

        @Override
        public FlatColorPalette setBackgroundColor(Color color) {
            return this;
        }

        @Override
        public FlatColorPalette setForegroundColor(Color color) {
            return this;
        }

        @Override
        public FlatColorPalette setBorderColor(Color color) {
            return this;
        }

        @Override
        public FlatColorPalette setHoverBackgroundColor(Color color) {
            return this;
        }

        @Override
        public FlatColorPalette setHoverForegroundColor(Color color) {
            return this;
        }

        @Override
        public FlatColorPalette setHoverBorderColor(Color color) {
            return this;
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public FlatColorPalette setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public FlatColorPalette setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public FlatColorPalette setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public FlatColorPalette setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
        return this;
    }

    public Color getHoverForegroundColor() {
        return hoverForegroundColor;
    }

    public FlatColorPalette setHoverForegroundColor(Color hoverForegroundColor) {
        this.hoverForegroundColor = hoverForegroundColor;
        return this;
    }

    public Color getHoverBorderColor() {
        return hoverBorderColor;
    }

    public FlatColorPalette setHoverBorderColor(Color hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
        return this;
    }
}
