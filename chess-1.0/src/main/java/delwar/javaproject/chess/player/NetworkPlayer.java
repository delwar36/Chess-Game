package delwar.javaproject.chess.player;

import delwar.javaproject.chess.controll.Chessboard;
import delwar.javaproject.chess.controll.PlayerColor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public final class NetworkPlayer extends Player {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private volatile boolean running = true;

    public NetworkPlayer(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            while (running) {
                try {
                    if (in.available() > 0)
                        update(Chessboard.read(in));
                } catch (IOException ignored) {}
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    @Override
    public void sendUpdate(Chessboard chessboard) {
        if (chessboard.playerColor == color && !chessboard.isDraw() && !chessboard.isWin(PlayerColor.WHITE) && !chessboard.isWin(PlayerColor.BLACK)) {
            try {
                chessboard.write(out);
                out.flush();
            } catch (IOException ignored) {}

        }
    }

    @Override
    public void onClosed() {
        running = false;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ignored) {}
    }
}
