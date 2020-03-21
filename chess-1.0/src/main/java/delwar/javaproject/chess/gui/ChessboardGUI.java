package delwar.javaproject.chess.gui;

import delwar.javaproject.chess.icon.Icons;
import delwar.javaproject.chess.controll.*;
import delwar.javaproject.chess.localisation.Localisation;
import delwar.javaproject.chess.player.ComputerPlayer;
import delwar.javaproject.chess.player.LocalPlayer;
import delwar.javaproject.chess.player.Player;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public final class ChessboardGUI extends GUI implements WindowListener {

    private static final Border BORDER_INSERTS = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private static final Border BORDER_BEVEL_RAISED = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    private static final Border BORDER_BEVEL_LOWERED = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    private static final Border BORDER_SIDE_PANEL = BorderFactory.createCompoundBorder(BORDER_BEVEL_RAISED, BORDER_BEVEL_LOWERED);

    private static final Font FONT_LABEL = new Font(null, 0, 50);
    private static final Font FONT_LABEL_PLAYER = new Font(null, Font.BOLD, 45);
    private static final Font FONT_LABEL_TIME = new Font(null, 0, 35);
    private static final Font FONT_LABEL_EVALUATION = new Font(null, 0, 25);
    private static final Font FONT_LABEL_HISTORY = new Font(null, 0, 20);

    private ChessboardButton[][] boardButtons;
    private JButton[] promotionButtons;
    private JLabel[] topLabels;
    private JLabel[] leftLabels;
    private JLabel playerLabel;
    private JLabel timerLabel;
    private JLabel evaluationLabel;
    private JPanel chessboardPanel;
    private JPanel promotionPanel;
    private JPanel historyPanel;

    private JMenuItem newMenuItem;
    private JCheckBoxMenuItem cheatCheckBoxMenuItem;
    private JRadioButtonMenuItem normalRadioButtonMenuItem;
    private JRadioButtonMenuItem reversedRadioButtonMenuItem;
    private JRadioButtonMenuItem automaticRadioButtonMenuItem;

    private int time;
    private final Player[] players;
    private Chessboard chessboard;
    private int[] selected;
    private boolean reversed;

    private final TimerThread timerThread = new TimerThread();

    ChessboardGUI(Player player1, Player player2) {
        super();

        addWindowListener(this);

        players = new Player[] { player1, player2 };
        players[0].init(PlayerColor.WHITE, this);
        players[1].init(PlayerColor.BLACK, this);

        if (players[0] instanceof ComputerPlayer) // compute first turn
            players[0].sendUpdate(chessboard);

        cheatCheckBoxMenuItem.setEnabled(players[0] instanceof LocalPlayer && players[1] instanceof LocalPlayer);

        timerThread.start();
    }

    @Override
    void initGUI() {
        setTitle(Localisation.getString("chessboard.title"));
        panel.setLayout(new BorderLayout());
        JPanel chessboardMPanel = new JPanel();
        panel.add(chessboardMPanel, BorderLayout.CENTER);

        // initialise menu bar
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu gameMenu = new JMenu(Localisation.getString("chessboard.menu_bar.game"));
        menu.add(gameMenu);
        gameMenu.add(newMenuItem = new JMenuItem(Localisation.getString("chessboard.menu_bar.game.new")));
        gameMenu.addSeparator();
        gameMenu.add(cheatCheckBoxMenuItem = new JCheckBoxMenuItem(Localisation.getString("chessboard.menu_bar.game.cheat_mode")));

        JMenu chessboardMenu = new JMenu(Localisation.getString("chessboard.menu_bar.chessboard"));
        menu.add(chessboardMenu);
        ButtonGroup buttonGroup = new ButtonGroup();
        chessboardMenu.add(normalRadioButtonMenuItem = new JRadioButtonMenuItem(Localisation.getString("chessboard.menu_bar.chessboard.normal")));
        buttonGroup.add(normalRadioButtonMenuItem);
        normalRadioButtonMenuItem.setSelected(true);
        chessboardMenu.add(reversedRadioButtonMenuItem = new JRadioButtonMenuItem(Localisation.getString("chessboard.menu_bar.chessboard.reversed")));
        buttonGroup.add(reversedRadioButtonMenuItem);
        chessboardMenu.add(automaticRadioButtonMenuItem = new JRadioButtonMenuItem(Localisation.getString("chessboard.menu_bar.chessboard.automatic")));
        buttonGroup.add(automaticRadioButtonMenuItem);

        chessboard = new Chessboard();

        // initialise chessboard
        chessboardPanel = new JPanel(new GridLayout(9, 9));
        chessboardMPanel.add(chessboardPanel);
        chessboardMPanel.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                int size =  Math.min(chessboardMPanel.getWidth(), chessboardMPanel.getHeight());
                chessboardPanel.setPreferredSize(new Dimension(size, size));
                chessboardMPanel.revalidate();
            }
        });

        boardButtons = new ChessboardButton[8][8];
        promotionButtons = new JButton[4];
        topLabels = new JLabel[8];
        leftLabels = new JLabel[8];

        chessboardPanel.add(new JLabel());
        for (int i = 0; i < 8; i++)
            chessboardPanel.add(topLabels[i] = newLabel(Character.toString((char) (0x61 + i))));
        for (int y = 0; y < 8; y++) {
            chessboardPanel.add(leftLabels[y] = newLabel(Integer.toString(8 - y)));
            for (int x = 0; x < 8; x++)
                chessboardPanel.add(boardButtons[x][y] = new ChessboardButton());
        }

        // initialise side panel
        JPanel sidePanelO = new JPanel(new BorderLayout());
        panel.add(sidePanelO, BorderLayout.LINE_END);
        sidePanelO.setBorder(BORDER_INSERTS);

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanelO.add(sidePanel, BorderLayout.CENTER);
        sidePanel.setBorder(BORDER_SIDE_PANEL);

        JScrollPane scrollPane = new JScrollPane(historyPanel = new JPanel());
        scrollPane.setBorder(null);
        sidePanel.add(scrollPane, BorderLayout.CENTER);
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

        JPanel sideTopPanel = new JPanel();
        sideTopPanel.setLayout(new BoxLayout(sideTopPanel, BoxLayout.Y_AXIS));
        sidePanel.add(sideTopPanel, BorderLayout.PAGE_START);

        sideTopPanel.add(Box.createHorizontalStrut(210));
        playerLabel = new JLabel();
        playerLabel.setFont(FONT_LABEL_PLAYER);
        playerLabel.setBorder(BORDER_INSERTS);
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideTopPanel.add(playerLabel);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(FONT_LABEL_TIME);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideTopPanel.add(timerLabel);

        evaluationLabel = new JLabel();
        evaluationLabel.setFont(FONT_LABEL_EVALUATION);
        evaluationLabel.setBorder(BORDER_INSERTS);
        evaluationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideTopPanel.add(evaluationLabel);


        // add promotion buttons
        promotionPanel = new JPanel(new GridLayout(2, 2));
        promotionPanel.setBorder(BORDER_INSERTS);
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            button.setContentAreaFilled(false);
            button.setBorder(null);
            promotionPanel.add(promotionButtons[i] = button);
        }
        sidePanel.add(promotionPanel, BorderLayout.PAGE_END);

        // setup background
        updateBackground();

        // setup icons
        updateIcons();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    void initListeners() {
        // menu bar listeners
        newMenuItem.addActionListener(e -> {
            // Open NewGameGUI Window
            new NewGameGUI().setVisible(true);
        });
        ItemListener listener = e -> {
            if (normalRadioButtonMenuItem.isSelected())
                reversed = false;
            else if (reversedRadioButtonMenuItem.isSelected())
                reversed = true;
            updateInverted();
            updateIcons();
            updateBackground();
        };
        normalRadioButtonMenuItem.addItemListener(listener);
        reversedRadioButtonMenuItem.addItemListener(listener);
        automaticRadioButtonMenuItem.addItemListener(listener);

        // chessboard listeners
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                final int fX = x, fY = y;
                boardButtons[x][y].addActionListener(e -> buttonPressed(fX, reversed ? 7 - fY : fY));
                boardButtons[x][y].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (cheatCheckBoxMenuItem.isSelected()) {
                            if (e.getButton() == MouseEvent.BUTTON2) { // middle click
                                chessboard = chessboard.cycleColor(fX, reversed ? 7 - fY : fY);
                                selected = null;
                                updateIcons();
                                updateBackground();
                            } else if (e.getButton() == MouseEvent.BUTTON3) { // right click
                                chessboard = chessboard.cycleChessPieceType(fX, reversed ? 7 - fY : fY);
                                selected = null;
                                updateIcons();
                                updateBackground();
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setLabels(fX, fY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        resetLabels(fX, fY);
                    }
                });
            }
        }
        for (int i = 0; i < 4; i++) {
            final int fI = i;
            promotionButtons[i].addActionListener(e -> promotionButtonPressed(fI));
        }
    }

    private void resetLabels(int x, int y) {
        Font font = topLabels[x].getFont();
        topLabels[x].setFont(new Font(font.getName(), font.getStyle() & ~Font.BOLD, font.getSize()));
        font = leftLabels[y].getFont();
        leftLabels[y].setFont(new Font(font.getName(), font.getStyle() & ~Font.BOLD, font.getSize()));
    }

    private void setLabels(int x, int y) {
        Font font = topLabels[x].getFont();
        topLabels[x].setFont(new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize()));
        font = leftLabels[y].getFont();
        leftLabels[y].setFont(new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize()));
    }

    private void buttonPressed(int x, int y) {
        if (!chessboard.promotion) {
            if (chessboard.getChessPiece(x, y) != null && chessboard.getChessPiece(x, y).playerColor == chessboard.playerColor && players[chessboard.playerColor.ordinal()] instanceof LocalPlayer && !chessboard.getPossibleMoves(x, y).isEmpty()) {
                if (selected != null && x == selected[0] && y == selected[1])
                    selected = null;
                else
                    selected = new int[] { x, y };
                updateBackground();
            } else if (selected != null) {
                Chessboard move = chessboard.checkAndMove(selected, new int[] { x, y });
                if (move != null) {
                    chessboard = move;
                    for (Player p : players) p.sendUpdate(chessboard);
                    selected = null;
                    updateInverted();
                    updateIcons();
                    updateBackground();
                    if (!chessboard.promotion)
                        updateGameState();
                }
            }
        }
    }

    private void promotionButtonPressed(int i) {
        if (chessboard.promotion && players[chessboard.playerColor.ordinal()] instanceof LocalPlayer) {
            chessboard = chessboard.promotion(ChessPieceType.POSITIONS_PROMOTION[i]);
            for (Player p : players) p.sendUpdate(chessboard);
            updateInverted();
            updateIcons();
            chessboardPanel.repaint();
            updateGameState();
        }
    }

    public void externalUpdate(Chessboard chessboard) {
        this.chessboard = chessboard;
        for (Player p : players) p.sendUpdate(chessboard);
        updateInverted();
        updateIcons();
        chessboardPanel.repaint();
        updateGameState();
    }

    private void updateBackground() {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                boardButtons[x][y].iconReset(((x ^ y) & 1) == 0 ^ reversed);
        if (selected != null) {
            boardButtons[selected[0]][reversed ? 7 - selected[1] : selected[1]].iconSelected();
            for (int[] m : chessboard.getPossibleMoves(selected[0], selected[1]))
                boardButtons[m[0]][reversed ? 7 - m[1] : m[1]].iconShow();
        }
        chessboardPanel.repaint();
    }

    private JLabel newLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(FONT_LABEL);
        label.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                Font font = label.getFont();
                label.setFont(new Font(font.getName(), font.getStyle(), (int) (label.getSize().height * 0.8)));
            }
        });
        return label;
    }

    private void updateIcons() {
        // update chessboard icons
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ChessPiece chessPiece = chessboard.getChessPiece(x, y);
                boardButtons[x][reversed ? 7 - y : y].setIcon(chessPiece != null ? Icons.getIcon(chessPiece.playerColor, chessPiece.chessPieceType) : null);
            }
        }

        // update promotion icons
        for (int i = 0; i < 4; i++)
            promotionButtons[i].setIcon(Icons.getIcon(chessboard.playerColor, ChessPieceType.POSITIONS_PROMOTION[i]));
        promotionPanel.setVisible(chessboard.promotion && players[chessboard.playerColor.ordinal()] instanceof LocalPlayer);

        playerLabel.setText(Localisation.getString("player_color." + chessboard.playerColor.toString().toLowerCase()));

        int value = ChessEngine.computeValue(chessboard);
        evaluationLabel.setText(String.format("%s%d.%02d", value > 0 ? "+" : value < 0 ? "-" : "", Math.abs(value) / 100, Math.abs(value) % 100));
    }

    private void updateGameState() {
        if (chessboard.isDraw()) {
            timerThread.running = false;
            JOptionPane.showMessageDialog(this, Localisation.getString("chessboard.message.draw"), Localisation.getString("chessboard.message.draw"), JOptionPane.PLAIN_MESSAGE);
        } else if (chessboard.isWin(PlayerColor.WHITE)) {
            timerThread.running = false;
            JOptionPane.showMessageDialog(this, Localisation.getString("chessboard.message.white_won"), Localisation.getString("chessboard.message.white_won"), JOptionPane.PLAIN_MESSAGE);
        } else if (chessboard.isWin(PlayerColor.BLACK)) {
            timerThread.running = false;
            JOptionPane.showMessageDialog(this, Localisation.getString("chessboard.message.black_won"), Localisation.getString("chessboard.message.black_won"), JOptionPane.PLAIN_MESSAGE);
        } else {
            time = 0;
            updateTimer();
        }
        JLabel label = new JLabel(chessboard.lastMove.getText());
        label.setIcon(Icons.getTinyIcons(chessboard.lastMove.chessPiece.playerColor, chessboard.lastMove.chessPiece.chessPieceType));
        label.setFont(FONT_LABEL_HISTORY);
        historyPanel.add(label);
    }

    private void updateInverted() {
        if (automaticRadioButtonMenuItem.isSelected()) {
            reversed = chessboard.playerColor == PlayerColor.BLACK;
        }
        for (int i = 0; i < 8; i++)
            leftLabels[reversed ? 7 - i : i].setText(Integer.toString(8 - i));
    }

    private void updateTimer() {
        timerLabel.setText(String.format("%02d:%02d", time / 60, time % 60));
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        timerThread.running = false;
        for (Player p : players)
            p.onClosed();
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

    private final class TimerThread extends Thread {

        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> {
                    time++;
                    updateTimer();
                });
            }
        }
    }
}
