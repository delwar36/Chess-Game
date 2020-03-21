package delwar.javaproject.chess.controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class Chessboard {

    private static final ChessPiece[][] INITIAL_POSITIONS = new ChessPiece[8][8];

    static {
        for (int i = 0; i < 8; i++) {
            INITIAL_POSITIONS[i][0] = new ChessPiece(ChessPieceType.POSITIONS_FIRST_ROW[i], PlayerColor.BLACK);
            INITIAL_POSITIONS[i][1] = new ChessPiece(ChessPieceType.PAWN, PlayerColor.BLACK);
            INITIAL_POSITIONS[i][6] = new ChessPiece(ChessPieceType.PAWN, PlayerColor.WHITE);
            INITIAL_POSITIONS[i][7] = new ChessPiece(ChessPieceType.POSITIONS_FIRST_ROW[i], PlayerColor.WHITE);
        }
    }

    private final ChessPiece[][] chessPieces;
    private final int turn;
    public final PlayerColor playerColor;
    public final boolean promotion;
    private final int promotionX;
    private final int promotionY;
    final boolean endGame;
    public final Move lastMove;


    public Chessboard() {
        this(INITIAL_POSITIONS, 1, PlayerColor.WHITE, false, -1, -1, null);
    }

    private Chessboard(ChessPiece[][] chessPieces, int turn, PlayerColor playerColor, boolean promotion, int promotionX, int promotionY, Move lastMove) {
        this.chessPieces = chessPieces;
        this.turn = turn;
        this.playerColor = playerColor;
        this.promotion = promotion;
        this.promotionX = promotionX;
        this.promotionY = promotionY;
        this.endGame = (countChessPieces(PlayerColor.WHITE, ChessPieceType.QUEEN) == 0 ||
                (countChessPieces(PlayerColor.WHITE, ChessPieceType.ROOK) == 0 && countChessPieces(PlayerColor.WHITE, ChessPieceType.BISHOP, ChessPieceType.KNIGHT) <= 1))
                && (countChessPieces(PlayerColor.BLACK, ChessPieceType.QUEEN) == 0 ||
                (countChessPieces(PlayerColor.BLACK, ChessPieceType.ROOK) == 0 && countChessPieces(PlayerColor.BLACK, ChessPieceType.BISHOP, ChessPieceType.KNIGHT) <= 1));
        this.lastMove = lastMove;
    }

    public ArrayList<int[]> getPossibleMoves(int x, int y) {
        ArrayList<int[]> possibilities = new ArrayList<>();
        ChessPiece chessPiece = chessPieces[x][y];
        if (chessPiece != null) {
            for (int[] a : chessPiece.getCapture()) {
                if (a.length == 4) { // en passant
                    int gX = x + a[0];
                    int gY = y - a[1] * chessPiece.playerColor.sign;
                    int bX = x + a[2];
                    int bY = y - a[3] * chessPiece.playerColor.sign;
                    if (gX < 0 || gX > 7 || gY < 0 || gY > 7)
                        continue;
                    if (chessPieces[gX][gY] == null && chessPieces[bX][bY] != null && chessPieces[bX][bY].playerColor != chessPiece.playerColor && chessPieces[bX][bY].chessPieceType == ChessPieceType.PAWN && chessPieces[bX][bY].checkEnPassant(turn))
                        possibilities.add(new int[] { gX, gY, bX, bY });
                } else {
                    int extend = a.length == 3 ? a[2] : 1;
                    for (int i = 1; i <= extend; i++) {
                        int nX = x + i * a[0];
                        int nY = y - i * a[1] * chessPiece.playerColor.sign;
                        if (nX < 0 || nX > 7 || nY < 0 || nY > 7)
                            continue;
                        if (chessPieces[nX][nY] != null) {
                            if (chessPieces[nX][nY].playerColor != chessPiece.playerColor)
                                possibilities.add(new int[] { nX, nY });
                            break;
                        }
                    }
                }
            }
            for (int[] a : chessPiece.getMove()) {
                int extend = a.length == 3 ? a[2] : 1;
                for (int i = 1; i <= extend; i++) {
                    int nX = x + i * a[0];
                    int nY = y - i * a[1] * chessPiece.playerColor.sign;
                    if (nX < 0 || nX > 7 || nY < 0 || nY > 7)
                        continue;
                    if (chessPieces[nX][nY] == null)
                        possibilities.add(new int[] { nX, nY });
                    else
                        break;
                }
            }
            if (chessPiece.chessPieceType == ChessPieceType.KING) {
                if (chessPiece.notMoved()) {
                    if (chessPieces[7][y] != null && chessPieces[7][y].notMoved() && chessPieces[6][y] == null && chessPieces[5][y] == null && !isThreatenedField(5, y, chessPiece.playerColor.otherColor()))
                        possibilities.add(new int[] { 6, y, 7, y, 5, y });
                    if (chessPieces[0][y] != null && chessPieces[0][y].notMoved() && chessPieces[1][y] == null && chessPieces[2][y] == null && chessPieces[3][y] == null && !isThreatenedField(3, y, chessPiece.playerColor.otherColor()))
                        possibilities.add(new int[] { 2, y, 0, y, 3, y });
                }
            }
            possibilities.removeAll(possibilities.stream().filter(a -> move(new int[] { x, y }, a).inCheck(chessPiece.playerColor)).collect(Collectors.toCollection(ArrayList::new)));
        }
        return possibilities;
    }

    private ArrayList<int[]> getThreatenedFields(int x, int y) {
        ArrayList<int[]> possibilities = new ArrayList<>();
        ChessPiece chessPiece = chessPieces[x][y];
        if (chessPiece != null) {
            for (int[] a : chessPiece.getCapture()) {
                int extend = a.length == 3 ? a[2] : 1;
                for (int i = 1; i <= extend; i++) {
                    int nX = x + i * a[0];
                    int nY = y - i * a[1] * chessPiece.playerColor.sign;
                    if (nX < 0 || nX > 7 || nY < 0 || nY > 7)
                        continue;
                    if (chessPieces[nX][nY] == null) {
                        possibilities.add(new int[] { nX, nY });
                    } else {
                        possibilities.add(new int[] { nX, nY });
                        break;
                    }
                }
            }
        }
        return possibilities;
    }

    private boolean isThreatenedField(int x, int y, PlayerColor playerColor) {
        for (int i1 = 0; i1 < 8; i1++)
            for (int i2 = 0; i2 < 8; i2++)
                if (chessPieces[i1][i2] != null && chessPieces[i1][i2].playerColor == playerColor)
                    for (int[] a : getThreatenedFields(i1, i2))
                        if (a[0] == x && a[1] == y)
                            return true;
        return false;
    }

    Chessboard move(int[] from, int[] to) {
        Move move;
        ChessPiece[][] chessPieces = copy(this.chessPieces);
        move = new Move(from[0], from[1], to[0], to[1], this.chessPieces[from[0]][from[1]], to[0], to[1], chessPieces[to[0]][to[1]]);
        chessPieces[to[0]][to[1]] = this.chessPieces[from[0]][from[1]].moved(turn, Math.abs(from[1] - to[1]) > 1);
        chessPieces[from[0]][from[1]] = null;
        if (to.length == 4) {
            move = new Move(from[0], from[1], to[0], to[1], this.chessPieces[from[0]][from[1]], to[2], to[3], chessPieces[to[2]][to[3]]);
            chessPieces[to[2]][to[3]] = null;
        } else if (to.length == 6) {
            chessPieces[to[4]][to[5]] = this.chessPieces[to[2]][to[3]].moved(turn, false);
            chessPieces[to[2]][to[3]] = null;
        }
        boolean promotion = chessPieces[to[0]][to[1]].checkPromotion(to[1]);
        return new Chessboard(chessPieces, promotion ? turn : turn + 1, promotion ? playerColor : playerColor.otherColor(), promotion, promotion ? to[0] : -1, promotion ? to[1] : -1, move);
    }


    public Chessboard checkAndMove(int[] from, int[] to) {
        for (int[] t : getPossibleMoves(from[0], from[1]))
            if (t[0] == to[0] && t[1] == to[1])
                return move(from, t);
        return null;
    }

    private boolean inCheck(PlayerColor playerColor) {
        int[] pos = findChessPiece(ChessPieceType.KING, playerColor);
        return pos != null && isThreatenedField(pos[0], pos[1], playerColor.otherColor());
    }

    private int countChessPieces(PlayerColor playerColor, ChessPieceType... chessPieceTypes) {
        List<ChessPieceType> lChessPieceTypes = Arrays.asList(chessPieceTypes);
        int count = 0;

        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                if (chessPieces[x][y] != null && lChessPieceTypes.contains(chessPieces[x][y].chessPieceType) && chessPieces[x][y].playerColor == playerColor)
                    count++;

        return count;
    }

    private int[] findChessPiece(ChessPieceType chessPieceType, PlayerColor playerColor) {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                if (chessPieces[x][y] != null && chessPieces[x][y].chessPieceType == chessPieceType && chessPieces[x][y].playerColor == playerColor)
                    return new int[] { x, y };
        return null;
    }

    public Chessboard promotion(ChessPieceType chessPieceType) {
        ChessPiece[][] chessPieces = copy(this.chessPieces);
        chessPieces[promotionX][promotionY] = chessPieces[promotionX][promotionY].promotion(chessPieceType);
        return new Chessboard(chessPieces, turn + 1, playerColor.otherColor(), false, -1, -1, lastMove);
    }

    public ChessPiece getChessPiece(int x, int y) {
        return chessPieces[x][y];
    }

    @Override
    public String toString() {
        return Arrays.deepToString(chessPieces);
    }

    private ChessPiece[][] copy(ChessPiece[][] array) {
        ChessPiece[][] a = new ChessPiece[array.length][];
        for (int i = 0; i < a.length; i++)
            a[i] = Arrays.copyOf(array[i], array[i].length);
        return a;
    }

    public Chessboard cycleColor(int x, int y) {
        ChessPiece[][] chessPieces = copy(this.chessPieces);
        if (chessPieces[x][y] != null) {
            chessPieces[x][y] = new ChessPiece(chessPieces[x][y].chessPieceType, chessPieces[x][y].playerColor.otherColor());
        } else {
            chessPieces[x][y] = new ChessPiece(ChessPieceType.PAWN, PlayerColor.BLACK);
        }
        return new Chessboard(chessPieces, turn, playerColor, promotion, promotionX, promotionY, lastMove);
    }

    public Chessboard cycleChessPieceType(int x, int y) {
        ChessPiece[][] chessPieces = copy(this.chessPieces);
        if (chessPieces[x][y] != null) {
            if (chessPieces[x][y].chessPieceType == ChessPieceType.KING)
                chessPieces[x][y] = null;
            else
                chessPieces[x][y] = new ChessPiece(ChessPieceType.values()[(chessPieces[x][y].chessPieceType.ordinal()- 1)], chessPieces[x][y].playerColor);
        } else {
            chessPieces[x][y] = new ChessPiece(ChessPieceType.PAWN, PlayerColor.WHITE);
        }
        return new Chessboard(chessPieces, turn, playerColor, promotion, promotionX, promotionY, lastMove);
    }

    private boolean canMove() {
        for (int i1 = 0; i1 < 8; i1++)
            for (int i2 = 0; i2 < 8; i2++)
                if (chessPieces[i1][i2] != null && chessPieces[i1][i2].playerColor == playerColor && !getPossibleMoves(i1, i2).isEmpty())
                    return true;
        return false;
    }

    public boolean isDraw() {
        return !canMove() && !inCheck(playerColor);
    }

    public boolean isWin(PlayerColor color) {
        return color != playerColor && !canMove() && inCheck(playerColor);
    }

    public void write(DataOutputStream stream) throws IOException {
        for (int i1 = 0; i1 < 8; i1++) {
            for (int i2 = 0; i2 < 8; i2++) {
                stream.writeBoolean(chessPieces[i1][i2] != null);
                if (chessPieces[i1][i2] != null)
                    chessPieces[i1][i2].write(stream);
            }
        }
        stream.writeInt(turn);
        stream.writeByte(playerColor.ordinal());
        stream.writeBoolean(promotion);
        stream.writeInt(promotionX);
        stream.writeInt(promotionY);
        stream.writeBoolean(lastMove != null);
        if (lastMove != null)
            lastMove.write(stream);
    }

    public static Chessboard read(DataInputStream stream) throws IOException {
        ChessPiece[][] chessPieces = new ChessPiece[8][8];
        for (int i1 = 0; i1 < 8; i1++)
            for (int i2 = 0; i2 < 8; i2++)
                if (stream.readBoolean())
                    chessPieces[i1][i2] = ChessPiece.read(stream);
        return new Chessboard(chessPieces, stream.readInt(), PlayerColor.values()[stream.readByte()], stream.readBoolean(), stream.readInt(), stream.readInt(), stream.readBoolean() ? Move.read(stream) : null);
    }
}
