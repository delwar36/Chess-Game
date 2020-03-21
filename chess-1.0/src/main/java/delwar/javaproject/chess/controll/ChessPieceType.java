package delwar.javaproject.chess.controll;

public enum ChessPieceType {

    KING('\u2654', '\u265A', new int[][] {
            { 0, 1, },
            { 0, -1 },
            { 1, 0 },
            { -1, 0 },
            { 1, 1 },
            { 1, -1 },
            { -1, 1 },
            { -1, -1 }
    }, 20000, new int[][]{
            { -30, -30, -30, -30, -20, -10, 20, 20 },
            { -40, -40, -40, -40, -30, -20, 20, 30 },
            { -40, -40, -40, -40, -30, -20,  0, 10 },
            { -50, -50, -50, -50, -40, -20,  0,  0 },
            { -50, -50, -50, -50, -40, -20,  0,  0 },
            { -40, -40, -40, -40, -30, -20,  0, 10 },
            { -40, -40, -40, -40, -30, -20, 20, 30 },
            { -30, -30, -30, -30, -20, -10, 20, 20 }
    }, new int[][]{
            { -50, -30, -30, -30, -30, -30, -30, -50 },
            { -40, -20, -10, -10, -10, -10, -30, -30 },
            { -30, -10,  20,  30,  30,  20,   0, -30 },
            { -20,   0,  30,  40,  40,  30,   0, -30 },
            { -20,   0,  30,  40,  40,  30,   0, -30 },
            { -30, -10,  20,  30,  30,  20,   0, -30 },
            { -40, -20, -10, -10, -10, -10, -30, -30 },
            { -50, -30, -30, -30, -30, -30, -30, -50 }
    }),
    QUEEN('\u2655', '\u265B', new int[][] {
            { 0, 1, 7 },
            { 0, -1, 7 },
            { 1, 0, 7 },
            { -1, 0, 7 },
            { 1, 1, 7 },
            { 1, -1, 7 },
            { -1, 1, 7 },
            { -1, -1, 7 }
    }, 900, new int[][]{
            { -20, -10, -10, -5, 0, -10, -10, -20 },
            { -10,   0,   0,  0, 0,   5,   0, -10 },
            { -10,   0,   5,  5, 5,   5,   5, -10 },
            {  -5,   0,   5,  5, 5,   5,   0,  -5 },
            {  -5,   0,   5,  5, 5,   5,   0,  -5 },
            { -10,   0,   5,  5, 5,   5,   0, -10 },
            { -10,   0,   0,  0, 0,   0,   0, -10 },
            { -20, -10, -10, -5, -5, -10, -10, -20 }
    }),
    ROOK('\u2656', '\u265C', new int[][] {
            { 0, 1, 7 },
            { 0, -1, 7 },
            { 1, 0, 7 },
            { -1, 0, 7 }
    }, 500, new int[][]{
            { 0,  5, -5, -5, -5, -5, -5, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0, 10,  0,  0,  0,  0,  0, 0 },
            { 0,  5, -5, -5, -5, -5, -5, 0 }
    }),
    BISHOP('\u2657', '\u265D', new int[][] {
            { 1, 1, 7 },
            { 1, -1, 7 },
            { -1, 1, 7 },
            { -1, -1, 7 }
    }, 330, new int[][] {
            { -20, -10, -10, -10, -10, -10, -10, -20 },
            { -10,   0,   0,   5,   0,  10,   5, -10 },
            { -10,   0,   5,   5,  10,  10,   0, -10 },
            { -10,   0,  10,  10,  10,  10,   0, -10 },
            { -10,   0,  10,  10,  10,  10,   0, -10 },
            { -10,   0,   5,   5,  10,  10,   0, -10 },
            { -10,   0,   0,   5,   0,  10,   5, -10 },
            { -20, -10, -10, -10, -10, -10, -10, -20 }
    }),
    KNIGHT('\u2658', '\u265E', new int[][] {
            { 1, 2 },
            { -1, 2 },
            { 1, -2 },
            { -1, -2 },
            { 2, 1 },
            { 2, -1 },
            { -2, 1 },
            { -2, -1 }
    }, 320, new int[][]{
            { -50, -40, -30, -30, -30, -30, -40, -50 },
            { -40, -20,   0,   5,   0,   5, -20, -40 },
            { -30,   0,  10,  15,  15,  10,   0, -30 },
            { -30,   0,  15,  20,  20,  15,   0, -30 },
            { -30,   0,  15,  20,  20,  15,   0, -30 },
            { -30,   0,  10,  15,  15,  10,   0, -30 },
            { -40, -20,   0,   5,   0,   5, -20, -40 },
            { -50, -40, -30, -30, -30, -30, -40, -50 }
    }),
    PAWN('\u2659', '\u265F', new int[][] {
            { 0, 1 }
    }, new int[][] {
            { 0, 1, 2 }
    }, new int[][] {
            { 1, 1 },
            { -1, 1 },
            { 1, 1, 1, 0 },
            { -1, 1, -1, 0 }
    }, 100, new int[][] {
            { 0, 50, 10,  5,  0,   5,   5, 0 },
            { 0, 50, 10,  5,  0,  -5,  10, 0 },
            { 0, 50, 20, 10,  0, -10,  10, 0 },
            { 0, 50, 30, 25, 20,   0, -20, 0 },
            { 0, 50, 30, 25, 20,   0, -20, 0 },
            { 0, 50, 20, 10,  0, -10 , 10, 0 },
            { 0, 50, 10,  5,  0,  -5,  10, 0 },
            { 0, 50, 10,  5,  0,   5,   5, 0 },
    });

    public static final ChessPieceType[] POSITIONS_FIRST_ROW = { ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK };
    public static final ChessPieceType[] POSITIONS_PROMOTION = { QUEEN, KNIGHT, ROOK, BISHOP };

    public final char symbolWhite;
    public final char symbolBlack;
    public final int[][] capture;
    private final int[][] move;
    private final int[][] moveFirstTurn;
    public final int value; // relative value
    private final int[][] squareValues; // bonuses for pieces standing well and penalties for pieces standing badly
    private final int[][] squareValuesEndGame;

    ChessPieceType(char symbolWhite, char symbolBlack, int[][] move, int value, int[][] squareValues) {
        this(symbolWhite, symbolBlack, move, move, move, value, squareValues);
    }

    ChessPieceType(char symbolWhite, char symbolBlack, int[][] move, int value, int[][] squareValues, int[][] squareValuesEndGame) {
        this(symbolWhite, symbolBlack, move, move, move, value, squareValues, squareValuesEndGame);
    }

    ChessPieceType(char symbolWhite, char symbolBlack, int[][] move, int[][] moveFirstTurn, int[][] capture, int value, int[][] squareValues) {
        this(symbolWhite, symbolBlack, move, moveFirstTurn, capture, value, squareValues, squareValues);
    }

    ChessPieceType(char symbolWhite, char symbolBlack, int[][] move, int[][] moveFirstTurn, int[][] capture, int value, int[][] squareValues, int[][] squareValuesEndGame) {
        this.symbolWhite = symbolWhite;
        this.symbolBlack = symbolBlack;
        this.move = move;
        this.moveFirstTurn = moveFirstTurn;
        this.capture = capture;
        this.value = value;
        this.squareValues = squareValues;
        this.squareValuesEndGame = squareValuesEndGame;
    }

    public int[][] getMove(boolean notMoved) {
        return notMoved ? moveFirstTurn : move;
    }

    public int[][] getSquareValues(boolean endGame) {
        return endGame ? squareValuesEndGame : squareValues;
    }
}
