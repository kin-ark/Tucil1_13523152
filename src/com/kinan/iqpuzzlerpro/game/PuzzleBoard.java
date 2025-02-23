package com.kinan.iqpuzzlerpro.game;

import java.util.HashMap;
import java.util.Map;

public class PuzzleBoard {
    private char[][] board;
    private int row, col;
    private Map<Character, String> pieceColors;
    private static final String RESET = "\u001B[0m";

    public PuzzleBoard(int rows, int cols) {
        this.row = rows;
        this.col = cols;
        this.board = new char[row][col];
        this.pieceColors = new HashMap<>();

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = '#';
            }
        }
    }

    public PuzzleBoard(char[][] customBoard) {
        this.row = customBoard.length;
        this.col = customBoard[0].length;
        this.board = new char[row][col];
        this.pieceColors = new HashMap<>();

        for (int i = 0; i < row; i++) {
            System.arraycopy(customBoard[i], 0, board[i], 0, col);
        }
        
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (board[i][j] == 'X'){
                    board[i][j] = '#';
                }
            }
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char[][] getBoard(){
        return board;
    }

    public boolean isEmpty(int x, int y) {
        return board[y][x] == '#';
    }

    public boolean canPlacePiece(PuzzlePiece piece, int x, int y) {
        int[][] shape = piece.getShape();
    
        for (int[] coord : shape) {
            int newX = x + coord[0];
            int newY = y + coord[1];

            if (newX < 0 || newY < 0 || newX >= col || newY >= row || board[newY][newX] != '#' || board[newY][newX] == '.') {
                return false;
            }
        }
        return true;
    }

    public void placePiece(PuzzlePiece piece, int x, int y) {
        int[][] shape = piece.getShape();
        char id = piece.getId();
    
        for (int[] coord : shape) {
            int newX = x + coord[0];
            int newY = y + coord[1];
            board[newY][newX] = id;
        }

        pieceColors.putIfAbsent(id, piece.getColor());
    }

    public void removePiece(PuzzlePiece piece, int x, int y) {
        int[][] shape = piece.getShape();
    
        for (int[] coord : shape) {
            int newX = x + coord[0];
            int newY = y + coord[1];

            if (newX >= 0 && newY >= 0 && newX < col && newY < row) {
                board[newY][newX] = '#';
            }
        }

        pieceColors.remove(piece.getId());
    }

    public boolean isSolved() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == '#') {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '#') {
                    System.out.print(RESET + "#  ");
                } else if (cell == '.') {
                    System.out.print("   " + RESET);
                } else {
                    String color = pieceColors.getOrDefault(cell, RESET);
                    System.out.print(color + cell + "  " + RESET);
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
