package delwar.javaproject.chess.controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ChessPiece {

    public final ChessPieceType chessPieceType;
    public final PlayerColor playerColor;
    private final int movements;
    private final int movedInTurn;
    private final boolean enPassant;

    ChessPiece(ChessPieceType chessPieceType, PlayerColor playerColor) {
        this(chessPieceType, playerColor, 0, 0, false);
    }

    private ChessPiece(ChessPieceType chessPieceType, PlayerColor playerColor, int movements, int movedInTurn, boolean enPassant) {
        this.chessPieceType = chessPieceType;
        this.playerColor = playerColor;
        this.movements = movements;
        this.movedInTurn = movedInTurn;
        this.enPassant = enPassant;
    }

    int[][] getMove() {
        return chessPieceType.getMove(notMoved());
    }

    int[][] getCapture() {
        return chessPieceType.capture;
    }

    ChessPiece moved(int turn, boolean enPassant) {
        return new ChessPiece(chessPieceType, playerColor, movements + 1, turn, enPassant);
    }

    ChessPiece promotion(ChessPieceType chessPieceType) {
        return new ChessPiece(chessPieceType, playerColor, movements, movedInTurn, false);
    }

    boolean notMoved() {
        return movements == 0;
    }

    boolean checkEnPassant(int turn) {
        return turn == movedInTurn + 1 && enPassant;
    }

    boolean checkPromotion(int y) {
        return chessPieceType == ChessPieceType.PAWN && (playerColor == PlayerColor.WHITE ? 0 : 7) == y;
    }

    @Override
    public String toString() {
        return chessPieceType.toString() + playerColor.toString();
    }

    char toChar() {
        return playerColor == PlayerColor.WHITE ? chessPieceType.symbolWhite : chessPieceType.symbolBlack;
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeByte(chessPieceType.ordinal());
        stream.writeByte(playerColor.ordinal());
        stream.writeInt(movements);
        stream.writeInt(movedInTurn);
        stream.writeBoolean(enPassant);
    }

    public static ChessPiece read(DataInputStream stream) throws IOException {
        return new ChessPiece(ChessPieceType.values()[stream.readByte()], PlayerColor.values()[stream.readByte()], stream.readInt(), stream.readInt(), stream.readBoolean());
    }
}