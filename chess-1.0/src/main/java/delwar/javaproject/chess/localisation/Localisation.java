package delwar.javaproject.chess.localisation;

import java.util.ResourceBundle;

public final class Localisation {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(Localisation.class.getName().toLowerCase());

    public static String getString(String key) {
        return resourceBundle.getString(key);
    }
}
