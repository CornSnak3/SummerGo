package core;

import core.exception.UnsupportedFileFormatException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            board = Board.loadGame(new File("BoardTestCases.save"));
        } catch (IOException | UnsupportedFileFormatException exc) {
            exc.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void makeMove() {
        // Suicidal
        assertFalse(board.makeMove(1, 0));
        // Ko
        assertTrue(board.makeMove(7, 6));
        assertSame(board.getCurrentPlayer(), board.getPlayerOne());
        assertFalse(board.makeMove(7, 7));
        assertSame(board.getCurrentPlayer(), board.getPlayerOne());
        assertTrue(board.makeMove(1, 0));
        assertEquals(5, board.getPlayerOne().getCapturedStones());
        assertTrue(board.makeMove(1, 4));
        // It's not ko after move sequence in different place
        assertTrue(board.makeMove(7, 7));
    }

    @org.junit.jupiter.api.Test
    void makePass() {
        assertSame(board.getCurrentPlayer(), board.getPlayerTwo());
        board.makePass();
        assertNotSame(board.getCurrentPlayer(), board.getPlayerTwo());
        board.makePass();
        assertTrue(board.isGameOver());
    }

    @org.junit.jupiter.api.Test
    void isOnBoard() {
        for (int i = 0; i < board.getBoardSize(); i++)
            for (int j = 0; j < board.getBoardSize(); j++)
                assertTrue(board.isOnBoard(i, j));
    }

    @org.junit.jupiter.api.Test
    void saveGame() {
    }

    @org.junit.jupiter.api.Test
    void loadGame() {
    }
}