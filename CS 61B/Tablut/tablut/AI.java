package tablut;

import static java.lang.Math.*;

import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Swadhin Nalubola
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** Number of pieces for maxDepth of 2. */
    private static final int DEPTHTWO = 20;
    /** Number of pieces for maxDepth of 3. */
    private static final int DEPTHTHREE = 10;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move myMove = findMove();
        _controller.reportMove(myMove);
        return myMove.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (b.turn() == WHITE) {
            findMove(b, maxDepth(b), true, 1, INFTY * -1, INFTY);
        } else if (b.turn() == BLACK) {
            findMove(b, maxDepth(b), true, -1, INFTY * -1, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {

        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }

        if (sense == 1) {
            int bestSoFar = Integer.MIN_VALUE;
            Move bestMove = board.legalMoves(board.turn()).get(0);

            for (Move mv : board.legalMoves(board.turn())) {
                Board clone = new Board(board);
                clone.makeMove(mv);
                int val = findMove(clone, depth - 1,
                        false, sense * -1, alpha, beta);
                clone.undo();
                if (val > bestSoFar) {
                    bestMove = mv;
                }
                bestSoFar = max(bestSoFar, val);
                alpha = max(alpha, bestSoFar);
                if (beta <= alpha) {
                    break;
                }
            }

            if (saveMove) {
                _lastFoundMove = bestMove;
            }
            return bestSoFar;

        } else {
            int bestSoFar = Integer.MAX_VALUE;
            Move bestMove = board.legalMoves(board.turn()).get(0);

            for (Move mv : board.legalMoves(board.turn())) {
                Board clone = new Board(board);
                clone.makeMove(mv);
                int val = findMove(clone, depth - 1,
                        false, sense * -1, alpha, beta);
                clone.undo();
                if (val < bestSoFar) {
                    bestMove = mv;
                }
                bestSoFar = min(bestSoFar, val);
                beta = min(bestSoFar, beta);
                if (beta <= alpha) {
                    break;
                }
            }

            if (saveMove) {
                _lastFoundMove = bestMove;
            }
            return bestSoFar;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        int numberPieces = board.pieceLocations(BLACK).size()
                 + board.pieceLocations(WHITE).size()
                 + board.pieceLocations(KING).size();
        if (numberPieces > DEPTHTWO) {
            return 2;
        } else if (numberPieces > DEPTHTHREE) {
            return 3;
        } else {
            return 4;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        if (board.kingPosition() == null) {
            return WINNING_VALUE * -1;
        } else if (board.kingPosition().isEdge()) {
            return WINNING_VALUE;
        }

        if (board.repeatedPosition()) {
            if (board.winner() == WHITE) {
                return WINNING_VALUE;
            } else if (board.winner() == BLACK) {
                return WINNING_VALUE * -1;
            }
        }

        Square kingPosition = board.kingPosition();
        int kingRow = kingPosition.row();
        int kingCol = kingPosition.col();
        if (board.isLegal(kingPosition, sq(kingCol, 0))
                || board.isLegal(kingPosition, sq(kingCol, 8))
                || board.isLegal(kingPosition, sq(0, kingRow))
                || board.isLegal(kingPosition, sq(8, kingRow))) {
            return WILL_WIN_VALUE;
        }

        int heuristic = 0;
        heuristic += 4 * board.pieceLocations(WHITE).size();
        heuristic -= 2 * board.pieceLocations(BLACK).size();

        int kingRowDist = min(kingRow, 8 - kingRow);
        int kingColDist = min(kingCol, 8 - kingCol);
        heuristic -= 2 * min(kingRowDist, kingColDist);

        int blackSurrounding = 0;
        for (int d = 0; d < 4; d++) {
            if (board.get(kingPosition.rookMove(d, 1)) == BLACK) {
                blackSurrounding += 1;
            }
        }
        if (board.get(kingPosition.diag1(
                kingPosition.rookMove(0, 1))) == BLACK) {
            blackSurrounding += 0.5;
        }
        if (board.get(kingPosition.diag2(
                kingPosition.rookMove(0, 1))) == BLACK) {
            blackSurrounding += 0.5;
        }
        if (board.get(kingPosition.diag1(
                kingPosition.rookMove(2, 1))) == BLACK) {
            blackSurrounding += 0.5;
        }
        if (board.get(kingPosition.diag2
                (kingPosition.rookMove(2, 1))) == BLACK) {
            blackSurrounding += 0.5;
        }
        heuristic -= 4 * blackSurrounding;

        return heuristic;

    }

}
