package com.accenture.jpdict.gui;

import com.accenture.jpdict.model.QueryResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class Result extends JPanel {
    private QueryResult result;
    private JTable queryResultTable;
    private List<Integer> selectedIndices;

    // Default result
    public Result() {
        Object columnNames[] = {"", "English", "Japanese", "Kana", "Romaji","Definition"};
        Object[][] data = {};
        DefaultTableModel dtm = new DefaultTableModel(data, columnNames) {
            boolean[] canEdit = new boolean[]{true, false, false, false, false, false};
            @Override
            public boolean isCellEditable(int row, int col) {
                return canEdit[col];
            }
        };
        queryResultTable = new JTable(dtm);
        for (int x = 0; x < 5; x++) {
           dtm.addRow(new Object[]{ new Boolean(false),
                   "row " + (x+1) + " col 2",
                   "row " + (x+1) + " col 3",
                   "row " + (x+1) + " col 4",
                   "row " + (x+1) + " col 5",
                   "row " + (x+1) + " col 6"});
        }
        JScrollPane sp = new JScrollPane(queryResultTable);
        TableColumn tc = queryResultTable.getColumnModel().getColumn(0);
        tc.setCellEditor(queryResultTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(queryResultTable.getDefaultRenderer(Boolean.class));
        tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));

        this.add(sp);
    }

    public Result(QueryResult queryResult) {
        this.result = queryResult;
    }

    private class MyItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getSource();
            if (source instanceof AbstractButton == false) return;
            boolean checked = e.getStateChange() == ItemEvent.SELECTED;
            for (int x = 0, y = queryResultTable.getRowCount(); x < y; x++) {
                queryResultTable.setValueAt(new Boolean(checked), x, 0);
            }
        }
    }
}
