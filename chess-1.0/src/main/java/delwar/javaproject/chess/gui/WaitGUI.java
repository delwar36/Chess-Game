package delwar.javaproject.chess.gui;

import delwar.javaproject.chess.controll.PlayerColor;
import delwar.javaproject.chess.localisation.Localisation;
import delwar.javaproject.chess.player.LocalPlayer;
import delwar.javaproject.chess.player.NetworkPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

final class WaitGUI extends GUI implements WindowListener {

    private final ServerSocket socket;
    private JLabel label;

    WaitGUI(ServerSocket socket, PlayerColor playerColor) {
        super();
        this.socket = socket;
        label.setText(String.format(Localisation.getString("wait.text"), socket.getLocalPort()));

        new Thread(() -> {
            try {
                Socket s = socket.accept();
                OutputStream out = s.getOutputStream();
                out.write(playerColor.otherColor().ordinal());
                out.flush();

                if (playerColor == PlayerColor.WHITE)
                    new ChessboardGUI(new LocalPlayer(), new NetworkPlayer(s)).setVisible(true);
                else
                    new ChessboardGUI(new NetworkPlayer(s), new LocalPlayer()).setVisible(true);
                dispose();
            } catch (IOException ignored) {}
        }).start();
    }

    @Override
    void initGUI() {
        setTitle(Localisation.getString("wait.title"));
        setResizable(false);
        setPreferredSize(new Dimension(300, 100));
        addWindowListener(this);

        panel.setLayout(new BorderLayout());

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    void initListeners() {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        try {
            socket.close();
        } catch (IOException ignore) {}
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
