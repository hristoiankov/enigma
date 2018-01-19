package enigma.frontend.graphical;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CustomizableFrame extends JFrame{
	/**
	 * Customizable Frame
	 * - titlebar
	 * - draggable by titlebar
	 * - border
	 * - resizable by border
	 * - customizable colors
	 * 
	 * 
	 */
	protected JFrame window;
	
	Color frameBackgroundColor;
	Color frameBorderColor;
	Color titleBarBackgroundColor;
	Color titleBarForegroundColor;
	
    int borderWidth = 2;
    
    int cursorDirectionCurrent = Cursor.DEFAULT_CURSOR;
    int cursorDirectionStart = Cursor.DEFAULT_CURSOR;
    Point startPoint;
    
	public CustomizableFrame(String title) {
		super(title);
		startPoint = new Point();
		window = this;
		
		System.setProperty("sun.awt.noerasebackground", "true");
		
		this.addMouseListener(new MouseAdapter() {
			@Override
            public void mousePressed(MouseEvent me) {
                super.mouseClicked(me);
                cursorDirectionStart = cursorDirectionCurrent;
                startPoint = me.getPoint();
            }
			
            @Override
            public void mouseReleased(MouseEvent me) {
                super.mouseReleased(me);
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            
            @Override
            public void mouseDragged(MouseEvent me) {
                int x = getX();
                int y = getY();
                int w = getWidth();
                int h = getHeight();

                int dx = me.getX() - startPoint.x;
                int dy = me.getY() - startPoint.y;
                
                int minWidth = window.getMinimumSize().width;
                int minHeight = window.getMinimumSize().height;
                
                switch (cursorDirectionStart) {
                    case Cursor.N_RESIZE_CURSOR:
                        if (!(h - dy < minHeight)) {
                            setBounds(x, y + dy, w, h - dy);
                        } break;
                    case Cursor.S_RESIZE_CURSOR:
                        if (!(h + dy < minHeight)) {
                            setBounds(x, y, w, h + dy);
                            startPoint = me.getPoint();
                        } break;
                    case Cursor.W_RESIZE_CURSOR:
                        if (!(w - dx < minWidth)) {
                            setBounds(x + dx, y, w - dx, h);
                        } break;
                    case Cursor.E_RESIZE_CURSOR:
                        if (!(w + dx < minWidth)) {
                            setBounds(x, y, w + dx, h);
                            startPoint = me.getPoint();
                        } break;
                    case Cursor.SW_RESIZE_CURSOR:
                        if (!(w - dx < minWidth) && !(h + dy < minHeight)) {
                            setBounds(x + dx, y, w - dx, h + dy);
                            startPoint = new Point(startPoint.x, me.getY());
                        } break;
                    case Cursor.SE_RESIZE_CURSOR:
                        if (!(w + dx < minWidth) && !(h + dy < minHeight)) {
                            setBounds(x, y, w + dx, h + dy);
                            startPoint = me.getPoint();
                        } break;
                    default:
                        Rectangle bounds = getBounds();
                        bounds.translate(dx, dy);
                        setBounds(bounds);
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                super.mouseMoved(me);
                int width = getWidth();
                int height = getHeight();
                
                int cursorType = Cursor.DEFAULT_CURSOR;
                if (me.getY() >= height - borderWidth && me.getX() >= width - borderWidth) {
                    cursorType = Cursor.SE_RESIZE_CURSOR;
                } else if (me.getY() >= height - borderWidth && me.getX() <= borderWidth) {
                    cursorType = Cursor.SW_RESIZE_CURSOR;
                } else if (me.getY() <= borderWidth) {
                    cursorType = Cursor.N_RESIZE_CURSOR;
                } else if (me.getY() >= height - borderWidth) {
                    cursorType = Cursor.S_RESIZE_CURSOR;
                } else if (me.getX() >= width - borderWidth) {
                    cursorType = Cursor.E_RESIZE_CURSOR;
                } else if (me.getX() <= borderWidth) {
                    cursorType = Cursor.W_RESIZE_CURSOR;
                } else {
                    cursorType = Cursor.DEFAULT_CURSOR;
                }
                
                // set the direction of the cursor
                cursorDirectionCurrent = cursorType;
                
                // set cursor to the chosen type
                setCursor(Cursor.getPredefinedCursor(cursorType));
            }
        });
	}
	
	@Override
	public void update(Graphics g) {
	    if (isShowing()) paint(g);
	}

    public void resizeWindow(int width, int height) {
    	//getContentPane().setBounds(0, 0, width, height);
    	this.setSize(width, height);
        revalidate();
    }
	
	
	
}
