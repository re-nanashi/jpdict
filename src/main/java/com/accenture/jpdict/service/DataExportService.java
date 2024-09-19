package com.accenture.jpdict.service;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DataExportService {
    public void export(List<List<String>> data) throws IOException {
        File file  = new File("Results.xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Create a workbook
            Workbook workbook = new Workbook(fos, "ResultsWorkbook", "1.0");

            // TODO: Multi-thread here
            data.forEach(tableData -> {
                String firstLetterOfWord = String.valueOf(Character.toUpperCase(tableData.getFirst().charAt(0)));
                // Create a new sheet
                Worksheet sheet = workbook.newWorksheet(firstLetterOfWord);

                // Set the column headers
                sheet.value(0, 0, "English");
                sheet.value(0, 1, "Japanese");
                sheet.value(0, 2, "Hiragana/Katakana");
                sheet.value(0, 3, "Romaji");
                sheet.value(0, 4, "Definition");
                sheet.value(0, 5, "Remarks");

                // Set the data
                for (int row = 0; row < tableData.size(); row++) {
                    String[] rowData = tableData.get(row).split(";");
                    sheet.value(row + 1, 0, rowData[0]);
                    sheet.value(row + 1, 0, rowData[1]);
                    sheet.value(row + 1, 0, rowData[2]);
                    sheet.value(row + 1, 0, rowData[3]);
                    sheet.value(row + 1, 0, rowData[4]);
                    sheet.value(row + 1, 0, rowData[5]);
                }
            });

            workbook.finish();
        }
    }
}
