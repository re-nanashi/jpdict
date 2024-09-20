package com.nana.jpdict.service;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataExportService {
    public void export(List<List<String>> data) throws IOException {
        // Use date and time as filename
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Define the desired format for Windows filename
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        // Format the date and time
        String formattedDateTime = now.format(formatter);

        File file  = new File("Results_" + formattedDateTime +".xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Create a workbook
            Workbook workbook = new Workbook(fos, "ResultsWorkbook", "1.0");
            // TODO
            Map<String, Worksheet> worksheets = new HashMap<>();
            class WorksheetData {
                private int lastRow;
                private Worksheet worksheet;

            }

            // TODO: Multi-thread here
            // TODO: Add to the same worksheet if same first Letter
            //       we need to find the number of rows currently
            data.forEach(tableData -> {
                String firstLetterOfWord = String.valueOf(Character.toUpperCase(tableData.getFirst().charAt(0)));
                boolean sheetAlreadyExists = worksheets.containsKey(firstLetterOfWord);

                // Create a new sheet or get already created worksheet
                Worksheet sheet = sheetAlreadyExists ? worksheets.get(firstLetterOfWord) : workbook.newWorksheet(firstLetterOfWord);

                // Set the column headers
                sheet.value(0, 0, "Category");
                sheet.value(0, 1, "English");
                sheet.value(0, 2, "Japanese");
                sheet.value(0, 3, "Hiragana/Katakana");
                sheet.value(0, 4, "Romaji");
                sheet.value(0, 5, "Definition");

                // Set the data
                for (int row = 0; row < tableData.size(); row++) {
                    String[] rowData = tableData.get(row).split(";");
                    sheet.value(row + 1, 1, rowData[0]);
                    sheet.value(row + 1, 2, rowData[1]);
                    sheet.value(row + 1, 3, rowData[2]);
                    sheet.value(row + 1, 4, rowData[3]);
                    sheet.value(row + 1, 5, rowData[4]);
                }

                if (!sheetAlreadyExists) {
                    worksheets.put(firstLetterOfWord, sheet);
                }
            });

            workbook.finish();
        }
    }
}
