package com.accenture.jpdict.gui;

import com.accenture.jpdict.model.JpWord;
import com.accenture.jpdict.model.QueryResult;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

enum Status {
    SELECTED, DESELECTED, INDETERMINATE
}

public class ResultsPane extends JTabbedPane {
    public ResultsPane() {
        this.createDefaultTab();
    }

    public void createDefaultTab() {
        Object[][] sampleData = {
                {true, "Welcome","ようこそ","ようこそ","youkoso","welcome"},
                {true, "to", "戸", "と", "to", "door (esp. Japanese-style)"},
                {true, "my","我が", "わが", "waga", "my,our,one's own"},
                {true, "dictionary", "辞書", "じしょ", "jisho", "dictionary,lexicon"}};
        this.addTab("ようこそ", new ResultPane(sampleData));
    }

    public void createTab(QueryResult queryResult) {
        List<JpWord> words = queryResult.getResults();
        if (words.isEmpty()) {
            createDefaultTab();
        }

        int sizeOfResults = words.size();
        Object[][] rows = new Object[sizeOfResults][6];
        for (int i = 0; i < sizeOfResults; i++) {
            JpWord word = words.get(i);
            Object[] rowData = {true, word.getEnglish(), word.getWord(), word.getReading(), word.getRomaji(), word.getOtherDefs()};
            rows[i] = rowData;
        }

        // Remove sample tab if displayed then continue to add the tabs to be displayed
        if (this.getTabCount() > 0 && this.getTitleAt(0).equals("ようこそ")) {
            this.removeTabAt(0);
        }
        this.addTab(queryResult.getQueryString(), new ResultPane(rows));
    }

    private JScrollPane getScrollPaneFromPanel(JPanel panel) {
        for (java.awt.Component component : panel.getComponents()) {
            if (component instanceof JScrollPane) {
                return (JScrollPane) component;
            }
        }
        return null;
    }

    public void copyResultsFromActiveTabToClipboard() {
        final int EXTRACTED_WORD_DETAIL_COUNT = 5;

        List<String> results = new ArrayList<>();

        JScrollPane retrievedScrollPane = getScrollPaneFromPanel((JPanel) this.getSelectedComponent());
        if (retrievedScrollPane == null) {
            throw new RuntimeException("JScrollPane not found");
        }

        // Extract the JTable from the JScrollPane
        JTable table = (JTable) retrievedScrollPane.getViewport().getView();
        TableModel model = table.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object firstColumn = model.getValueAt(row, 0);
            if (firstColumn instanceof Boolean isSelected && !isSelected) {
                continue;
            }

            String[] values = new String[EXTRACTED_WORD_DETAIL_COUNT];
            for (int col = 1, i = 0; col <= EXTRACTED_WORD_DETAIL_COUNT; col++, i++) {
                values[i] = String.valueOf(model.getValueAt(row, col));
            }

            results.add(String.format("%s;%s;%s;%s;%s", values[0], values[1], values[2], values[3], values[4]));
        }

        // Copy selected results to clipboard
        String stringToCopy = String.join("\n", results);
        StringSelection stringSelection = new StringSelection(stringToCopy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}

class ResultPane extends JPanel {
    private final Object[] COLUMN_NAMES = {Status.SELECTED, "English", "Japanese", "Kana", "Romaji", "Definition"};

    protected ResultPane(Object[][] data) {
        super(new BorderLayout()); // initialize using border layout

        // Set table model using our custom config
        DefaultTableModel model = new DefaultTableModel(COLUMN_NAMES, 0) {
            final boolean[] canEdit = new boolean[]{true, false, false, false, false, false};

            @Override public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return canEdit[col];
            }
        };

        // Insert rows here using this type: Object[]
        for (Object[] row : data) {
            model.addRow(row);
        }

        JTable queryResultTable = new JTable(model) {
            private static final int MODEL_COLUMN_IDX = 0;
            private transient HeaderCheckBoxHandler handler;

            @Override
            public void updateUI() {
                setSelectionForeground(new ColorUIResource(Color.RED));
                setSelectionBackground(new ColorUIResource(Color.RED));

                getTableHeader().removeMouseListener(handler);

                TableModel model = getModel();
                if (Objects.nonNull(model)) {
                    model.removeTableModelListener(handler);
                }
                super.updateUI();

                model = getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    TableCellRenderer renderer = getDefaultRenderer(model.getColumnClass(i));
                    if (renderer instanceof Component) {
                        SwingUtilities.updateComponentTreeUI((Component) renderer);
                    }
                }

                TableColumn column = getColumnModel().getColumn(MODEL_COLUMN_IDX);
                column.setHeaderRenderer(new HeaderRenderer());
                column.setHeaderValue(Status.SELECTED);

                handler = new HeaderCheckBoxHandler(this, MODEL_COLUMN_IDX);
                model.addTableModelListener(handler);
                getTableHeader().addMouseListener(handler);
            }

            @Override
            public Component prepareEditor(TableCellEditor editor, int row, int column) {
                Component component = super.prepareEditor(editor, row, column);

                if (component instanceof JCheckBox checkBox) {
                    checkBox.setBackground(getSelectionBackground());
                    checkBox.setBorderPainted(true);
                }

                return component;
            }
        };

