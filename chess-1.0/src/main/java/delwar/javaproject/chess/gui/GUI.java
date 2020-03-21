package delwar.javaproject.chess.gui;

import javax.swing.*;

abstract class GUI extends JFrame {

    final JPanel panel;

    GUI() {
        // create panel
        panel = new JPanel();

        // initialise GUI
        initGUI();

        setLocation(200,0);

        // add panel
        setContentPane(panel);
        pack();

        // initialise Listeners
        initListeners();
    }

    abstract void initGUI();

    abstract void initListeners();
}
