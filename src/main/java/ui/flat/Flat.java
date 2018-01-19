package ui.flat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class Flat {
	
	//-----------------------------------------------------
  	// Flat Color Palette
  	//-----------------------------------------------------
	public FlatColorPalette palette;
	
	public Flat() {
		this.palette = new FlatColorPalette();
		palette.backgroundColor = new Color(60,60,60);
		palette.foregroundColor = new Color(200,200,200);
		palette.borderColor = new Color(60,60,60);
		palette.hoverBackgroundColor = new Color(200,200,200);
		palette.hoverForegroundColor = new Color(60,60,60);
		palette.hoverBorderColor = new Color(200,200,200);
	}
	
	public FlatColorPalette getPalette() {
		return this.palette;
	}
	
	public void setPalatte(FlatColorPalette palette) {
		this.palette = palette;
	}
	
	public static class FlatColorPalette {
		public Color backgroundColor;
		public Color foregroundColor;
		public Color borderColor;
		public Color hoverBackgroundColor;
		public Color hoverForegroundColor;
		public Color hoverBorderColor;
		
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
	}
	
	//-----------------------------------------------------
  	// Flat Components
  	//-----------------------------------------------------

	public static class FlatMenu extends JMenu {
		public FlatMenu(String name, FlatColorPalette palette) {
			super(name);
			new FlatComponentListener(this, palette);
			//this.setMenuLocation(this.getWidth(), this.getHeight());
		}
	}
	public static class FlatMenuItem extends JMenuItem {
		public FlatMenuItem(String name, FlatColorPalette palette) {
			super(name);
			new FlatComponentListener(this, palette, new BorderDimensions(4,4,4,4), 
					new BorderDimensions(0,0,0,0));
			//this.setPreferredSize(new Dimension(200,this.getPreferredSize().height));
		}
	}
	public static class FlatPanel extends JPanel {
		public FlatPanel(FlatColorPalette palette) {
			super();
			this.setBackground(palette.backgroundColor);
			this.setForeground(palette.foregroundColor);
		}
	}
	public static class FlatSeparator extends JSeparator {
		public FlatSeparator(FlatColorPalette palette) {
			super();
			this.setBackground(palette.backgroundColor);
			this.setForeground(palette.foregroundColor);
			//new FlatComponentListener(this, palette);
		}
	}
	public static class FlatTextField extends JTextField {
		public FlatTextField(FlatColorPalette palette) {
			super();
			this.setBackground(palette.backgroundColor);
			this.setForeground(palette.foregroundColor);
	        this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, palette.borderColor));
		    this.setCaretColor(palette.foregroundColor);
		}
	}
	public static class FlatButton extends JButton {
		public FlatButton(String name, FlatColorPalette palette) {
			super(name);
			new FlatComponentListener(this, palette, new BorderDimensions(2,2,2,2),
					new BorderDimensions(0,3,0,3));
		}
	}
	public static class FlatScrollPane extends JScrollPane {
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
	
	// border dimensions
	public static class BorderDimensions {
		int top, left, bottom, right;
		public BorderDimensions(int top, int left, int bottom, int right) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}
	}
	
	//-----------------------------------------------------
  	// Flat Component Listener
  	//-----------------------------------------------------
	
	public static class FlatComponentListener implements MouseListener{
		private JComponent component;
		private FlatColorPalette palette;
		private BorderDimensions borderDimensions;
		private BorderDimensions paddingDimensions;
		private Border defaultBorder;
		private Border hoverBorder;
		
		// top left bottom right
		
		// passed in components will automatically have
		// their listeners set to this class
		
		public FlatComponentListener(JComponent component, FlatColorPalette palette, 
				BorderDimensions borderDimensions, BorderDimensions paddingDimensions) {
			this.component = component;
			this.palette = palette;
			this.borderDimensions = borderDimensions;
			this.paddingDimensions = paddingDimensions;
			
			initBorders();
			
			component.addMouseListener(this);
			component.setOpaque(true);

			setDefaults();
		}
		
		private void initBorders() {
			Border border = BorderFactory.createMatteBorder(
					borderDimensions.top, borderDimensions.left, 
					borderDimensions.bottom, borderDimensions.right, palette.borderColor);
			Border highlightBorder = BorderFactory.createMatteBorder(
					borderDimensions.top, borderDimensions.left, 
					borderDimensions.bottom, borderDimensions.right, palette.borderColor);
			Border padding = new EmptyBorder(paddingDimensions.top, paddingDimensions.left, 
					paddingDimensions.bottom, paddingDimensions.right);
			defaultBorder = new CompoundBorder(border, padding);
			hoverBorder = new CompoundBorder(highlightBorder, padding);
		}
		
		public FlatComponentListener(JComponent component, FlatColorPalette palette) {
			this(component, palette, new BorderDimensions(2,2,2,2), new BorderDimensions(0,0,0,0));
		}
		
		public void setDefaults() {
			component.setBorder(defaultBorder);
			component.setBackground(palette.backgroundColor);
			component.setForeground(palette.foregroundColor);
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			component.setBorder(hoverBorder);
			component.setBackground(palette.hoverBackgroundColor);
			component.setForeground(palette.hoverForegroundColor);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			setDefaults();
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	
}
