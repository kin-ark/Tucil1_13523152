package com.kinan.iqpuzzlerpro.game;

import java.util.Arrays;
import java.util.HashSet;

public class PuzzlePiece {
    private int[][] shape;
    private int size;
    private char id;
    private String color;
    public PuzzlePiece[] variations;

    public PuzzlePiece(int[][] shape, int size, char id, String color) {
        this.shape = shape;
        this.size = size;
        this.id = id;
        this.color = color;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getSize() {
        return size;
    }

    public char getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public PuzzlePiece rotate() {
        int[][] newShape = new int[size][2];
        for (int i = 0; i < size; i++) {
            newShape[i][0] = -shape[i][1];
            newShape[i][1] = shape[i][0];
        }
        return new PuzzlePiece(newShape, size, id, color);
    }

    public PuzzlePiece flipH() {
        int[][] newShape = new int[size][2];
        for (int i = 0; i < size; i++) {
            newShape[i][0] = -shape[i][0];
            newShape[i][1] = shape[i][1];
        }
        return new PuzzlePiece(newShape, size, id, color);
    }

    public PuzzlePiece normalize() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    
        for (int[] cell : shape) {
            minX = Math.min(minX, cell[0]);
            minY = Math.min(minY, cell[1]);
        }
    
        int[][] newShape = new int[size][2];
        for (int i = 0; i < size; i++) {
            newShape[i][0] = shape[i][0] - minX;
            newShape[i][1] = shape[i][1] - minY;
        }
    
        return new PuzzlePiece(newShape, size, id, color);
    }
    
    public void getAllUniqueVariations() {
        PuzzlePiece[] allVariations = new PuzzlePiece[8];
        PuzzlePiece current = this;
    
        for (int i = 0; i < 4; i++) {
            allVariations[i] = current.normalize();
            allVariations[i + 4] = current.flipH().normalize();
            current = current.rotate();
        }
    
        HashSet<String> uniqueShapes = new HashSet<>();
        int uniqueCount = 0;
    
        for (PuzzlePiece piece : allVariations) {
            String shapeString = Arrays.deepToString(piece.getShape());
            if (!uniqueShapes.contains(shapeString)) {
                uniqueShapes.add(shapeString);
                uniqueCount++;
            }
        }
    
        variations = new PuzzlePiece[uniqueCount];
        int index = 0;
        for (PuzzlePiece piece : allVariations) {
            String shapeString = Arrays.deepToString(piece.getShape());
            if (uniqueShapes.contains(shapeString)) {
                variations[index++] = piece;
                uniqueShapes.remove(shapeString);
            }
        }
    }
}
