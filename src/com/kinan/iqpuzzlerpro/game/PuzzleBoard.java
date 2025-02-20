package com.kinan.iqpuzzlerpro.game;

public class PuzzleBoard {
    private int[][] board;
    private int row, col;

    public PuzzleBoard(int rows, int cols){
        this.row = rows;
        this.col = cols;
        this.board = new int[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = 0;
            }
        }
    }

    public boolean canPlacePiece(PuzzlePiece piece, int x, int y){
        int[][] shape = piece.getShape();
        for (int i = 0; i < piece.getSize(); i++){
            int newX = x + shape[i][0];
            int newY = y + shape[i][1];

            if (newX < 0 || newY < 0 || newX >= col || newY >= row || board[newX][newY] != 0){
                return  false;
            }
        }
        return  true;
    }

    public void placePiece(PuzzlePiece piece, int x, int y){
        int[][] shape = piece.getShape();
        int id = piece.getId();
        for (int i = 0; i < piece.getSize(); i++){
            board[y + shape[i][1]][x + shape[i][0]] = id;
        }
    }

    public boolean isSolved() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
