package tablut;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;
import java.util.List;

/** The suite of all JUnit tests for the enigma package.
 *  @author Swadhin Nalubola
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Sout line to print the board nicely:
     *  System.out.printf("===%n%s===%n", b);. */

    /** Test the copy method from Board. */
    @Test
    public void testCopy() {
        Board b = new Board();
        Board c = new Board();
        b.makeMove(Square.sq(7, 4), Square.sq(7, 5));
        c.copy(b);
        assertEquals(b.get(Square.sq(7, 5)), c.get(Square.sq(7, 5)));
    }

    /** Test the move limit functionality of Board. */
    @Test
    public void testMoveLimit() {
        Board b = new Board();
        b.setMoveLimit(2);
        b.makeMove(Square.sq(7, 4), Square.sq(7, 5));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(4, 6), Square.sq(5, 6));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(7, 5), Square.sq(7, 4));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(5, 6), Square.sq(6, 6));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(7, 4), Square.sq(7, 5));
        assertEquals(Piece.WHITE, b.winner());
    }

    /** Test that wins from repeated positions are recorded properly. */
    @Test
    public void testCheckRepeated() {
        Board b = new Board();
        b.makeMove(Square.sq(7, 4), Square.sq(7, 5));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(4, 6), Square.sq(5, 6));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(7, 5), Square.sq(7, 4));
        assertEquals(null, b.winner());
        b.makeMove(Square.sq(5, 6), Square.sq(4, 6));
        assertEquals(Piece.BLACK, b.winner());
        assertEquals(true, b.repeatedPosition());
    }

    /** Test the functionality of the undo method from Board. */
    @Test
    public void testUndo() {
        Board b = new Board();
        Board c = new Board();
        Board d = new Board();
        b.makeMove(Square.sq(7, 4), Square.sq(7, 5));
        c.copy(b);
        b.makeMove(Square.sq(4, 6), Square.sq(5, 6));
        b.undo();
        assertEquals(b.encodedBoard(), c.encodedBoard());
        b.undo();
        assertEquals(b.encodedBoard(), d.encodedBoard());
        assertEquals(null, b.winner());
        assertEquals(false, b.repeatedPosition());
    }


    /** Tests legalMoves for white pieces. */
    @Test
    public void testLegalWhiteMoves() {

        Board b = new Board();
        List<Move> movesList = b.legalMoves(Piece.WHITE);

        assertEquals(56, movesList.size());

        assertFalse(movesList.contains(Move.mv("e7-8")));
        assertFalse(movesList.contains(Move.mv("e8-f")));

        assertTrue(movesList.contains(Move.mv("e6-f")));
        assertTrue(movesList.contains(Move.mv("f5-8")));
    }

    /** Tests legalMoves for black pieces. */
    @Test
    public void testLegalBlackMoves() {

        Board b = new Board();
        List<Move> movesList = b.legalMoves(Piece.BLACK);

        assertEquals(80, movesList.size());

        assertFalse(movesList.contains(Move.mv("e8-7")));
        assertFalse(movesList.contains(Move.mv("e7-8")));

        assertTrue(movesList.contains(Move.mv("f9-i")));
        assertTrue(movesList.contains(Move.mv("h5-1")));

    }



}


