package com.kinan.iqpuzzlerpro.gui;

import com.kinan.iqpuzzlerpro.game.PuzzleBoard;
import com.kinan.iqpuzzlerpro.game.PuzzlePiece;
import com.kinan.iqpuzzlerpro.io.InputReader;
import com.kinan.iqpuzzlerpro.solver.PuzzleSolver;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PuzzleSolverGUI {
    private File selectedFile;
    private JButton selectFileButton;
    private JFrame frame;
    private JLabel loadingLabel;
    private char[][] solutionBoard;
    private boolean solved;
    private long timesTaken;
    private long turnsTaken;

    public PuzzleSolverGUI() {
        solved = false;
        frame = new JFrame("IQ Puzzle Pro Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("IQ Puzzle Pro Solver");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        selectFileButton = new JButton("Pick Puzzle Config File (.txt)");
        styleButton(selectFileButton);
        selectFileButton.addActionListener(e -> openFileChooser());

        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);
        gbc.gridy = 1;
        panel.add(selectFileButton, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (file.getName().endsWith(".txt")) {
                selectedFile = file;
                selectFileButton.setText("File Selected: " + file.getName());
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());

                showLoadingScreen();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid file type! Please select a .txt file.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLoadingScreen() {
        frame.getContentPane().removeAll();

        JLabel statusLabel = new JLabel("Solving the puzzle... Please wait.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        frame.add(statusLabel, BorderLayout.NORTH);

        loadingLabel = new JLabel("⠋", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        frame.add(loadingLabel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();

        startLoadingAnimation();
        runSolver();
    }

    private void startLoadingAnimation() {
        SwingWorker<Void, String> animationWorker = new SwingWorker<>() {
            private final String[] spinnerFrames = { "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏" };

            @Override
            protected Void doInBackground() {
                int i = 0;
                while (!isCancelled()) {
                    publish(spinnerFrames[i % spinnerFrames.length]);
                    i++;
                    try {
                        Thread.sleep(100); // Change frame every 100ms
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                loadingLabel.setText(chunks.get(chunks.size() - 1));
            }
        };
        animationWorker.execute();
    }

    private void runSolver() {
        SwingWorker<Void, Void> solverWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    InputReader reader = new InputReader(selectedFile.getAbsolutePath());
    
                    PuzzleBoard board = null;
                    if ("DEFAULT".equals(reader.type)){
                        board = new PuzzleBoard(reader.N, reader.M);
                    } else if ("CUSTOM".equals(reader.type)){
                        board = new PuzzleBoard(reader.customBoard);
                    }
                    
                    PuzzlePiece[] pieces = new PuzzlePiece[reader.P];
    
                    String[] colors = {
                        "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m",
                        "\u001B[1;31m", "\u001B[1;32m", "\u001B[1;33m", "\u001B[1;34m", "\u001B[1;35m", "\u001B[1;36m",
                        "\u001B[4;31m", "\u001B[4;32m", "\u001B[4;33m", "\u001B[4;34m", "\u001B[4;35m", "\u001B[4;36m",
                        "\u001B[41m", "\u001B[42m", "\u001B[43m", "\u001B[44m", "\u001B[45m", "\u001B[46m",
                        "\u001B[1;4;31m", "\u001B[1;4;32m"
                    };
    
                    for (int i = 0; i < reader.P; i++) {
                        int[][] shape = reader.puzzlePieces.get(i).toArray(new int[0][]);
                        int size = reader.puzzlePieces.get(i).size();
                        pieces[i] = new PuzzlePiece(shape, size, reader.ids[i], colors[i]);
                        pieces[i].getAllUniqueVariations();
                    }
    
                    PuzzleSolver solver = new PuzzleSolver(board, pieces, reader.type);
                    solver.startSolving();

                    if (solver.solved){
                        solutionBoard = solver.board.getBoard();
                        solved = true;
                    }

                    timesTaken = solver.executionTime;
                    turnsTaken = solver.turn;

    
                } catch (Error e) { 
                    showErrorDialog("Input Error: " + e.getMessage());
                    return null; 
                } catch (Exception e) { 
                    showErrorDialog("Unexpected Error!");
                    return null; 
                }
                return null;
            }
    
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> showFinalScreen());
            }
        };
    
        solverWorker.execute();
    }
    
    private void showFinalScreen() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout(10, 10));
    
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Title Label
        JLabel solvedLabel = new JLabel("Puzzle Result!", SwingConstants.CENTER);
        solvedLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        topPanel.add(solvedLabel, BorderLayout.NORTH);
    
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JLabel turnLabel = new JLabel("Turns: " + turnsTaken, SwingConstants.CENTER);
        JLabel timeLabel = new JLabel("Time: " + timesTaken + " ms", SwingConstants.CENTER);
    
        turnLabel.setFont(new Font("Poppins", Font.BOLD, 14));
        timeLabel.setFont(new Font("Poppins", Font.BOLD, 14));
    
        statsPanel.add(turnLabel);
        statsPanel.add(timeLabel);
    
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        
        if (solved){
            // Puzzle Grid
            JPanel puzzlePanel = new JPanel(new GridLayout(solutionBoard.length, solutionBoard[0].length));
            puzzlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
            for (char[] row : solutionBoard) {
                for (char cell : row) {
                    JLabel cellLabel = new JLabel(String.valueOf(cell), SwingConstants.CENTER);
                    cellLabel.setOpaque(true);
                    cellLabel.setBackground(getColorForPiece(cell));
                    cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    puzzlePanel.add(cellLabel);
                }
            }

            frame.add(puzzlePanel, BorderLayout.CENTER);
        } else{
            JLabel unsolvedLabel = new JLabel("No Solution Found. :(", SwingConstants.CENTER);
            unsolvedLabel.setFont(new Font("Poppins", Font.BOLD, 14));
            frame.add(unsolvedLabel, BorderLayout.CENTER);
        }
    
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        if (solved){
            JButton saveTextButton = new JButton("Save as Text");
            JButton saveImageButton = new JButton("Save as Image");

            styleButton(saveTextButton);
            styleButton(saveImageButton);

            saveTextButton.addActionListener(e -> saveSolutionToFile());
            saveImageButton.addActionListener(e -> saveSolutionAsImage());

            buttonPanel.add(saveTextButton);
            buttonPanel.add(saveImageButton);
        }

        JButton restartButton = new JButton("Restart App");
    
        styleButton(restartButton);
    
        restartButton.addActionListener(e -> restartApp());
    
        buttonPanel.add(restartButton);
    
        frame.add(buttonPanel, BorderLayout.SOUTH);
    
        frame.revalidate();
        frame.repaint();
    }
    
    

    private void saveSolutionToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Solution As...");
        fileChooser.setSelectedFile(new File("solution.txt"));

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                for (char[] row : solutionBoard) {
                    writer.println(new String(row));
                }
                writer.write("Times taken: " + timesTaken + " ms\n");
                writer.write("Turns taken: " + turnsTaken + " turns\n");
                JOptionPane.showMessageDialog(frame, "Solution saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving solution!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveSolutionAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Solution As Image...");
        fileChooser.setSelectedFile(new File("solution.png"));
    
        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
    
            int rows = solutionBoard.length;
            int cols = solutionBoard[0].length;
            int cellSize = 50;
            int width = cols * cellSize;
            int height = rows * cellSize;
    
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
    
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    g2d.setColor(getColorForPiece(solutionBoard[i][j]));
                    g2d.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    g2d.drawString(String.valueOf(solutionBoard[i][j]), j * cellSize + cellSize / 3, i * cellSize + cellSize / 2);
                }
            }
            g2d.dispose();
    
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(frame, "Solution image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private Color getColorForPiece(char piece) {
        switch (piece) {
            case 'A': return Color.RED;
            case 'B': return Color.GREEN;
            case 'C': return Color.YELLOW;
            case 'D': return Color.BLUE;
            case 'E': return Color.MAGENTA;
            case 'F': return Color.CYAN;
            case 'G': return Color.ORANGE;
            case 'H': return Color.PINK;
            case 'I': return new Color(128, 0, 128); // Purple
            case 'J': return new Color(139, 69, 19); // Brown
            case 'K': return new Color(255, 165, 0); // Orange
            case 'L': return new Color(75, 0, 130); // Indigo
            case 'M': return new Color(0, 128, 128); // Teal
            case 'N': return new Color(0, 255, 127); // Spring Green
            case 'O': return new Color(255, 105, 180); // Hot Pink
            case 'P': return new Color(173, 216, 230); // Light Blue
            case 'Q': return new Color(240, 230, 140); // Khaki
            case 'R': return new Color(250, 128, 114); // Salmon
            case 'S': return new Color(154, 205, 50); // Yellow Green
            case 'T': return new Color(0, 206, 209); // Dark Turquoise
            case 'U': return new Color(255, 99, 71); // Tomato
            case 'V': return new Color(188, 143, 143); // Rosy Brown
            case 'W': return new Color(85, 107, 47); // Dark Olive Green
            case 'X': return new Color(123, 104, 238); // Medium Slate Blue
            case 'Y': return new Color(147, 112, 219); // Medium Purple
            case 'Z': return new Color(210, 180, 140); // Tan
            default: return Color.LIGHT_GRAY; // Default color for empty or unknown
        }
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(89, 119, 212));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 89, 182));
            }
        });
    }

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            restartApp();
        });
    }
    
    private void restartApp() {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            new PuzzleSolverGUI();
        });
    }
}
