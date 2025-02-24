package com.kinan.iqpuzzlerpro.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputReader {
    public int N, M, P;
    public String type;
    public List<List<int[]>> puzzlePieces;
    public char[] ids;
    public char[][] customBoard;

    public InputReader(String fileName) {
        puzzlePieces = new ArrayList<>();
        readInputFile(fileName);
    }

    private void readInputFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] firstLine = br.readLine().split(" ");
            if (firstLine.length != 3){
                throw new Error("Input format is not correct!");
            }

            N = parsePositiveInt(firstLine[0], "Invalid row count (N)");
            M = parsePositiveInt(firstLine[1], "Invalid column count (M)");
            P = parsePositiveInt(firstLine[2], "Invalid piece count (P)");

            ids = new char[P];

            type = br.readLine();
            if (!"DEFAULT".equals(type) && !"CUSTOM".equals(type)) {
                throw new Error("Invalid type: Must be either 'DEFAULT' or 'CUSTOM'.");
            }

            if ("CUSTOM".equals(type)) {
                customBoard = new char[N][M];
                for (int i = 0; i < N; i++) {
                    String line = br.readLine();
                    if (line == null || line.length() != M) {
                        throw new Error("Invalid board: Each row must have exactly " + M + " characters.");
                    }
                    for (char c : line.toCharArray()) {
                        if (c != 'X' && c != '.') {
                            throw new Error("Invalid board character: Only 'X' (available cells) and '.' are allowed.");
                        }
                    }
                    customBoard[i] = line.toCharArray();
                }
            }

            List<String> currentPiece = new ArrayList<>();
            char currentId = 0;
            String line;
            int index = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                char firstChar = line.trim().charAt(0);
                if (Character.isWhitespace(firstChar)) {
                    throw new Error("Invalid piece format: Each piece must start with an ID character.");
                }

                if (currentPiece.isEmpty() || firstChar != currentId) {
                    if (!currentPiece.isEmpty()) {
                        puzzlePieces.add(parsePiece(currentPiece));
                        currentPiece.clear();
                        index++;
                    }
                    if (index >= P) {
                        throw new Error("Too many puzzle pieces: Expected " + P + ", but more were found.");
                    }
                    currentId = firstChar;
                    for(int i = 0; i < ids.length; i++){
                        if (currentId == ids[i]){
                            throw new Error("Duplicate pieces found.");
                        }
                    }
                    ids[index] = currentId;
                }
                currentPiece.add(line);
            }

            if (!currentPiece.isEmpty()) {
                puzzlePieces.add(parsePiece(currentPiece));
            }

            if (puzzlePieces.size() != P) {
                throw new Error("Mismatch in piece count: Expected " + P + " pieces, but found " + puzzlePieces.size() + ".");
            }
        } catch (IOException e) {
            throw new Error("Error reading file: " + e.getMessage());
        }
    }

    private int parsePositiveInt(String value, String errorMessage) {
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                throw new Error(errorMessage + " must be a positive integer.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new Error(errorMessage + " is not a valid integer.");
        }
    }

    private List<int[]> parsePiece(List<String> shape) {
        List<int[]> coordinates = new ArrayList<>();

        int minX = Integer.MAX_VALUE;
        for (String row : shape) {
            int firstCharIndex = row.indexOf(row.trim().charAt(0));
            if (firstCharIndex < minX) {
                minX = firstCharIndex;
            }
        }

        for (int y = 0; y < shape.size(); y++) {
            for (int x = 0; x < shape.get(y).length(); x++) {
                if (shape.get(y).charAt(x) != ' ') {
                    coordinates.add(new int[]{x - minX, y});
                }
            }
        }
        return coordinates;
    }
}