        // Custom cell renderer for japanese fonts
        DefaultTableCellRenderer jpRenderer = new DefaultTableCellRenderer() {
            final Font font = new Font("Meiryo", Font.PLAIN, 12);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(font);
                return this;
            }
        };

        // Set custom fonts on Japanese word columns
        int KANJI_COLUMN_IDX = 2;
        int KANA_COLUMN_IDX = 3;
        queryResultTable.getColumnModel().getColumn(KANJI_COLUMN_IDX).setCellRenderer(jpRenderer);
        queryResultTable.getColumnModel().getColumn(KANA_COLUMN_IDX).setCellRenderer(jpRenderer);

        queryResultTable.getTableHeader().setReorderingAllowed(false);
        queryResultTable.setFillsViewportHeight(true);

        // Set row and column dimensions
        queryResultTable.setRowHeight(20); // rows
        // columns
        final int FIRST_COLUMN = 0;
        final int LAST_COLUMN = 5;
        TableColumnModel columnModel = queryResultTable.getColumnModel();
        columnModel.getColumn(FIRST_COLUMN).setMaxWidth(90);
        columnModel.getColumn(FIRST_COLUMN).setPreferredWidth(90);
        columnModel.getColumn(LAST_COLUMN).setPreferredWidth(400);

        add(new JScrollPane(queryResultTable));
    }
}

class HeaderRenderer implements TableCellRenderer {
    private final JCheckBox checkbox = new JCheckBox("");
    private final JLabel label = new JLabel("Select all");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value instanceof Status) {
            switch ((Status) value) {
                case SELECTED:
                    checkbox.setSelected(true);
                    checkbox.setEnabled(true);
                    break;
                case DESELECTED:
                    checkbox.setSelected(false);
                    checkbox.setEnabled(true);
                    break;
                case INDETERMINATE:
                    checkbox.setSelected(true);
                    checkbox.setEnabled(false);
                    break;
                default:
                    throw new AssertionError("Unknown Status");
            }
        } else {
            checkbox.setSelected(true);
            checkbox.setEnabled(false);
        }
        checkbox.setOpaque(false);
        checkbox.setFont(table.getFont());

        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (component instanceof JLabel) {
            JLabel jl = (JLabel) component;
            label.setIcon(new ComponentIcon(checkbox));
            jl.setIcon(new ComponentIcon(label));
            jl.setText(null);
        }

        return component;
    }
}

class HeaderCheckBoxHandler extends MouseAdapter implements TableModelListener {
    private final JTable table;
    private final int targetColumnIndex;

    protected HeaderCheckBoxHandler(JTable table, int index) {
        super();
        this.table = table;
        this.targetColumnIndex = index;
    }

    @Override
    public void tableChanged(TableModelEvent event) {
        if (event.getType() == TableModelEvent.UPDATE && event.getColumn() == targetColumnIndex) {
            int vci = table.convertColumnIndexToView(targetColumnIndex);
            TableColumn column = table.getColumnModel().getColumn(vci);
            Object status = column.getHeaderValue();
            TableModel model = table.getModel();

            if (model instanceof DefaultTableModel && checkRepaint((DefaultTableModel) model, column, status)) {
                JTableHeader header = table.getTableHeader();
                header.repaint(header.getHeaderRect(vci));
            }
        }
    }

    private boolean checkRepaint(DefaultTableModel model, TableColumn column, Object status) {
        if (status == Status.INDETERMINATE) {
            List<?> data = model.getDataVector();
            List<Boolean> items = data.stream()
                    .map(vector -> (Boolean) ((List<?>) vector).get(targetColumnIndex) )
                    .distinct()
                    .toList();

            boolean notDuplicates = items.size() == 1;
            if (notDuplicates) {
                boolean isSelected = items.getFirst();
                column.setHeaderValue(isSelected ? Status.SELECTED : Status.DESELECTED);
                return true;
            } else {
                return false;
            }
        } else {
            column.setHeaderValue(Status.INDETERMINATE);
            return true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        JTableHeader header = (JTableHeader) event.getComponent();
        JTable table = header.getTable();
        TableColumnModel columnModel = table.getColumnModel();
        TableModel model = table.getModel();

        int vci = columnModel.getColumnIndexAtX(event.getX());
        int mci = table.convertColumnIndexToModel(vci);

        if (mci == targetColumnIndex && model.getRowCount() > 0) {
            TableColumn column = columnModel.getColumn(vci);
            boolean status = column.getHeaderValue() == Status.DESELECTED;

            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(status, i, mci);
            }

            column.setHeaderValue(status ? Status.SELECTED : Status.DESELECTED);
        }
    }
}

class ComponentIcon implements Icon {
    private final Component component;

    protected ComponentIcon(Component component) {
        this.component = component;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        SwingUtilities.paintComponent(g, component, c.getParent(), x, y, getIconWidth(), getIconHeight());
    }

    @Override
    public int getIconWidth() {
        return component.getPreferredSize().width;
    }

    @Override
    public int getIconHeight() {
        return component.getPreferredSize().height;
    }
}