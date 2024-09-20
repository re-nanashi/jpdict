package com.nana.jpdict;

import com.nana.jpdict.gui.DictionaryUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DictionaryUI ui = new DictionaryUI();
                ui.display();
            }
        });
    }
}