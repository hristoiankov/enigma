package ui.flat.dialog;

import ui.flat.component.FlatButton;
import ui.flat.images.Icons;
import ui.flat.settings.FlatColorPalette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;

import javax.swing.*;

public class FlatDialog extends JDialog implements 
	MouseListener, MouseMotionListener {
	
	public static int YES_OPTION    = 0;
	public static int NO_OPTION     = 1;
	public static int CANCEL_OPTION = 2;
	public static int OK_OPTION     = 3;
	public static int CLOSED_OPTION = 4;

	protected int dialogResponse;
	
	protected FlatDialog window;
	protected JPanel contentPane;
	protected JPanel topPane;
	protected JMenuBar menuBar;
	protected JLabel windowTitle;
	protected FlatButton closeButton;

	
	protected Color frameBackgroundColor;
	protected Color frameForegroundColor;
	protected Color frameBorderColor;
	
	protected int borderWidth = 4;
    private int cursorDirectionCurrent = Cursor.DEFAULT_CURSOR;
    private int cursorDirectionStart = Cursor.DEFAULT_CURSOR;
    private Point startPoint;
	
	protected Component parent;
	protected WindowListener windowListener;

	/*
	Sample Simple Flat UI

	new FlatDialog("Title")
			.addCenter(new FlatTextArea()
					.setText("Content"))
			.addTop(new FlatMenuBar()
					.add(new FlatMenu("File")
							.add(new FlatMenuItem("New File", (ActionEvent) -> ...)))
					.add(new FlatMenu("Edit")
							.add(new FlatMenuItem("Copy", (ActionEvent) -> ...))
			.addBottom(new FlatPanel()
					.addLeft(new FlatButton("Okay", (ActionEvent) -> ...))
					.addRight(new FlatButton("Cancel", (ActionEvent) -> ...)))

	*/

	/**
	 *  Create a dialog with a simple text component
	 *
	 * @param parent
	 * @param title
	 * @param text
	 */
	public FlatDialog(JFrame parent, String title, String text) {
		this(parent, title);
    	JTextArea textArea = new JTextArea();
    	textArea.setText(text);
    	textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    	textArea.setEditable(false);
    	textArea.setForeground(frameForegroundColor);
    	textArea.setBackground(frameBackgroundColor);
    	textArea.setBorder(BorderFactory.createMatteBorder(
        		4,8,0,8,
        		frameBackgroundColor));
    	this.addCenter(textArea);
	}
    
	public FlatDialog(Frame parent, String title, 
			 Color frameBackgroundColor, Color frameForegroundColor,
			 Color frameBorderColor) {
		super(parent, true);
		this.setMinimumSize(new Dimension(100,100));
		this.frameBackgroundColor = frameBackgroundColor;
		this.frameForegroundColor = frameForegroundColor;
		this.frameBorderColor = frameBorderColor;
		startPoint = new Point();
		window = this;
		this.setTitle(title);
		
		// set frame settings
		this.setUndecorated(true);
		
		// create components
		createComponents();
		
		// attach components
		topPane.add(menuBar, BorderLayout.WEST);
		topPane.add(windowTitle, BorderLayout.CENTER);
		topPane.add(closeButton, BorderLayout.EAST);
		contentPane.add(topPane, BorderLayout.NORTH);
		this.setContentPane(contentPane);
		
		// set styling
		setStyling();
		
		System.setProperty("sun.awt.noerasebackground", "true");
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public FlatDialog(Frame parent, String title) {
		this(parent, title, FlatColorPalette.BLACK, FlatColorPalette.LIGHT_GRAY, FlatColorPalette.BLACK);
		this.parent = parent;
		this.setMinimumSize(new Dimension(200, 20));
	}
	
	public int showDialog() {
		dialogResponse = FlatDialog.CLOSED_OPTION;
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		return dialogResponse;
	}

	public FlatDialog addTop(JComponent component) {
		this.contentPane.add(component, BorderLayout.NORTH);
		this.pack();
		this.resizeToFitContent();
		return this;
	}
	public FlatDialog addBottom(JComponent component) {
		this.contentPane.add(component, BorderLayout.SOUTH);
		this.pack();
		this.resizeToFitContent();
		return this;
	}
	public FlatDialog addLeft(JComponent component) {
		this.contentPane.add(component, BorderLayout.WEST);
		this.pack();
		this.resizeToFitContent();
		return this;
	}
	public FlatDialog addRight(JComponent component) {
		this.contentPane.add(component, BorderLayout.EAST);
		this.pack();
		this.resizeToFitContent();
		return this;
	}
	public FlatDialog addCenter(JComponent component) {
		this.contentPane.add(component, BorderLayout.CENTER);
		this.pack();
		this.resizeToFitContent();
		return this;
	}

	private void resizeToFitContent() {
		this.setSize(new Dimension(
			this.getContentPane().getSize().width + this.borderWidth * 2,
			this.getContentPane().getSize().height + topPane.getHeight()
			));
	}

	private void createComponents() {
		contentPane = new JPanel(new BorderLayout());
		topPane = new JPanel(new BorderLayout());
		menuBar = new JMenuBar();
		FlatColorPalette buttonPalette = new FlatColorPalette()
				.setBackgroundColor(FlatColorPalette.BLACK)
				.setBorderColor(FlatColorPalette.BLACK)
				.setHoverBackgroundColor(FlatColorPalette.RED);
		closeButton = new FlatButton("", buttonPalette, e -> this.setVisible(false))
				.setIcon(Icons.CLOSE_ICON_BLACK_LIGHT_GRAY)
				.setRolloverIcon(Icons.CLOSE_ICON_RED_LIGHT_GRAY)
				.setPressedIcon(Icons.CLOSE_ICON_RED_LIGHT_GRAY);
		windowTitle = new JLabel(this.getTitle());
	}
	
	protected void setStyling() {
		this.setForeground(frameForegroundColor);
		this.setBackground(frameBackgroundColor);
	    topPane.setForeground(frameForegroundColor);
	    topPane.setBackground(frameBackgroundColor);
	    windowTitle.setForeground(frameForegroundColor);
		closeButton.setFocusPainted(false);
		windowTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		// set panel border
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(
        		borderWidth, borderWidth, borderWidth, borderWidth, frameBorderColor));
	}
	
	//-----------------------------------------------------
	// Mouse Motion Listener Events
	//-----------------------------------------------------

	@Override
    public void mousePressed(MouseEvent me) {
        cursorDirectionStart = cursorDirectionCurrent;
        startPoint = me.getPoint();
    }
	
    @Override
    public void mouseReleased(MouseEvent me) {
    }
	

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
        
        // trigger resize event
        if(cursorType != Cursor.DEFAULT_CURSOR) {
        	//resizeWindow();
        }
        
        // set the direction of the cursor
        cursorDirectionCurrent = cursorType;
        
        // set cursor to the chosen type
        setCursor(Cursor.getPredefinedCursor(cursorType));
    }
    

	//-----------------------------------------------------
	// Mouse Listener Events
	//-----------------------------------------------------

    @Override
    public void mouseExited(MouseEvent me) {
    	cursorDirectionCurrent = Cursor.DEFAULT_CURSOR;
        
        // set cursor to the chosen type
        setCursor(Cursor.getPredefinedCursor(cursorDirectionCurrent));
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
