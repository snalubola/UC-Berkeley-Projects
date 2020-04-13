package tablut;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.Formatter;

import static tablut.Move.ROOK_MOVES;
import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Swadhin Nalubola
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        for (int col = 0; col < _board.length; col++) {
            for (int row = 0; row < _board[0].length; row++) {
                _board[col][row] = model.get(col, row);
            }
        }
        _turn = model.turn();
        _winner = model.winner();
        _moveCount = model.moveCount();
        _repeated = model.repeatedPosition();
        setMoveLimit(model._moveLimit);
        _kingPosition = model.kingPosition();
        _pastPositions = model.pastPositions();
        _undoStack = model.undoStack();
    }

    /** Clears the board to the initial position. */
    void init() {
        _board = new Piece[SIZE][SIZE];
        put(KING, THRONE);
        for (Square sq : INITIAL_DEFENDERS) {
            put(WHITE, sq);
        }
        for (Square sq : INITIAL_ATTACKERS) {
            put(BLACK, sq);
        }
        for (int col = 0; col < _board.length; col++) {
            for (int row = 0; row < _board[0].length; row++) {
                if (_board[col][row] == null) {
                    put(EMPTY, sq(col, row));
                }
            }
        }
        _turn = BLACK;
        _winner = null;
        _moveCount = 0;
        _repeated = false;
        _moveLimit = Integer.MAX_VALUE / 2;
        _kingPosition = THRONE;
        _pastPositions = new HashSet<String>();
        _pastPositions.add(encodedBoard());
        _undoStack = new Stack<>();
        _undoStack.push(new ArrayList<>(List.of(EMPTY, THRONE, -1)));
    }

    /** Set the move limit to N.  It is an error if 2*N <= moveCount(). */
    void setMoveLimit(int n) {
        _moveLimit = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        String currentPosition = encodedBoard();
        if (_pastPositions.contains(currentPosition)) {
            _repeated = true;
            _winner = turn();
        } else {
            _pastPositions.add(currentPosition);
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        return _kingPosition;
    }

    /** Return past positions. */
    HashSet<String> pastPositions() {
        return _pastPositions;
    }

    /** Return undo stack. */
    Stack<ArrayList<Object>> undoStack() {
        return _undoStack;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.col()][s.row()] = p;
        if (p == KING) {
            _kingPosition = s;
        }
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        Piece prevPiece = get(s);
        put(p, s);
        ArrayList<Object> prevState = new ArrayList<>();
        prevState.add(prevPiece);
        prevState.add(s);
        prevState.add(moveCount());
        _undoStack.push(prevState);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (!from.isRookMove(to)) {
            return false;
        }
        int fromIndex = from.index();
        int toDirection = from.direction(to);
        for (Square sq : ROOK_SQUARES[fromIndex][toDirection]) {
            if (get(sq) != EMPTY) {
                return false;
            }
            if (sq == to) {
                break;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (to == THRONE && get(from) != KING) {
            return false;
        }
        return isUnblockedMove(from, to) && isLegal(from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        if (moveCount() >= (2 * _moveLimit)) {
            if (turn() == BLACK) {
                _winner = WHITE;
            } else if (turn() == WHITE) {
                _winner = BLACK;
            }
        }

        Piece side = get(from);
        revPut(side, to);
        revPut(EMPTY, from);

        for (int dir = 0; dir < 4; dir++) {
            Square oppositeSquare = to.rookMove(dir, 2);
            if (oppositeSquare != null) {
                if (side == BLACK) {
                    makeMoveHelperBlack(to, oppositeSquare, side);
                } else if (side == WHITE || side == KING) {
                    makeMoveHelperWhite(to, oppositeSquare, side);
                }
            }
        }

        if (side == KING && to.isEdge()) {
            _winner = WHITE;
        }

        if (turn() == BLACK) {
            if (hasMove(WHITE)) {
                _turn = WHITE;
            } else {
                _winner = BLACK;
            }
        } else if (turn() == WHITE) {
            if (hasMove(BLACK)) {
                _turn = BLACK;
            } else {
                _winner = WHITE;
            }
        }
        _moveCount += 1;
        checkRepeated();
    }

    /** Helper function for capture conditions in makeMove.  A piece from
     *  SIDE == BLACK is moving to TO and may be able to capture the piece
     *  in OPPOSITESQUARE. */
    void makeMoveHelperBlack(Square to, Square oppositeSquare, Piece side) {

        if (get(oppositeSquare) == BLACK) {
            Piece toCapture = get(to.between(oppositeSquare));
            if (toCapture == WHITE) {
                capture(to, oppositeSquare);
            } else if (toCapture == KING) {
                if (kingPosition() == THRONE) {
                    int blackSurrounding = 0;
                    for (int d = 0; d < 4; d++) {
                        if (get(THRONE.rookMove(d, 1)) == BLACK) {
                            blackSurrounding += 1;
                        }
                    }
                    if (blackSurrounding == 4) {
                        capture(to, oppositeSquare);
                    }
                } else if (kingPosition() == NTHRONE
                        || kingPosition() == ETHRONE
                        || kingPosition() == STHRONE
                        || kingPosition() == WTHRONE) {
                    if (hostileAroundKing() == 4) {
                        capture(to, oppositeSquare);
                    }
                } else {
                    capture(to, oppositeSquare);
                }
            }
        } else if (oppositeSquare == THRONE) {
            if (get(THRONE) == EMPTY) {
                Piece toCapture = get(to.between(oppositeSquare));
                if (toCapture == WHITE) {
                    capture(to, oppositeSquare);
                } else if (toCapture == KING) {
                    if (hostileAroundKing() == 4) {
                        capture(to, oppositeSquare);
                    }
                }
            } else if (get(THRONE) == KING) {
                int blackSurrounding = 0;
                for (int d = 0; d < 4; d++) {
                    if (get(THRONE.rookMove(d, 1)) == BLACK) {
                        blackSurrounding += 1;
                    }
                }
                if (blackSurrounding > 2) {
                    Piece toCapture = get(to.between(oppositeSquare));
                    if (toCapture == WHITE || toCapture == KING) {
                        capture(to, oppositeSquare);
                    }
                }
            }
        }
    }

    /** Return the number of hostile squares around the king. */
    int hostileAroundKing() {
        int hostileSurrounding = 0;
        for (int d = 0; d < 4; d++) {
            if (get(kingPosition().rookMove(d, 1)) == BLACK) {
                hostileSurrounding += 1;
            } else if (kingPosition().rookMove(d, 1) == THRONE) {
                hostileSurrounding += 1;
            }
        }
        return hostileSurrounding;
    }

    /** Helper function for capture conditions in makeMove.  A piece from
     *  SIDE == WHITE is moving to TO and may be able to capture the piece
     *  in OPPOSITESQUARE. */
    void makeMoveHelperWhite(Square to, Square oppositeSquare, Piece side) {

        if (get(oppositeSquare) == WHITE || get(oppositeSquare) == KING) {
            Piece toCapture = get(to.between(oppositeSquare));
            if (toCapture == BLACK) {
                capture(to, oppositeSquare);
            }
        } else if (oppositeSquare == THRONE) {
            Piece toCapture = get(to.between(oppositeSquare));
            if (toCapture == BLACK) {
                capture(to, oppositeSquare);
            }
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Piece toCapture = get(sq0.between(sq2));
        revPut(EMPTY, sq0.between(sq2));
        if (toCapture == KING) {
            _winner = BLACK;
            _kingPosition = null;
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            int prevMoveCount = (int) _undoStack.peek().get(2);
            while ((int) _undoStack.peek().get(2) == prevMoveCount) {
                ArrayList<Object> prevState = _undoStack.pop();
                Piece prevPiece = (Piece) prevState.get(0);
                Square prevSquare = (Square) prevState.get(1);
                put(prevPiece, prevSquare);
            }
            if (turn() == BLACK) {
                _turn = WHITE;
            } else if (turn() == WHITE) {
                _turn = BLACK;
            }
            _moveCount -= 1;
            _winner = null;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (!repeatedPosition()) {
            _pastPositions.remove(encodedBoard());
        }
        _repeated = false;
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _undoStack.empty();
        _pastPositions.clear(


        );
        _pastPositions.add(encodedBoard());
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        ArrayList<Move> result = new ArrayList<>();
        HashSet<Square> pieceLocations = pieceLocations(side);
        for (Square sq : pieceLocations) {
            for (int d = 0; d < 4; d++) {
                Move.MoveList rookMoves = ROOK_MOVES[sq.index()][d];
                for (Move mv : rookMoves) {
                    if (isUnblockedMove(mv.from(), mv.to())
                        && mv.to() != THRONE) {
                        result.add(mv);
                    }
                }
            }
        }
        if (side == WHITE && kingPosition() != null) {
            Square kingPosition = kingPosition();
            for (int d = 0; d < 4; d++) {
                Move.MoveList rookMoves = ROOK_MOVES[kingPosition.index()][d];
                for (Move mv : rookMoves) {
                    if (isUnblockedMove(mv.from(), mv.to())) {
                        result.add(mv);
                    }
                }
            }
        }
        return result;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> result = new HashSet<>();
        for (int col = 0; col < _board.length; col++) {
            for (int row = 0; row < _board[0].length; row++) {
                if (_board[col][row] == side) {
                    result.add(sq(col, row));
                }
            }
        }
        return result;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or null if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** The maximum number of moves for a player during the current game. */
    private int _moveLimit;
    /** A representation of all the squares on the board and the piece that is
     *  in each of these squares. */
    private Piece[][] _board;
    /** The position of the King. */
    private Square _kingPosition;
    /** A record of all the positions that have been reached thus far. */
    private HashSet<String> _pastPositions;
    /** A stack containing previous moves. */
    private Stack<ArrayList<Object>> _undoStack;

}
