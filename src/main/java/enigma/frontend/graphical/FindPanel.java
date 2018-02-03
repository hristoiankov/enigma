package enigma.frontend.graphical;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import ui.flat.component.FlatButton;
import ui.flat.settings.FlatColorPalette;
import ui.flat.component.FlatPanel;
import ui.flat.component.FlatTextField;
import ui.flat.component.scrollbar.FlatScrollBar;

public class FindPanel extends FlatPanel {
    private static final int SEARCH_MARKER_CATEGORY = 0;
    private static final Color SEARCH_MARKER_COLOR = Color.yellow;
    private FlatTextField searchField;
    private EditorPanel editorPane;
    private FlatScrollBar scrollBar;
    private SimpleAttributeSet highlightAttribute;

    FindPanel(final FlatColorPalette palette, final EditorPanel editorPane,
              final FlatScrollBar scrollBar) {
        super(palette);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.editorPane = editorPane;
        this.scrollBar = scrollBar;

        searchField = new FlatTextField(palette);
        searchField.setFont(new Font("Consolas", Font.PLAIN, 12));
        searchField.setSelectionColor(new Color(51,204,255));
        searchField.setSize(200, searchField.getHeight());
        searchField.setPreferredSize(new Dimension(200, searchField.getHeight()));

        final FindPanel panel = this;
        this.add(searchField);
        this.add(new FlatButton("Find Next", palette, e ->
            findNextResult(panel.editorPane, panel.searchField.getText())));
        this.add(new FlatButton("X", palette, e -> {
            panel.setVisible(false);
            resetHighlighting();
        }));

        // set highlighting attribute color
        highlightAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(highlightAttribute, Color.WHITE);
        StyleConstants.setBackground(highlightAttribute, Color.BLACK);

        // setup event handlers
        initEventHandlers();
    }

