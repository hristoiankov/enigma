package enigma.frontend.graphical;

import ui.flat.component.FlatLineNumberHeader;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Hristo on 5/4/2016.
 * Represented by: editorPane
 */
public class EditorPanel extends JTextPane {

    public EditorPanel() {
        super();

    }

    public List<Integer> getEOLList() {
        final String text = this.getText();
        return IntStream.range(0, text.length())
                .filter(index -> text.charAt(index) == '\n')
                .boxed()
                .collect(Collectors.toList());
    }

    public int getRowIndex(int offset, final List<Integer> eolList) {
        return (int) eolList.stream()
                .filter(eolIndex -> eolIndex < offset)
                .count();
    }

    /**
     * Get the count of rows for the given JTextPane
     * @return
     */
    public int getRowCount() {
        return FlatLineNumberHeader.getRowCount(this);
    }

    /**
     * Get the row index of the row at the given offset into the JTextPane\
     * @param offset offset into the document
     * @return
     */
    public int getRowIndex(int offset) {
        return FlatLineNumberHeader.getRowIndex(this, offset);
    }

    /**
     * Get the column index of the column at the given offset into the JTextPane
     * @param offset offset into the document
     * @return
     */
    public int getColumnIndex(int offset) {
        return FlatLineNumberHeader.getColumnIndex(this, offset);
    }

}
