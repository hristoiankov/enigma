package ui.flat.settings;

import java.awt.*;

/**
 * FlatColorPalette
 */
public class FlatColorPalette {
    public static final Color DARK_GRAY = new Color(60,60,60);
    public static final Color LIGHT_GRAY = new Color(200, 200, 200);
    public static final Color BLACK = new Color(0,0,0);
    public static final Color RED = new Color(255, 0, 0);
    public static final ImmutableFlatColorPalette DEFAULT_PALETTE = new ImmutableFlatColorPalette(
            FlatColorPalette.DARK_GRAY, FlatColorPalette.LIGHT_GRAY, FlatColorPalette.DARK_GRAY,
            FlatColorPalette.LIGHT_GRAY, FlatColorPalette.DARK_GRAY, FlatColorPalette.LIGHT_GRAY);

    protected Color backgroundColor;
    protected Color foregroundColor;
    protected Color borderColor;
    protected Color hoverBackgroundColor;
    protected Color hoverForegroundColor;
    protected Color hoverBorderColor;

    public FlatColorPalette() {
        this.backgroundColor = FlatColorPalette.DARK_GRAY;
        this.foregroundColor = FlatColorPalette.LIGHT_GRAY;
        this.borderColor = FlatColorPalette.DARK_GRAY;
        this.hoverBackgroundColor = FlatColorPalette.LIGHT_GRAY;
        this.hoverForegroundColor = FlatColorPalette.DARK_GRAY;
        this.hoverBorderColor = FlatColorPalette.LIGHT_GRAY;
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

    public static class ImmutableFlatColorPalette extends FlatColorPalette {
        public ImmutableFlatColorPalette(final Color backgroundColor, final Color foregroundColor,
                                         final Color borderColor, final Color hoverBackgroundColor,
                                         final Color hoverForegroundColor, final Color hoverBorderColor) {
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
            this.borderColor = borderColor;
            this.hoverBackgroundColor = hoverBackgroundColor;
            this.hoverForegroundColor = hoverForegroundColor;
            this.hoverBorderColor = hoverBorderColor;
        }

        @Override
        public FlatColorPalette setBackgroundColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
        }

        @Override
        public FlatColorPalette setForegroundColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
        }

        @Override
        public FlatColorPalette setBorderColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
        }

        @Override
        public FlatColorPalette setHoverBackgroundColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
        }

        @Override
        public FlatColorPalette setHoverForegroundColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
        }

        @Override
        public FlatColorPalette setHoverBorderColor(Color color) {
            throw new UnsupportedOperationException("Cannot modify immutable structure.");
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
