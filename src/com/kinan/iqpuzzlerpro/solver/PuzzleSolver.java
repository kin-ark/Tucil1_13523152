package com.kinan.iqpuzzlerpro.solver;

import com.kinan.iqpuzzlerpro.game.*;

public class PuzzleSolver {
    public PuzzleBoard board;
    private PuzzlePiece[] pieces;
    public boolean solved;
    public long executionTime;
    public long turn;
    private String type;

    public PuzzleSolver(PuzzleBoard board, PuzzlePiece[] pieces, String type){
        this.board = board;
        this.pieces = pieces;
        this.solved = false;
        this.turn = 0;
        this.type = type;
    }

    public boolean solvePuzzle(int piecePlaced) {
        if (piecePlaced == pieces.length) {
            if (board.isSolved()) {
                board.printBoard();
                solved = true;
                return solved;
            }
            return solved;
        }
    
        PuzzlePiece piece = pieces[piecePlaced];
        
    
        for (PuzzlePiece variation : piece.variations) {
            for (int y = 0; y < board.getRow(); y++) {
                for (int x = 0; x < board.getCol(); x++) {
                    turn++;
                    // if (turn % 10000000 == 0){
                    //     System.out.println(turn);
                    //     board.printBoard();
                    // }
                    
                    if (!board.canPlacePiece(variation, x, y)) continue;
    
                    // System.out.println("Turn: " + turn++);
                    // System.out.println("Placing piece: " + piece.getId());
                    // board.printBoard();
    
                    board.placePiece(variation, x, y);
                    if (solvePuzzle(piecePlaced + 1)) {
                        return true;
                    }
                    board.removePiece(variation, x, y);
                }
            }
        }
        return false;
    }

    public void startSolving() {
        solved = false;
        
        long startTime = System.nanoTime();
        
        if (!solvePuzzle(0)) {
            System.out.println("No solution found.");
        }
        
        System.out.println("Turn: " + turn);
        long endTime = System.nanoTime();
        
        executionTime = (endTime - startTime) / 1000000;
        System.out.println(executionTime + "ms");
    }
}
