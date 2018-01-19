package ui.flat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import ui.flat.Flat.FlatButton;

public class FlatFrame extends JFrame implements 
	MouseListener, MouseMotionListener, ComponentListener{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Customizable Frame
	 * - titlebar
	 * - titlebar has menu & closeButton
	 * - draggable by titlebar
	 * - border
	 * - resizable by border
	 * - customizable colors
	 * 
	 * 
	 */
	protected FlatFrame window;
	protected JPanel contentPane;
	protected JPanel topPane;
	protected JPanel topRightPane;
	protected JMenuBar menuBar;
	protected JLabel windowTitle;
	protected Flat.FlatButton closeButton;
	protected Flat.FlatButton minimizeButton;
	
	protected Flat flat;
	
	protected Color frameBackgroundColor;
	protected Color frameForegroundColor;
	protected Color frameBorderColor;
	protected Color titleBarBackgroundColor;
	protected Color titleBarForegroundColor;
	
	protected int borderWidth = 2;
    private int cursorDirectionCurrent = Cursor.DEFAULT_CURSOR;
    private int cursorDirectionStart = Cursor.DEFAULT_CURSOR;
    private Point startPoint;
    

	//-----------------------------------------------------
	// Constructors
	//-----------------------------------------------------
    
    public FlatFrame(String title) {
    	this(title, new Color(60,60,60), new Color(200,200,200),
    				new Color(60,60,60));
    }
    
	public FlatFrame(String title, 
					 Color frameBackgroundColor, Color frameForegroundColor,
					 Color frameBorderColor) {
		super(title);
        this.setMinimumSize(new Dimension(100,100));
		this.frameBackgroundColor = frameBackgroundColor;
		this.frameForegroundColor = frameForegroundColor;
		this.frameBorderColor = frameBorderColor;
		startPoint = new Point();
		window = this;
		
		// set frame settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);

        // create instance of flat
        // optionally set the colors for the
        // instance of flat
        flat = new Flat();
		
		// create components
		createComponents();

		// attach components (top panel)
		topPane.add(this.menuBar, BorderLayout.WEST);
		topPane.add(this.windowTitle, BorderLayout.CENTER);
		topPane.add(this.topRightPane, BorderLayout.EAST);
		topRightPane.add(this.minimizeButton);
		topRightPane.add(this.closeButton);
		contentPane.add(this.topPane, BorderLayout.NORTH);
		this.setContentPane(this.contentPane);
		
		// attach events
		attachEvents();
		
		// set styling
		setStyling();
		
		System.setProperty("sun.awt.noerasebackground", "true");
		
		this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
	}
	
	private void createComponents() {
		this.contentPane = new JPanel(new BorderLayout());
		this.topPane = new JPanel(new BorderLayout());
		this.topRightPane = new JPanel();
		this.topRightPane.setLayout(new BoxLayout(topRightPane, BoxLayout.X_AXIS));
		this.menuBar = new JMenuBar();
		this.closeButton = new Flat.FlatButton("Close", this.getFlat().getPalette());
		this.minimizeButton = new Flat.FlatButton("Min", this.getFlat().getPalette());
		this.windowTitle = new JLabel(this.getTitle());
	}
	
	private void attachEvents() {
		closeButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	//window.closeApplication();
		    	window.dispose();
		    }
		});
		minimizeButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	//window.closeApplication();
		    	window.dispose();
		    	if(window.getExtendedState() == JFrame.ICONIFIED) {
		    		window.setExtendedState(JFrame.NORMAL);
		    	} else {
		    		window.setExtendedState(JFrame.ICONIFIED);
		    	}
		    }
		});
	}
	
	protected void setStyling() {
		this.setForeground(frameForegroundColor);
		this.setBackground(frameBackgroundColor);
	    topPane.setForeground(frameForegroundColor);
	    topPane.setBackground(frameBackgroundColor);
	    topRightPane.setBackground(frameBackgroundColor);
	    windowTitle.setForeground(frameForegroundColor);
		closeButton.setFocusPainted(false);
		minimizeButton.setFocusPainted(false);
		windowTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		// set panel border
		int borderWidth = 2; //2
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(
        		borderWidth, borderWidth, borderWidth, borderWidth, frameBorderColor));
	}
	
	public Flat getFlat() {
		return flat;
	}
	
	//-----------------------------------------------------
	// Event Handlers
	//-----------------------------------------------------
	
	@Override
	public void update(Graphics g) {
	    if (isShowing()) paint(g);
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

	//-----------------------------------------------------
	// Component Listener Events
	//-----------------------------------------------------

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		resizeWindow();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	//-----------------------------------------------------
	// Other Functions
	//-----------------------------------------------------
	
    public void resizeWindow() {
    	
    	// maintain the content pane width
		this.getContentPane().setSize(
				(int) this.getSize().getWidth(), 
				this.getContentPane().getHeight());
		
    }
    
    public void closeApplication() {
    	// kill the jframe
        window.dispose();
        System.exit(0);
    }

    
    
    
	
	
	
	
}
