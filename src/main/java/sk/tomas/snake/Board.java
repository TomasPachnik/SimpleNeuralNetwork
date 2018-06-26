package sk.tomas.snake;

import java.io.Serializable;

class Board implements Serializable {

    private Node board[][];

    Board(int gridWidth, int gridHeight) {
        board = new Node[gridWidth][gridHeight];
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                board[i][j] = new Node(i, j);
            }
        }
    }

    Node getAtPosition(int x, int y) {
        return board[x][y];
    }

    int getGridWidth() {
        return board.length;
    }

    int getGridHeight() {
        return board[0].length;
    }

    void print() {
        for (int i = 0; i < getGridWidth(); i++) {
            for (int j = 0; j < getGridHeight(); j++) {
                if (getAtPosition(i, j).isHead()) {
                    System.out.print("X");
                } else if (getAtPosition(i, j).isSnakeBody()) {
                    System.out.print("x");
                } else if (getAtPosition(i, j).isApple()) {
                    System.out.print("o");
                } else {
                    System.out.print("-");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
