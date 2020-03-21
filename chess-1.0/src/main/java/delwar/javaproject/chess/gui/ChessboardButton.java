package delwar.javaproject.chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class ChessboardButton extends JButton implements MouseListener {

    private static final Color COLOR_1 = new Color(255, 206, 158);
    private static final Color COLOR_1_B = new Color(255, 222, 174);
    private static final Color COLOR_2 = new Color(209, 139, 71);
    private static final Color COLOR_2_B = new Color(225, 155, 87);
    private static final Color COLOR_SELECTED = new Color(209, 82, 60);
    private static final Color COLOR_SELECTED_B = new Color(225, 98, 76);
    private static final Color COLOR_SHOW = new Color(98, 183, 62);
    private static final Color COLOR_SHOW_B = new Color(10, 199, 16);

    private boolean hover;
    private boolean background;
    private ImageIcon icon;
    private int iconSet = 0;

    ChessboardButton() {
        super();
        addMouseListener(this);
        setPreferredSize(new Dimension(50, 50));
        setBorder(null);
    }

    @Override
    public void paint(Graphics g) {
        switch (iconSet) {
            case 0:
                if (background)
                    g.setColor(hover && isEnabled() ? COLOR_1_B : COLOR_1);
                else
                    g.setColor(hover && isEnabled() ? COLOR_2_B : COLOR_2);
                break;
            case 1:
                g.setColor(hover && isEnabled() ? COLOR_SELECTED_B : COLOR_SELECTED); break;
            case 2:
                g.setColor(hover && isEnabled() ? COLOR_SHOW_B : COLOR_SHOW); break;
        }

        g.fillRect(0, 0, getWidth(), getHeight());

        if (icon != null) {
            int width = Math.min(getWidth(), 50);
            int height = Math.min(getHeight(), 50);
            g.drawImage(icon.getImage(), Math.abs(getWidth() - width) / 2, Math.abs(getHeight() - height) / 2, width, height, icon.getImageObserver());
        }
    }

    void iconReset(boolean background) {
        iconSet = 0;
        this.background = background;
    }

    void iconSelected() {
        iconSet = 1;
    }

    void iconShow() {
        iconSet = 2;
    }

    void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
    }
}