    private void initEventHandlers() {
        final FindPanel panel = this;

        // focus event
        panel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                panel.searchField.grabFocus();
                panel.searchField.select(0, panel.searchField.getText().length());
            }
            @Override
            public void focusLost(FocusEvent arg0) {
            }
        });

        // action event of text field
        searchField.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.updateHighlighting();
            }
        });

        // text field document listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                panel.updateHighlighting();
            }
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                panel.updateHighlighting();
            }
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                panel.updateHighlighting();
            }
        });

        // event handler for window resizes
        this.editorPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                searchAndHighlightAllResults(panel.editorPane, searchField.getText());
            }
        });
    }

    private void resetHighlighting() {
        //editorPane.getHighlighter().removeAllHighlights();
        editorPane.getStyledDocument().setCharacterAttributes(0,
                editorPane.getDocument().getLength(), new SimpleAttributeSet(), true);
        scrollBar.clearMarkers(FindPanel.SEARCH_MARKER_CATEGORY);
        scrollBar.getParent().revalidate();
        scrollBar.getParent().repaint();
    }

    private void updateHighlighting() {
        final FindPanel panel = this;
        new Thread(() -> {
            searchAndHighlightAllResults(panel.editorPane, searchField.getText());
        }).start();
    }

    /**
     * Search the provided editor pane for the given search phrase.
     * Highlight all related encounters of the phrase.
     * @param editorPane
     * @param searchPhrase
     */
    private void searchAndHighlightAllResults(final EditorPanel editorPane, final String searchPhrase) {

        // if the search panel is not open, then do not perform this task.
        if(!this.isVisible()) {
            return;
        }

		/*final Highlighter highlighter = editorPane.getHighlighter();
		final HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.black);*/
        String editorText = null;
        try {
            editorText = editorPane.getDocument().getText(0, editorPane.getDocument().getLength());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        // reset all highlighting
        resetHighlighting();

        if(editorText == null || searchPhrase == null ||
                editorText.length() < 1 || searchPhrase.length() < 1) {
            return;
        }

        // determine the number of lines in the document or if it is larger,
        // the number of lines potentially visible on the editor at its current
        // size
        int paneHeight = editorPane.getHeight();
        int lineHeight = editorPane.getFontMetrics(editorPane.getFont()).getHeight();
        float numVisibleLines = paneHeight / (float)lineHeight;
        float rowCount = editorPane.getRowCount();
        if(numVisibleLines > rowCount) rowCount = numVisibleLines;
        final float rowCountFinal = rowCount;

        // determine regex or plain text search
        // regex if "/query/"
        Matcher patternMatcher = getPatternMatcher(searchPhrase, editorText);

        int index = 0;
        int highlightLength = 0;
        ArrayList<Integer> highlightedLineIndeces = new ArrayList<>();
        while(index >= 0 && index < editorText.length()) {
            if(patternMatcher != null) {
                if(patternMatcher.find(index)) {
                    index = patternMatcher.start();
                    highlightLength = patternMatcher.end() - patternMatcher.start();
                } else {
                    highlightLength = 0;
                }
            } else {
                index = editorText.toLowerCase().indexOf(searchPhrase.toLowerCase(), index);
                highlightLength = searchPhrase.length();
            }

            if(index >= 0) {
				/*try {
					highlighter.addHighlight(index, index + searchPhrase.length(), painter);
				} catch (BadLocationException e) {
					System.out.println(index + " | " + index + searchPhrase.length());
					//e.printStackTrace();
				}*/

                editorPane.getStyledDocument().setCharacterAttributes(index, highlightLength, highlightAttribute, true);

                // determine the line the text was found on, how many lines the match continues,
                // how many lines max there are in this document and then add the marker on to
                // the scrollbar
                //
                // actually instead determine how many lines are able to appear on the text editor
                // as it is sized now and scale the scroll bar by that amount compared to the
                // number of lines of text in the document. If the numLinesVisible > numLinesDocument
                // then scale by numLinesVisible
                //int rowIndex = editorPane.getRowIndex(index);
                //scrollBar.addMarker(rowIndex / rowCount, SEARCH_MARKER_COLOR, SEARCH_MARKER_CATEGORY);
                //highlightedLineIndeces.add(rowIndex);
                highlightedLineIndeces.add(index);

                if(highlightLength < 1) highlightLength = 1;
                index += highlightLength;


            }
        }

        // add highlight markers to the scroll bar
        List<Integer> eolList = editorPane.getEOLList();
        highlightedLineIndeces.stream().forEach(index2 -> {
            //int rowIndex = editorPane.getRowIndex(index2);
            int rowIndex = editorPane.getRowIndex(index2, eolList);
            scrollBar.addMarker(rowIndex / rowCountFinal, SEARCH_MARKER_COLOR, SEARCH_MARKER_CATEGORY);
        });

        // force an interface update
        SwingUtilities.invokeLater(() -> {

            scrollBar.getParent().revalidate();
            scrollBar.getParent().repaint();
        });

    }

    /**
     * Return a pattern matcher if the given string is determined
     * to be a regex query
     * @param searchPhrase
     * @return
     */
    private static Matcher getPatternMatcher(String searchPhrase, String text) {
        if(searchPhrase == null || text == null) {
            return null;
        }

        Matcher patternMatcher = null;
        if(searchPhrase.length() > 2 && searchPhrase.startsWith("/") && searchPhrase.endsWith("/")) {
            String searchPhraseSubstring = searchPhrase.substring(1, searchPhrase.length() - 1);
            try {
                Pattern pattern = Pattern.compile(searchPhraseSubstring);
                patternMatcher = pattern.matcher(text);
            }catch(Exception e) {
                // if there are any problems compiling the regex,
                // then no longer treat it as a regex
            }
        }
        return patternMatcher;
    }

    private void findNextResult(JTextPane editorPane, String searchPhrase) {
        String editorText = null;
        try {
            editorText = editorPane.getDocument().getText(0, editorPane.getDocument().getLength());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        int startIndex = editorPane.getCaretPosition() + 1;
        if(startIndex >= editorPane.getDocument().getLength()) {
            startIndex = 0;
        }

        Matcher patternMatcher = getPatternMatcher(searchPhrase, editorText);
        int index = 0;
        if(patternMatcher != null && patternMatcher.find(startIndex)) {
            index = patternMatcher.start();
        } else {
            index = editorText.toLowerCase().indexOf(searchPhrase.toLowerCase(), startIndex);
        }
        if(index > 0) {
            editorPane.grabFocus();
            editorPane.setCaretPosition(index);
        } else if(startIndex > 0) {
            // if no result is found and the start index is not
            // at the start of the document, then place it there and
            // search again
            editorPane.setCaretPosition(0);
            findNextResult(editorPane, searchPhrase);
        }
    }



}






