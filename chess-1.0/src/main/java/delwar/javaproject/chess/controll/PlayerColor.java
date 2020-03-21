package delwar.javaproject.chess.controll;

// the two player colors
public enum PlayerColor {
    WHITE(1),
    BLACK(-1);

    public final int sign;

    PlayerColor(int sign) {
        this.sign = sign;
    }

    public PlayerColor otherColor() {
        return values()[ordinal() ^ 1];
    }
}
