package com.kinan.iqpuzzlerpro.game;

public class PuzzlePiece {
    private int[][] shape;
    private int size;
    private int id;

    public PuzzlePiece(int[][] shape, int size) {
        this.shape = shape;
        this.size = size;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public PuzzlePiece rotate() {
        int[][] newShape = new int[size][2];
        for (int i = 0; i < size; i++) {
            newShape[i][0] = shape[i][1];
            newShape[i][1] = -shape[i][0];
        }
        return new PuzzlePiece(newShape, size);
    }

    public PuzzlePiece flipH() {
        int[][] newShape = new int[size][2];
        for (int i = 0; i < size; i++) {
            newShape[i][0] = -shape[i][0];
            newShape[i][1] = shape[i][1];
        }
        return new PuzzlePiece(newShape, size);
    }

    public PuzzlePiece[] getAllVariations() {
        PuzzlePiece[] variations = new PuzzlePiece[8];
        PuzzlePiece current = this;
        for (int i = 0; i < 4; i++) {
            variations[i] = current;
            variations[i + 4] = current.flipH();
            current = current.rotate();
        }
        return variations;
    }
}
