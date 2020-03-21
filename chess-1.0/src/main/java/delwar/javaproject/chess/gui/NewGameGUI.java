package delwar.javaproject.chess.gui;

import delwar.javaproject.chess.controll.PlayerColor;
import delwar.javaproject.chess.localisation.Localisation;
import delwar.javaproject.chess.player.ComputerPlayer;
import delwar.javaproject.chess.player.LocalPlayer;
import delwar.javaproject.chess.player.NetworkPlayer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public final class NewGameGUI extends GUI {

    private JButton startButton;
    private JRadioButton onePlayerRadioButton;
    private JRadioButton twoPlayerRadioButton;
    private JTextField hostTextField;
    private JTextField portTextField;
    private JComboBox<String> twoPlayerModeComboBox;
    private JComboBox<String> colorComboBox;
    private JLabel colorLabel;
    private JLabel hostLabel;
    private JLabel portLabel;

    @Override
    void initGUI() {
        setTitle(Localisation.getString("new_game.title"));
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        GridBagConstraints c = new GridBagConstraints();

        ButtonGroup buttonGroup = new ButtonGroup();

        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 2;
        panel.add(onePlayerRadioButton = new JRadioButton(Localisation.getString("new_game.one_player")), c);
        buttonGroup.add(onePlayerRadioButton);
        onePlayerRadioButton.setSelected(true);
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridheight = 3;
        panel.add(Box.createHorizontalStrut(10), c);
        c.gridheight = 1;
        c.gridx = 3;
        panel.add(colorLabel = new JLabel(Localisation.getString("new_game.color")), c);
        c.gridx = 4;
        panel.add(colorComboBox = new JComboBox<>(new String[] { Localisation.getString("player_color.white"), Localisation.getString("player_color.black") }), c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 2;
        panel.add(twoPlayerRadioButton = new JRadioButton(Localisation.getString("new_game.two_players")), c);
        buttonGroup.add(twoPlayerRadioButton);
        c.gridx = 3;
        c.gridwidth = 1;
        panel.add(hostLabel = new JLabel(Localisation.getString("new_game.host")), c);
        c.gridx = 4;
        panel.add(hostTextField = new JTextField("localhost"), c);
        hostTextField.setPreferredSize(new Dimension(150, -1));

        c.gridy = 2;
        c.gridx = 0;
        panel.add(Box.createHorizontalStrut(18), c);
        c.gridx = 1;
        panel.add(twoPlayerModeComboBox = new JComboBox<>(new String[] { Localisation.getString("new_game.local"), Localisation.getString("new_game.server"), Localisation.getString("new_game.client") }), c);
        c.gridx = 3;
        panel.add(portLabel = new JLabel(Localisation.getString("new_game.port")), c);
        c.gridx = 4;
        panel.add(portTextField = new JTextField("49152"), c);

        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 5;
        panel.add(startButton = new JButton(Localisation.getString("new_game.start")), c);
        getRootPane().setDefaultButton(startButton);

        updateEnabledStatus();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    @Override
    void initListeners() {
        // listener to update enabled status
        ItemListener listener = e -> updateEnabledStatus();
        onePlayerRadioButton.addItemListener(listener);
        twoPlayerRadioButton.addItemListener(listener);
        twoPlayerModeComboBox.addItemListener(listener);

        // listener for start button
        startButton.addActionListener(e -> onStartButtonPressed());

        // listener to change the color of the port text field
        portTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            void update() {
                try {
                    int port = Integer.parseInt(portTextField.getText());
                    portTextField.setForeground(port < 49152 || port > 65535 ? Color.ORANGE : Color.BLACK);
                } catch (NumberFormatException e) {
                    portTextField.setForeground(Color.RED);
                }
            }
        });
    }

    private void onStartButtonPressed() {
        if (onePlayerRadioButton.isSelected()) {
            // player vs computer
            if (colorComboBox.getSelectedIndex() == 0)
                new ChessboardGUI(new LocalPlayer(), new ComputerPlayer()).setVisible(true);
            else
                new ChessboardGUI(new ComputerPlayer(), new LocalPlayer()).setVisible(true);
        } else {
            if (twoPlayerModeComboBox.getSelectedIndex() == 0) {
                // 2 Players local
                new ChessboardGUI(new LocalPlayer(), new LocalPlayer()).setVisible(true);
            } else if(twoPlayerModeComboBox.getSelectedIndex() == 1) {
                for (int port = 49152; port <= 65535; port++) {
                    try {
                        ServerSocket socket = new ServerSocket(port);
                        new WaitGUI(socket, PlayerColor.values()[colorComboBox.getSelectedIndex()]).setVisible(true);
                        break;
                    } catch (IOException ignored) {}
                }
            } else {
                try {
                    int port = Integer.parseInt(portTextField.getText());
                    if (port >= 49152 && port <= 65535) {
                        new Thread(() -> {
                            try {
                                Socket socket = new Socket(hostTextField.getText(), port);
                                InputStream in = socket.getInputStream();
                                while (true) {
                                    if (in.available() > 0)
                                        break;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ignored) {}
                                }
                                PlayerColor playerColor = PlayerColor.values()[in.read()];
                                if (playerColor == PlayerColor.WHITE)
                                    new ChessboardGUI(new LocalPlayer(), new NetworkPlayer(socket)).setVisible(true);
                                else
                                    new ChessboardGUI(new NetworkPlayer(socket), new LocalPlayer()).setVisible(true);
                            } catch (IOException ignored) {}
                        }).start();
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        dispose();
    }

    private void updateEnabledStatus() {
        twoPlayerModeComboBox.setEnabled(twoPlayerRadioButton.isSelected());

        boolean enabled = onePlayerRadioButton.isSelected() || twoPlayerRadioButton.isSelected() && twoPlayerModeComboBox.getSelectedIndex() == 1;
        colorLabel.setEnabled(enabled);
        colorComboBox.setEnabled(enabled);

        enabled = twoPlayerRadioButton.isSelected() && twoPlayerModeComboBox.getSelectedIndex() == 2;
        hostLabel.setEnabled(enabled);
        hostTextField.setEnabled(enabled);
        portLabel.setEnabled(enabled);
        portTextField.setEnabled(enabled);

    }
}