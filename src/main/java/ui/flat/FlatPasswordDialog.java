package ui.flat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import ui.flat.Flat.FlatColorPalette;

public class FlatPasswordDialog extends FlatDialog {

	private JPasswordField passwordField;
	private Flat.FlatButton okButton;
	private Flat.FlatButton cancelButton;
	
	public FlatPasswordDialog(JFrame parent, String title) {
		super(parent, title);
		initContent();
		attachEvents();
	}
	
	private void initContent() {
		passwordField = new JPasswordField();
		FlatColorPalette buttonPalette = this.getFlat().getPalette().clone();
		buttonPalette.backgroundColor = Color.black;
		buttonPalette.borderColor = Color.black;
		okButton = new Flat.FlatButton("Ok", buttonPalette);
		cancelButton = new Flat.FlatButton("Cancel", buttonPalette);
		JPanel panel = new JPanel(new BorderLayout());
		JPanel optionsPanel = new JPanel(new BorderLayout());
		
		optionsPanel.add(okButton, BorderLayout.EAST);
		optionsPanel.add(cancelButton, BorderLayout.WEST);
		
		panel.add(passwordField, BorderLayout.CENTER);
		panel.add(optionsPanel, BorderLayout.SOUTH);
		
		optionsPanel.setBackground(frameBackgroundColor);
		passwordField.setForeground(frameForegroundColor);
		passwordField.setBackground(new Color(60,60,60));
		passwordField.getCaret().setVisible(true);
		passwordField.setCaretColor(Color.white);
		passwordField.setBorder(BorderFactory.createMatteBorder(
        		5,5,5,5, frameBackgroundColor));

		setContent(panel);
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
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogResponse = FlatDialog.OK_OPTION;
				window.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogResponse = FlatDialog.CANCEL_OPTION;
				window.dispose();
			}
		});
		
		
	}
	
	
	
	
	
}
