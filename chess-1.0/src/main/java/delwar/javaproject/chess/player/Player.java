package delwar.javaproject.chess.player;

import delwar.javaproject.chess.gui.ChessboardGUI;
import delwar.javaproject.chess.controll.Chessboard;
import delwar.javaproject.chess.controll.PlayerColor;

import javax.swing.*;

public abstract class Player {

    PlayerColor color;
    ChessboardGUI gui;

    public void init(PlayerColor color, ChessboardGUI gui) {
        this.color = color;
        this.gui = gui;
    }

    public abstract void sendUpdate(Chessboard chessboard);

    void update(Chessboard chessboard) {
        SwingUtilities.invokeLater(() -> gui.externalUpdate(chessboard));
    }

    public abstract void onClosed();
}
