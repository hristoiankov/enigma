package enigma.frontend.graphical;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.JComponent;

public class BinaryViewer extends JComponent implements 
	MouseListener, MouseWheelListener, MouseMotionListener,
	ComponentListener, KeyListener{

 	private byte[] fileData;
	private int minWidth = 1;
	private int pixelWidth = 16;
	private double zoom = 1.0;
	private Point offset;
	private Point initLocation;
	private BufferedImage image;
	private TextEditor parent;
	
	public BinaryViewer(byte[] fileData, TextEditor parent) {
		this.fileData = fileData;
		this.parent = parent;
		
		offset = new Point(0,0);
		initLocation = new Point(0,0);
		this.image = generateImage(this.fileData);
		
		this.setFocusable(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addComponentListener(this);
		this.addKeyListener(this);
	}
	
	private BufferedImage generateImage(byte[] fileData) {
		//int width = this.getWidth();
		int width = pixelWidth;
		if(width < minWidth) width = minWidth;
		if(width > fileData.length) width = fileData.length;
		int height = (fileData.length / width);
		if(width * height < fileData.length) height += 1;
		
		// generate padded byte data
		byte[] paddedData = new byte[width * height];
		System.arraycopy(fileData, 0, paddedData, 0, fileData.length);
		
		// generate the image
		BufferedImage image = new BufferedImage(
				width, height, BufferedImage.TYPE_BYTE_GRAY);
		image.getRaster().setDataElements(0, 0, width, height, paddedData);
		
		return image;
	}
	
	protected void paintComponent(Graphics g) {
		if(image == null) return;

		// determine the width and height of the drawn image
		// based on the zoom level
		int x = (int) (this.getWidth()/2.0 + offset.getX() - (image.getWidth() / 2) * zoom);
		int y = (int) (this.getHeight()/2.0 + offset.getY() - (image.getHeight() / 2) * zoom);
		int width = (int)(image.getWidth() * zoom);
		int height = (int)(image.getHeight() * zoom);
		g.drawImage(image, x, y, width, height, null);
		
	}
	
	public void centerImage() {
		
		// center the image to the screen based on the
		// dimensions of the image.  Also set the zoom
		// to fill the screen with the image.
		//zoom = 4.0;
		//int x = (int) ((image.getWidth()/2.0) * (zoom));
		//int y = (int) ((image.getHeight()/2.0) * (zoom));
		//offset.setLocation(x, y);
		offset.setLocation(0, 0);
		zoom = 16.0;
	}

	// ignore this since the mouse scroll is already 
	// captured by the scroll panel
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		double oldZoom = zoom;
		zoom *= Math.pow(2, -notches);
		
		/*int x = (int) ((image.getWidth()/2.0) * (oldZoom - zoom));
		int y = (int) ((image.getHeight()/2.0) * (oldZoom - zoom));
		offset.translate(x, y);*/
		
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		offset.translate((int)((e.getX() - initLocation.getX())),
						 (int)((e.getY() - initLocation.getY())));
		initLocation = e.getPoint();
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		initLocation = e.getPoint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		this.image = generateImage(this.fileData);
		this.repaint();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_RIGHT:
			pixelWidth += 1;
			parent.writeConsole("width: " + pixelWidth);
			this.image = generateImage(this.fileData);
			this.repaint();
			break;
		case KeyEvent.VK_LEFT:
			pixelWidth -= 1;
			if(pixelWidth < minWidth) pixelWidth = minWidth;
			parent.writeConsole("width: " + pixelWidth);
			this.image = generateImage(this.fileData);
			this.repaint();
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	
	
	
}










