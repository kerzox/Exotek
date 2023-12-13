package mod.kerzox.exotek.common.util;

import java.awt.*;

public enum ColourHex {
    BLACK("0x000000"),
    WHITE("0xffffff"),
    ENERGY_GREEN("0x56ffaa"),
    INPUT_BLUE("0x5c7aff"),
    OUTPUT_RED("0xa4161a");

    String colour;

    ColourHex(String colour) {
        this.colour = colour;
    }

    public int getColour() {
        return Color.decode(colour).getRGB();
    }

    public int changeOpacity(int percentage) {
        String alpha = Integer.toHexString(percentage * 255 / 100);
        String decode = this.colour.replace("0x", "");
        int r = Integer.valueOf(decode.substring(0, 2), 16);
        int g = Integer.valueOf(decode.substring(2, 4), 16);
        int b = Integer.valueOf(decode.substring(4, 6), 16);
        int a = Integer.parseInt(alpha, 16);
        return new Color(r, g, b, a).getRGB();
    }

    public static int custom(String colour, int opacity) {
        String alpha = Integer.toHexString(opacity * 255 / 100);
        String decode = colour.replace("0x", "");
        int r = Integer.valueOf(decode.substring(0, 2), 16);
        int g = Integer.valueOf(decode.substring(2, 4), 16);
        int b = Integer.valueOf(decode.substring(4, 6), 16);
        int a = Integer.parseInt(alpha, 16);
        return new Color(r, g, b, a).getRGB();
    }
}
