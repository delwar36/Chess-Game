package delwar.javaproject.chess.icon;

import delwar.javaproject.chess.controll.ChessPieceType;
import delwar.javaproject.chess.controll.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Icons {

    private static final ImageIcon[][] icons;
    private static final ImageIcon[][] tinyIcons;

    static {
        icons = new ImageIcon[PlayerColor.values().length][ChessPieceType.values().length];
        tinyIcons = new ImageIcon[PlayerColor.values().length][ChessPieceType.values().length];
        for (PlayerColor playerColor : PlayerColor.values()) {
            for (ChessPieceType chessPieceType : ChessPieceType.values()) {
                icons[playerColor.ordinal()][chessPieceType.ordinal()] = new ImageIcon(Icons.class.getResource(playerColor.name().toLowerCase() + "_" + chessPieceType.name().toLowerCase() + ".png"));
                tinyIcons[playerColor.ordinal()][chessPieceType.ordinal()] = resize(icons[playerColor.ordinal()][chessPieceType.ordinal()], 25, 25);
            }
        }
    }

    public static ImageIcon getIcon(PlayerColor playerColor, ChessPieceType chessPieceType) {
        return icons[playerColor.ordinal()][chessPieceType.ordinal()];
    }

    public static ImageIcon getTinyIcons(PlayerColor playerColor, ChessPieceType chessPieceType) {
        return tinyIcons[playerColor.ordinal()][chessPieceType.ordinal()];
    }

    private static ImageIcon resize(ImageIcon image, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image.getImage(), 0, 0, width, height, null);
        g2d.dispose();
        return new ImageIcon(bufferedImage);
    }
}
