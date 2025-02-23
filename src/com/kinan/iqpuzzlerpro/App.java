package com.kinan.iqpuzzlerpro;

import com.kinan.iqpuzzlerpro.gui.PuzzleSolverGUI;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PuzzleSolverGUI::new);
    }
}