package ui.flat.dialog;

import ui.flat.component.FlatButton;
import ui.flat.component.FlatPanel;
import ui.flat.settings.FlatColorPalette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class FlatPasswordDialog extends FlatDialog {

	private JPasswordField passwordField;
	private FlatButton okButton;
	private FlatButton cancelButton;
	
	public FlatPasswordDialog(JFrame parent, String title) {
		super(parent, title);
		initContent();
		attachEvents();
	}
	
	private void initContent() {
		passwordField = new JPasswordField();
		FlatColorPalette buttonPalette = new FlatColorPalette();
		buttonPalette.setBackgroundColor(Color.black);
		buttonPalette.setBorderColor(Color.black);

		FlatPanel panel = new FlatPanel();
		FlatPanel optionsPanel = new FlatPanel(new BorderLayout())
				.addLeft(new FlatButton("Ok", buttonPalette, e -> {
					dialogResponse = FlatDialog.OK_OPTION;
					window.dispose();
				}))
				.addRight(new FlatButton("Cancel", buttonPalette, e -> {
					dialogResponse = FlatDialog.CANCEL_OPTION;
					window.dispose();
				}));
		
		panel.addCenter(passwordField);
		panel.addBottom(optionsPanel);
		
		optionsPanel.setBackground(frameBackgroundColor);
		passwordField.setForeground(frameForegroundColor);
		passwordField.setBackground(FlatColorPalette.DARK_GRAY);
		passwordField.getCaret().setVisible(true);
		passwordField.setCaretColor(Color.white);
		passwordField.setBorder(BorderFactory.createMatteBorder(
        		5,5,5,5, frameBackgroundColor));

		addCenter(panel);
	}
	
	public char[] getPassword() {
		return passwordField.getPassword();
	}
	
	public int showDialog() {
		dialogResponse = FlatDialog.CLOSED_OPTION;
		this.setLocationRelativeTo(parent);
		passwordField.requestFocus();
		this.setVisible(true);
		return dialogResponse;
	}
	
	private void attachEvents() {
		passwordField.addActionListener(new ActionListener() {
			@Override // enter key is pressed
			public void actionPerformed(ActionEvent e) {
				dialogResponse = FlatDialog.OK_OPTION;
				window.dispose();
			}
		});
	}
	
	
	
	
	
}
