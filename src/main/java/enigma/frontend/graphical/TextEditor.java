package enigma.frontend.graphical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.*;
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
import enigma.backend.utils.ValidationUtils;
import ui.flat.FlatFrame;
import ui.flat.component.FlatButton;
import ui.flat.component.FlatLineNumberHeader;
import ui.flat.component.FlatPanel;
import ui.flat.component.FlatScrollPane;
import ui.flat.component.menu.FlatMenu;
import ui.flat.component.menu.FlatMenuItem;
import ui.flat.component.menu.FlatSeparator;
import ui.flat.component.scrollbar.FlatScrollBar;
import ui.flat.dialog.FlatDialog;
import ui.flat.dialog.FlatPasswordDialog;
import ui.flat.settings.FlatColorPalette;

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
	
	private static final String APP_TITLE = "Enigma Text Editor";
	private static float APP_VERSION = 0.15f;
	private static final int APP_YEAR = 2018;
	private static final int CONSOLE_TIMEOUT_SHORT = 3000;
	private static final int CONSOLE_TIMEOUT_LONG = 8000;
	private static final int CONSOLE_DETECTION_HEIGHT = 10;
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

	// state
	private boolean documentChanged;

	public TextEditor() {
		super(APP_TITLE,
			  new Color(60,60,60),
			  new Color(200,200,200),
			  new Color(60,60,60));
        
        // reference self
        window = this;

        // state
        this.documentChanged = false;
        
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
		editorScrollPane = new FlatScrollPane(editorPane,
				FlatColorPalette.DEFAULT_PALETTE, editorVerticalScrollBar,
				editorHorizontalScrollBar);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		consolePane = new ConsolePanel();
		FlatScrollBar consoleVerticalScrollBar = new FlatScrollBar(JScrollBar.VERTICAL);
		FlatScrollBar consoleHorizontalScrollBar = new FlatScrollBar(JScrollBar.HORIZONTAL);
		consoleScrollPane = new FlatScrollPane(consolePane,
				FlatColorPalette.DEFAULT_PALETTE, consoleVerticalScrollBar,
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
		FlatColorPalette findPanelPalette = FlatColorPalette.DEFAULT_PALETTE.clone()
				.setForegroundColor(FlatColorPalette.DEFAULT_PALETTE.getBackgroundColor())
				.setBackgroundColor(FlatColorPalette.DEFAULT_PALETTE.getForegroundColor())
				.setBorderColor(FlatColorPalette.DEFAULT_PALETTE.getForegroundColor())
				.setHoverForegroundColor(FlatColorPalette.DEFAULT_PALETTE.getBackgroundColor())
				.setHoverBackgroundColor(FlatColorPalette.DEFAULT_PALETTE.getForegroundColor());
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

	/**
	 * Close the application
	 */
	@Override
	public void closeApplication() {
		if(this.documentChanged) {
			// Display "There are unsaved changes, are you sure you want to close the application?"
			FlatColorPalette darkPalette = new FlatColorPalette()
					.setBackgroundColor(Color.black)
					.setForegroundColor(FlatColorPalette.DEFAULT_PALETTE.getForegroundColor());
			FlatDialog dialog = new FlatDialog(this, "Unsaved Changes",
					"There are unsaved changes, are you sure you want to close the application?");
			dialog.addBottom(new FlatPanel(darkPalette)
							.addLeft(new FlatButton("Okay", darkPalette, e -> super.closeApplication()))
							.addRight(new FlatButton("Cancel", darkPalette, e -> dialog.dispose())));
			dialog.showDialog();
			return;
		}
		super.closeApplication();
	}
	
	private void setFileFilters() {
		imageFileFilter = new FileNameExtensionFilter("Image Files", "png");
	}
	
	private void setComponentEvents() {
        consolePane.addFocusListener(consoleFocusListener);
        this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int height = getHeight();
				if (e.getY() >= height - CONSOLE_DETECTION_HEIGHT) {
					showConsole(CONSOLE_TIMEOUT_LONG, 2);
				}
			}
		});
	}
	
	private void setEditorStyling() {
		editorFont = new Font("Consolas", Font.PLAIN, 12);
		lineNumberFont = new Font("Consolas", Font.PLAIN, 12);
		consoleFont = new Font("Consolas", Font.PLAIN, 12);
		editorBackgroundColor = this.frameBackgroundColor;
		editorForegroundColor = this.frameForegroundColor;
		selectionBackgroundColor = new Color(51,204,255);
		selectionForegroundColor = new Color(0, 0, 0);
        
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
	}
	
	private void populateMenuBar() {

        final FlatColorPalette separatorPalette = FlatColorPalette.DEFAULT_PALETTE.clone();
        separatorPalette.setForegroundColor(Color.black);
		
        // File Menu
		this.menuBar.add(new FlatMenu("File")
				.setMenuIcon(new ImageIcon(TextEditor.class.getResource("/enigma-black_24.png")))
				.add(new FlatMenuItem("New Text File", e -> newFile())
						.setShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)))
				.add(new FlatMenuItem("Open Text File", e -> openFile())
						.setShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)))
				.add(new FlatMenuItem("Save Text File", e -> saveFile())
						.setShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)))
				.add(new FlatSeparator(separatorPalette))
				.add(new FlatMenuItem("Exit", e -> window.closeApplication())
						.setShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK))
						.setPreferredWidth(200)));

		// Edit Menu
		this.menuBar.add(new FlatMenu("Edit")
				.add(new FlatMenuItem("Find", e -> displayFindPanel())
						.setShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK))
						.setPreferredWidth(150)));
		
		// Encr Menu
		this.menuBar.add(new FlatMenu("Encrypted")
				.add(new FlatMenuItem("Open Encrypted Steg. Image", e -> openEncryptedImage()))
				.add(new FlatMenuItem("Save Encrypted Steg. Image", e -> saveEncryptedImage()))
				.add(new FlatSeparator(separatorPalette))
				.add(new FlatMenuItem("Open Encrypted File", e -> openEncrypted()))
				.add(new FlatMenuItem("Save Encrypted File", e -> saveEncrypted())));

		// Advn Menu
		this.menuBar.add(new FlatMenu("Advanced")
//				.add(new FlatMenuItem("Extract Data", e -> writeConsole("Unimplemented functionality.")))
//				.add(new FlatMenuItem("Decrypt File", e -> writeConsole("Unimplemented functionality.")))
//				.add(new FlatMenuItem("Encrypt File", e -> writeConsole("Unimplemented functionality.")))
				.add(new FlatMenu("Binary Viewer")
						.add(new FlatMenuItem("Open File", e -> openFileInBinaryViewer())))
						.add(new FlatMenuItem("Close Viewer", e -> closeBinaryViewer())));
		
		// Help Menu
		this.menuBar.add(new FlatMenu("Help")
				.add(new FlatMenuItem("About", e -> openAboutScreen())))
				.add(new FlatMenuItem("How To", e -> openHowToScreen()));
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

	/**
	 * Set whether the document has changed since opening.
	 *
	 * @param changed
	 */
	private void setDocumentChanged(boolean changed) {
		this.documentChanged = changed;
		if(changed) {
			windowTitle.setText(APP_TITLE + "*");
		} else {
			windowTitle.setText(APP_TITLE);
		}
	}

	/**
	 * Determine whether the document has changed since opening
	 *
	 * @return
	 */
	private boolean getDocumentChanged() {
		return this.documentChanged;
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

    	int numLines = text.replaceAll("[^\n]", "").length() + 1;
		consolePane.setText((ValidationUtils.isEmpty(consolePane.getText())
				? "" : consolePane.getText()) + "\n" + text);
		consolePane.setCaretPosition(consolePane.getText().length());
		this.showConsole(numLines > 1 ? CONSOLE_TIMEOUT_LONG : CONSOLE_TIMEOUT_SHORT, numLines);
	}

	/**
	 * Show the console for a specified amount of time
	 *
	 * @param milliseconds
	 */
	private void showConsole(final int milliseconds, final int numLines) {
		int lineHeight = consolePane.getFontMetrics(consoleFont).getHeight();
		int paneHeight = lineHeight * numLines;
		consoleScrollPane.setPreferredSize(
				new Dimension(consoleScrollPane.getWidth(), paneHeight + 2));
		consolePane.setPreferredSize(new Dimension(consoleScrollPane.getWidth(), paneHeight));
		contentPane.add(consoleScrollPane, BorderLayout.SOUTH);
		contentPane.revalidate();

		closeConsoleTimer.setInitialDelay(milliseconds);
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
