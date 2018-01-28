package enigma.frontend.graphical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import enigma.backend.EncryptionSystem;
import enigma.backend.SteganographySystem;
import ui.flat.*;
import ui.flat.Flat.FlatColorPalette;

/**
 * Enigma Text Editor
 * 
 * The text editor will open an encrypted file .aes
 * after requesting and validating the password, the editor
 * will hold the raw data of the file in memory.  Any
 * save or close operation afterward will ecrypt the data
 * again before saving the same file to the disk.
 * 
 * The text editor will remember
 * - the filename of the encrypted file
 * - the password until the file/application closes
 * - the raw data while it is open
 * 
 * make the parent a JWindow instead of a JFrame in order to 
 * allow custom titlebar drawing
 *
 */

public class TextEditor extends FlatFrame {
	
	private static String APP_TITLE = "Enigma Text Editor";
	private static float APP_VERSION = 0.14f;
	private static int APP_YEAR = 2016;
	private static int CONSOLE_TIMEOUT_SHORT = 3000;
	private static int CONSOLE_TIMEOUT_LONG = 8000;
	private int tabSpacing = 4;
	private JPanel contentPane2;
	private FindPanel findPane;
	private EditorPanel editorPane;
	private JScrollPane editorScrollPane;
	private FlatScrollBar editorVerticalScrollBar;
	private ConsolePanel consolePane;
	private JScrollPane consoleScrollPane;
	private FlatLineNumberHeader jList;
	private JFileChooser fileChooser;
	private BinaryViewer binaryViewer;
	
	// editor styling
	private Font editorFont;
	private Font lineNumberFont;
	private Font consoleFont;
	private Color editorBackgroundColor;
	private Color editorForegroundColor;
	private Color lineNumberBackgroundColor;
	private Color lineNumberForegroundColor;
	private Color selectionBackgroundColor;
	private Color selectionForegroundColor;

	// file filters
	FileNameExtensionFilter imageFileFilter;
	
	// open files
	private File openedFile;
	private File imageFile;
	private File encryptedFile;
	

	public TextEditor() {
		super(APP_TITLE,
			  new Color(60,60,60),
			  new Color(200,200,200),
			  new Color(60,60,60));
        
        // reference self
        window = this;
        
        this.setMinimumSize(new Dimension(200,200));
        this.setSize(new Dimension(600,600));
        this.setPreferredSize(new Dimension(600,600));
        
        // set frame icon
        ImageIcon icon = new ImageIcon(TextEditor.class.getResource("/enigma_64.png"));
        this.setIconImage(icon.getImage());
        
        // set file filters
        setFileFilters();
        
        // populate the window
		editorPane = new EditorPanel();
		jList = new FlatLineNumberHeader(editorPane);
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

		editorVerticalScrollBar = new FlatScrollBar(JScrollBar.VERTICAL);
		FlatScrollBar editorHorizontalScrollBar = new FlatScrollBar(JScrollBar.HORIZONTAL);
		editorScrollPane = new Flat.FlatScrollPane(editorPane,
				this.getFlat().getPalette(), editorVerticalScrollBar,
				editorHorizontalScrollBar);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		consolePane = new ConsolePanel();
		FlatScrollBar consoleVerticalScrollBar = new FlatScrollBar(JScrollBar.VERTICAL);
		FlatScrollBar consoleHorizontalScrollBar = new FlatScrollBar(JScrollBar.HORIZONTAL);
		consoleScrollPane = new Flat.FlatScrollPane(consolePane,
				this.getFlat().getPalette(), consoleVerticalScrollBar,
				consoleHorizontalScrollBar);
		consoleScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consoleScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		
		// Pane Ordering
		//
		//  FlatFrame
		//   contentPane
		//     topPane
		//     contentPane2
		//       findPane
		//       editorScrollPane
		//         editorPane
		//         jList
		//       consoleScrollPane
		//
		
		// create find panel
		FlatColorPalette findPanelPalette = this.getFlat().getPalette().clone();
		Color tempColor = findPanelPalette.foregroundColor;
		findPanelPalette.foregroundColor = findPanelPalette.backgroundColor;
		findPanelPalette.backgroundColor = tempColor;
		findPanelPalette.borderColor = tempColor;
		findPanelPalette.hoverForegroundColor = findPanelPalette.backgroundColor;
		findPanelPalette.hoverBackgroundColor = findPanelPalette.foregroundColor;
		this.findPane = new FindPanel(findPanelPalette, editorPane,
				editorVerticalScrollBar);
		
		//  content pane 2
		this.contentPane2 = new JPanel(new BorderLayout());
		
		// order the panes
		editorScrollPane.setRowHeaderView(jList);

		contentPane2.add(findPane, BorderLayout.NORTH);
		contentPane2.add(editorScrollPane, BorderLayout.CENTER);
		contentPane2.add(consoleScrollPane, BorderLayout.SOUTH);
		
		contentPane.add(contentPane2, BorderLayout.CENTER);
		
		// set editor styling
		//setLookAndFeel();
		setEditorStyling();
		setUiEvents();
		
		// populate the menu bar
		populateTopPane();
		
		// set component events
		setComponentEvents();
		
		// activate the application
		this.pack();
        this.setLocationRelativeTo(null);
		//this.setVisible(true);
        this.editorPane.grabFocus();
        
        // hide elements
		this.findPane.setVisible(false);
        
        writeConsole("Welcome to the " + APP_TITLE + ".\n" +
        			 "Version: " + APP_VERSION
        			 );
	}
	
