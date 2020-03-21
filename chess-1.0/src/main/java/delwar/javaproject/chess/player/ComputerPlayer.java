package delwar.javaproject.chess.player;

import delwar.javaproject.chess.controll.ChessEngine;
import delwar.javaproject.chess.controll.Chessboard;
import delwar.javaproject.chess.controll.PlayerColor;

public final class ComputerPlayer extends Player {

    @Override
    public void sendUpdate(Chessboard chessboard) {
        if (chessboard.playerColor == color && !chessboard.isDraw() && !chessboard.isWin(PlayerColor.WHITE) && !chessboard.isWin(PlayerColor.BLACK)) {
            new Thread(() -> {
                update(ChessEngine.compute(chessboard));
            }).start();
        }
    }

    @Override
    public void onClosed() {

    }
}