	private void setFileFilters() {
		imageFileFilter = new FileNameExtensionFilter("Image Files", "png");
	}
	
	private void setComponentEvents() {

        consolePane.addFocusListener(consoleFocusListener);
	}
	
	private void setEditorStyling() {
		editorFont = new Font("Consolas", Font.PLAIN, 12);
		lineNumberFont = new Font("Consolas", Font.PLAIN, 12);
		consoleFont = new Font("Consolas", Font.PLAIN, 12);
		editorBackgroundColor = this.frameBackgroundColor;
		editorForegroundColor = this.frameForegroundColor;
		selectionBackgroundColor = new Color(51,204,255);
        
        menuBar.setBackground(editorBackgroundColor);
        menuBar.setForeground(editorForegroundColor);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, frameBorderColor));

		jList.setFont(lineNumberFont);

		editorPane.setContentType("text");
		editorPane.setEnabled(true);
	    editorPane.setFont(editorFont);
	    editorPane.setForeground(editorForegroundColor);
	    editorPane.setBackground(editorBackgroundColor);
	    editorPane.setCaretColor(Color.white);
	    editorPane.setSelectionColor(selectionBackgroundColor);
	    editorPane.setSelectedTextColor(selectionForegroundColor);
	    
	    consolePane.setForeground(editorForegroundColor);
	    consolePane.setBackground(editorBackgroundColor);
	    consolePane.setFont(consoleFont);
        consoleScrollPane.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, frameBorderColor));

	    
	    editorScrollPane.setBackground(Color.black);
	    
	    // set global styling
	    /*UIManager.put("Panel.opaque", true);
	    UIManager.put("Panel.background", editorBackgroundColor);
	    UIManager.put("Panel.foreground", editorForegroundColor);
	    UIManager.getLookAndFeelDefaults().put("Panel.background", editorBackgroundColor);*/
	    UIManager.put("Label.opaque", true);
	    UIManager.put("Label.background", editorBackgroundColor);
	    UIManager.put("Label.foreground", editorForegroundColor);
	    UIManager.put("Menu.opaque", true);
	    //UIManager.put("Menu.background", editorBackgroundColor);
	    //UIManager.put("Menu.foreground", editorForegroundColor);
		UIManager.put("Menu.selectionBackground", editorForegroundColor);
		UIManager.put("Menu.selectionForeground", editorBackgroundColor);
	    UIManager.put("PopupMenu.border", BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
	    //UIManager.put("MenuItem.border", BorderFactory.createMatteBorder(0, 0, 0, 0, panelBorderColor));
	    //UIManager.put("MenuItem.borderPainted", false);
	    //UIManager.put("MenuItem.background", editorBackgroundColor);
	    //UIManager.put("MenuItem.foreground", editorForegroundColor);
		UIManager.put("MenuItem.selectionBackground", editorForegroundColor);
		UIManager.put("MenuItem.selectionForeground", editorBackgroundColor);
	    UIManager.put("MenuItem.opaque", true);
	    UIManager.put("MenuItem.acceleratorForeground", editorForegroundColor);
		//UIManager.put("MenuBar.selectionBackground", Color.GREEN);
		//UIManager.put("MenuBar.selectionForeground", Color.BLUE);
	    //UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 0, 0, panelBorderColor));
	    //UIManager.put("MenuBar.borderPainted", false);
	    //UIManager.put("Button.background", editorBackgroundColor);
	    //UIManager.put("Button.foreground", editorForegroundColor);
	    //UIManager.put("Button.highlight", editorForegroundColor);
	    //UIManager.put("Button.border", BorderFactory.createMatteBorder(0, 0, 0, 0, panelBorderColor));
	    //UIManager.put("Button.borderPainted", false);
	    //UIManager.put("Button.focus", Color.black);
	    
		
	}
	
	private void populateTopPane() {
		populateMenuBar();
		attachMenuItemEvents();
		setMenuItemMnemonics();
		closeButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	window.closeApplication();
		    }
		});
	}

	Flat.FlatMenuItem menuNewFile, menuOpenFile, menuSaveFile, menuExit, 
					  menuOpenEnc, menuSaveEnc, menuOpenEncSteg, menuSaveEncSteg,
					  menuFind,
					  menuExtract, menuDecrypt, menuEncrypt,
					  menuOpenInBinViewer, menuCloseBinViewer,
					  menuAbout, menuHowTo;
	
	private void populateMenuBar() {
		Flat.FlatMenu fileMenu = new Flat.FlatMenu("File", this.getFlat().getPalette());
		Flat.FlatMenu editMenu = new Flat.FlatMenu("Edit", this.getFlat().getPalette());
		Flat.FlatMenu encrMenu = new Flat.FlatMenu("Encrypted", this.getFlat().getPalette());
		Flat.FlatMenu advnMenu = new Flat.FlatMenu("Advanced", this.getFlat().getPalette());
		Flat.FlatMenu viwrMenu = new Flat.FlatMenu("Binary Viewer", this.getFlat().getPalette());
		Flat.FlatMenu helpMenu = new Flat.FlatMenu("Help", this.getFlat().getPalette());
		Flat.FlatMenuItem menuItem;
		
		final ImageIcon menuIcon = new ImageIcon(TextEditor.class.getResource("/enigma-black_24.png"));
        fileMenu.setIcon(menuIcon);
        final Flat.FlatColorPalette separatorPalette = this.getFlat().getPalette().clone();
        separatorPalette.foregroundColor = Color.black;
		
        // File Menu
		menuItem = new Flat.FlatMenuItem("New Text File", this.getFlat().getPalette());
		fileMenu.add(menuItem); menuNewFile = menuItem;
		menuItem = new Flat.FlatMenuItem("Open Text File", this.getFlat().getPalette());
		fileMenu.add(menuItem); menuOpenFile = menuItem;
		menuItem = new Flat.FlatMenuItem("Save Text File", this.getFlat().getPalette());
		fileMenu.add(menuItem); menuSaveFile = menuItem;
		fileMenu.add(new Flat.FlatSeparator(separatorPalette));
		menuItem = new Flat.FlatMenuItem("Exit", this.getFlat().getPalette());
		menuItem.setPreferredSize(new Dimension(200, menuItem.getPreferredSize().height));
		fileMenu.add(menuItem); menuExit = menuItem;

		// Edit Menu
		menuItem = new Flat.FlatMenuItem("Find", this.getFlat().getPalette());
		menuItem.setPreferredSize(new Dimension(150, menuItem.getPreferredSize().height));
		editMenu.add(menuItem); menuFind = menuItem;
		
		// Encr Menu
		menuItem = new Flat.FlatMenuItem("Open Encrypted Steg. Image", this.getFlat().getPalette());
		encrMenu.add(menuItem); menuOpenEncSteg = menuItem;
		menuItem = new Flat.FlatMenuItem("Save Encrypted Steg. Image", this.getFlat().getPalette());
		encrMenu.add(menuItem); menuSaveEncSteg = menuItem;
		encrMenu.add(new Flat.FlatSeparator(separatorPalette));
		menuItem = new Flat.FlatMenuItem("Open Encrypted File", this.getFlat().getPalette());
		encrMenu.add(menuItem); menuOpenEnc = menuItem;
		menuItem = new Flat.FlatMenuItem("Save Encrypted File", this.getFlat().getPalette());
		encrMenu.add(menuItem); menuSaveEnc = menuItem;
		
		// Advn Menu
		menuItem = new Flat.FlatMenuItem("Extract Data", this.getFlat().getPalette());
		/*advnMenu.add(menuItem);*/ menuExtract = menuItem;
		menuItem = new Flat.FlatMenuItem("Decrypt File", this.getFlat().getPalette());
		/*advnMenu.add(menuItem);*/ menuDecrypt = menuItem;
		menuItem = new Flat.FlatMenuItem("Encrypt File", this.getFlat().getPalette());
		/*advnMenu.add(menuItem);*/ menuEncrypt = menuItem;
		
		// Viwr Menu
		menuItem = new Flat.FlatMenuItem("Open File", this.getFlat().getPalette());
		viwrMenu.add(menuItem); menuOpenInBinViewer = menuItem;
		menuItem = new Flat.FlatMenuItem("Close Viewer", this.getFlat().getPalette());
		viwrMenu.add(menuItem); menuCloseBinViewer = menuItem;
		advnMenu.add(viwrMenu);
		
		// Help Menu
		menuItem = new Flat.FlatMenuItem("About", this.getFlat().getPalette());
		helpMenu.add(menuItem); menuAbout = menuItem;
		menuItem = new Flat.FlatMenuItem("How To", this.getFlat().getPalette());
		helpMenu.add(menuItem); menuHowTo = menuItem;
		
		this.menuBar.add(fileMenu);
		this.menuBar.add(editMenu);
		this.menuBar.add(encrMenu);
		this.menuBar.add(advnMenu);
		this.menuBar.add(helpMenu);
	}
	
	private void setMenuItemMnemonics() {
		menuNewFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuSaveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		menuFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
	}
	
	private void attachMenuItemEvents() {
		
		// file menu
		menuNewFile.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	newFile();
		    }
		});
		menuOpenFile.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openFile();
		    }
		});
		menuSaveFile.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	saveFile();
		    }
		});
		menuExit.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	window.closeApplication();
		    }
		});
		
		// edit menu
		menuFind.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	displayFindPanel();
		    }
		});
		
		// encrypted menu
		menuOpenEnc.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openEncrypted();
		    }
		});
		menuSaveEnc.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	saveEncrypted();
		    }
		});
		menuOpenEncSteg.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openEncryptedImage();
		    }
		});
		menuSaveEncSteg.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	saveEncryptedImage();
		    }
		});
		
		// advanced menu
		menuExtract.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				writeConsole("Unimplemented functionality.");
		    }
		});
		menuDecrypt.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				writeConsole("Unimplemented functionality.");
		    }
		});
		menuEncrypt.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				writeConsole("Unimplemented functionality.");
		    }
		});
		menuOpenInBinViewer.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openFileInBinaryViewer();
		    }
		});
		menuCloseBinViewer.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	closeBinaryViewer();
		    }
		});
		
		// help menu
		menuAbout.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	openAboutScreen();
		    }
		});
		menuHowTo.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
				openHowToScreen();
		    }
		});
	}
	
	private void displayFindPanel() {
		this.findPane.setVisible(true);
		this.findPane.grabFocus();
	}
	
	private void openAboutScreen() {
    	// display modal dialog
		FlatDialog dialog = new FlatDialog(this, "About", 
				APP_TITLE + "\n" +
				"Version:                 " + APP_VERSION + "\n" + 
				"Release Year:            " + APP_YEAR + "\n" +
				"Original Creator:        " + "Itzo" + "\n" +
				"\n" +
				"Hashing Algorithm:       " + EncryptionSystem.HASH_ALGORITHM + "\n" +
				"Encryption Algorithm:    " + EncryptionSystem.ENC_ALGORITHM + "\n" +
				"Encryption Size:         " + (EncryptionSystem.ENC_BYTES * 8) + " bits\n" +
				"Steganography Algorithm: " + "LSB");
		dialog.showDialog();
	}
	
	private void openHowToScreen() {
    	// display modal dialog
		FlatDialog dialog = new FlatDialog(this, "How To", 
				"Regular expression search          /<\\w*>/");
		dialog.showDialog();
	}
	
	private void openEncryptedImage() {
		fileChooser.setFileFilter(imageFileFilter);
		byte[] bytes = requestFileOpen();
		imageFile = openedFile;
		fileChooser.setFileFilter(null);
		
		// make sure to clear the opened file so that the image 
		// does not get overwritten by a simple save
		openedFile = null;
		
		if(bytes == null) return;
		
		// open file as an image
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			writeConsole("Unable to read image file.");
		}
		
		if(image == null) return;
		bytes = SteganographySystem.decryptImage(image);

		if(bytes == null) return;
		
		byte[] password = requestPassword();
		if(password != null && password.length > 0) {
			byte[] decryptedData = EncryptionSystem.decryptData(bytes, password);
			String decodedText = decodeText(decryptedData);
			editorPane.setText(decodedText);
			setDocumentChanged(false);
		} else {
			// unsuccessful open
			writeConsole("No password entered.");
		}
	}
	
	private void saveEncryptedImage() {
		
		// if no image file is open already, then request one to select
		if(imageFile == null) {
			requestFileOpen("Select Host Image");
			imageFile = openedFile;
		}
		
		// make sure to clear the opened file so that the image 
		// does not get overwritten by a simple save
		openedFile = null;
		
		if(imageFile == null) return;
		
		// encrypt the data
		byte[] password = requestPassword();
		byte[] bytes = editorPane.getText().getBytes();
		byte[] encryptedData = EncryptionSystem.encryptData(bytes, password);
		
		// open file as an image
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			writeConsole("Unable to read image file.");
		}
		
		// encrypt image with data
		if(image == null) return;
		
		int imageSpace = SteganographySystem.spaceForLSB(image);
		if(imageSpace < encryptedData.length + 4) {
			writeConsole("Unable to fit " + encryptedData.length + 
			" bytes of data into an image with " + imageSpace + 
			" bytes free for data.");
		}
		
		BufferedImage encryptedImage = SteganographySystem.encryptImage(image, encryptedData);

		if(encryptedImage == null) {
			writeConsole("Unable to embed image file with data.");
			return;
		}
		
		//boolean success = requestFileSave(imageData);
		//requestFileOpen("Select Save File Name");
    	fileChooser.setDialogTitle("Select Save File Name");
    	int result = fileChooser.showSaveDialog(window);
    	File selectedFile = null;
    	if(result == JFileChooser.APPROVE_OPTION) {
    		selectedFile = fileChooser.getSelectedFile();
    	}
    	
		File outFile = selectedFile;
		if(outFile == null || !outFile.exists()) {
			String filename = "outFile.png";
			if(outFile != null) filename = outFile.getName();
			outFile = new File(filename);
		}
		
		boolean success = false;
		try {
			ImageIO.write(encryptedImage, "png", outFile);
			success = true;
		} catch (IOException e) {
			writeConsole("Unable to write image file.");
		}
		setDocumentChanged(!success);
		
	}
	
	private void openFileInBinaryViewer() {
		// hide the editorPane from the editorScrollPane
		// temporarily replace it with the binaryViewer
		// and display the selected file
		byte[] bytes = requestFileOpen();
		
		// make sure to clear the opened file so that the image 
		// does not get overwritten by a simple save
		openedFile = null;
		
		if(bytes == null) return;

		// remove any old binaryViewer that may be showing
		if(binaryViewer != null) contentPane.remove(binaryViewer);
		
		binaryViewer = new BinaryViewer(bytes, this);
		//this.editorScrollPane.setViewportView(binaryViewer);

		contentPane2.remove(editorScrollPane);
		contentPane2.add(binaryViewer, BorderLayout.CENTER);
		
		binaryViewer.centerImage();
		
		// display the instructions for using the binary viewer
		writeConsole("Binary Viewer Instructions:\n" +
					 "[<] and [>] arrows to alter the line width\n" + 
					 "mouse left button: click + drag to move\n" +
					 "mouse wheel: zoom in / out");
	}
	
	private void closeBinaryViewer() {
		if(binaryViewer == null) return;
		contentPane2.remove(binaryViewer);
		contentPane2.add(editorScrollPane, BorderLayout.CENTER);
		writeConsole("Binary viewer closed.");
	}
	
	private void newFile() {
		// offer to save the currently open file
		// then clear the text editor
		
		if(!editorPane.getText().isEmpty()) {
			// ask to save the opened file or not
			saveFile();
		}
		editorPane.setText("");
		openedFile = null;
		writeConsole("New text file created.");
	}
	
	private void openFile() {
		// determine if another file is already open and offer
		// to save it. afterward open the selected file and 
		// display it appropriately
		//
		// First determine the type of file this is:
		//
		// Text File:
		//  - Display contents onto the text area
		// Image File:
		//  - Attempt a data extract from the image
		// Encrypted Data:
		//  - Request a password for attempted decryption
		//
		
		byte[] bytes = requestFileOpen();
		if(bytes == null) return;
		String decodedText = decodeText(bytes);
		editorPane.setText(decodedText);
		setDocumentChanged(false);
	}
	
	private String decodeText(byte[] bytes) {
		if(bytes == null) return null;
		String decodedText = null;
		try {
			decodedText = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			writeConsole("Unable to decode text encoding.");
			e.printStackTrace();
		}
		return decodedText;
	}
	
	/**
	 * determine if the currently open text has
	 * a file assigned to it.  If not, then open a dialog
	 * to determine where to save it.  Otherwise just
	 * save it directly to the expected file.
	 */
	private void saveFile() {
		byte[] bytes = editorPane.getText().getBytes();
		boolean success = requestFileSave(bytes);
		setDocumentChanged(!success);
	}
	
	private byte[] requestFileOpen() {
		return requestFileOpen("Open File");
	}
	
	private byte[] requestFileOpen(String title) {
		// only read upto the number of bytes representable
				// by an integer
		    	fileChooser.setDialogTitle(title);
		    	int result = fileChooser.showOpenDialog(window);
		    	byte[] bytes = null;
		    	if(result == JFileChooser.APPROVE_OPTION) {
		    		File selectedFile = fileChooser.getSelectedFile();
		    		openedFile = selectedFile;
		    		try {
		    			FileInputStream reader = new FileInputStream(selectedFile);
		    			
						int fileLength = (int) selectedFile.length();
						bytes = new byte[fileLength];
						reader.read(bytes, 0, fileLength);
						
						writeConsole("File opened. " + openedFile.getPath());
					} catch (FileNotFoundException e) {
						writeConsole("Unable to open file.");
						e.printStackTrace();
						openedFile = null;
					} catch (IOException e) {
						writeConsole("Unable to read file.");
						e.printStackTrace();
						openedFile = null;
					}
		    	} else {
					writeConsole("File open cancelled.");
		    	}
		    	
		    	return bytes;
	}
	
	private boolean requestFileSave(byte[] bytes) {
		boolean fileSaved = false;
    	fileChooser.setDialogTitle("Save File");
		if(openedFile == null || !openedFile.canWrite()) {
	    	int result = fileChooser.showSaveDialog(window);
	    	if(result == JFileChooser.APPROVE_OPTION) {
	    		File selectedFile = fileChooser.getSelectedFile();
	    		openedFile = selectedFile;
	    	}
		}
		if(openedFile == null) {
			// the request was canceled
			writeConsole("File save request cancelled by the user.");
			return fileSaved;
		}
		try {
			//FileWriter writer = new FileWriter(openedFile);
			FileOutputStream writer = new FileOutputStream(openedFile);
			writer.write(bytes);
			writer.close();
			fileSaved = true;
			writeConsole("File saved. " + openedFile.getPath());
		} catch (IOException e) {
			writeConsole("Cannot save to file.");
			e.printStackTrace();
		}
		return fileSaved;
	}
	
	private void openEncrypted() {
		byte[] bytes = requestFileOpen();
		encryptedFile = openedFile;
		
		// make sure to clear the opened file so that the image 
		// does not get overwritten by a simple save
		openedFile = null;
		
		if(bytes == null) return;
		byte[] password = requestPassword();
		byte[] decryptedData = EncryptionSystem.decryptData(bytes, password);
		String decodedText = decodeText(decryptedData);
		editorPane.setText(decodedText);
		setDocumentChanged(false);
	}
	
	private void saveEncrypted() {
		byte[] password = requestPassword();
		byte[] bytes = editorPane.getText().getBytes();
		byte[] encryptedData = EncryptionSystem.encryptData(bytes, password);
		boolean success = requestFileSave(encryptedData);
		encryptedFile = openedFile;
		setDocumentChanged(!success);
	}
	
	private byte[] requestPassword() {
		FlatPasswordDialog dialog = new FlatPasswordDialog(window, "Enter Password");
		int response = dialog.showDialog();
				
		String password = "";
		if (response == FlatDialog.OK_OPTION) {
			password = new String(dialog.getPassword());
		}
		return password.getBytes();
	}
	
	private void setUiEvents() {
		editorPane.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// replaced by accelerators/mnemonics
				/*int modifier = e.getModifiersEx();
				int key = e.getKeyCode();
				if(((modifier & KeyEvent.CTRL_DOWN_MASK) > 0) &&
						key == KeyEvent.VK_S) {
					saveFile();
				}*/
				//writeConsole("key: " + key + " modifier: " + modifier);
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		editorPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				//writeConsole("text inserted");
				setDocumentChanged(true);
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				//writeConsole("text removed");
				setDocumentChanged(true);
			}
		});
	}
	
	private void setDocumentChanged(boolean changed) {
		if(changed) {
			windowTitle.setText(APP_TITLE + "*");
		} else {
			windowTitle.setText(APP_TITLE);
		}
	}
	
	/**
	 * Write debug or other information out to the
	 * the determined console.
	 * 
	 * @param text
	 */
	public void writeConsole(String text) {
		
		// if the provided text contains more than one line
		// of text, then the console should expand large enough
		// to encompass all the text.  The console should
		// also remain longer when more text is displayed.
		
    	int lineHeight = consolePane.getFontMetrics(consoleFont).getHeight();
    	int numLines = text.replaceAll("[^\n]", "").length() + 1;
    	int paneHeight = lineHeight * numLines;
    	consoleScrollPane.setPreferredSize(
    			new Dimension(consoleScrollPane.getWidth(), paneHeight + 2));
    	consolePane.setPreferredSize(new Dimension(consoleScrollPane.getWidth(), paneHeight));
		
		contentPane.add(consoleScrollPane, BorderLayout.SOUTH);
		contentPane.revalidate();
		consolePane.setText(consolePane.getText() + "\n" + text);
		consolePane.setCaretPosition(consolePane.getText().length());
		//System.out.println(text);
		
		if(numLines > 1) {
			closeConsoleTimer.setInitialDelay(CONSOLE_TIMEOUT_LONG);
		} else {
			closeConsoleTimer.setInitialDelay(CONSOLE_TIMEOUT_SHORT);
		}
		
		// start the close procedure
		if(closeConsoleTimer.isRunning()) {
			closeConsoleTimer.stop();
		}
		closeConsoleTimer.start();
	}
	
	// add event handling to the console so that when
	// the console receives focus, the closeTimer will be stopped.
	// once the console loses focus again, reactivate the closeTimer
	
	FocusListener consoleFocusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			if(closeConsoleTimer.isRunning()) {
				closeConsoleTimer.stop();
			}
		}
		@Override
		public void focusLost(FocusEvent e) {
			if(!closeConsoleTimer.isRunning()) {
				closeConsoleTimer.start();
			}
		}
    };
	
	ActionListener closeConsoleAction = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			contentPane.remove(consoleScrollPane);
			contentPane.revalidate();
		}
	};
	Timer closeConsoleTimer = new Timer(CONSOLE_TIMEOUT_SHORT, closeConsoleAction);

	
	/**
	 * Create and activate the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
        		TextEditor textEditor = new TextEditor();
        		textEditor.setVisible(true);
            }
        });
	}
	
	
	
	
}
