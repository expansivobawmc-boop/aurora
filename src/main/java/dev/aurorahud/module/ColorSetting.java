package dev.aurorahud.module;

public class ColorSetting extends Setting {
    public final NumberSetting r, g, b;

    public ColorSetting(String name, int red, int green, int blue) {
        super(name);
        r = new NumberSetting("Red", 0, 255, 1, red);
        g = new NumberSetting("Green", 0, 255, 1, green);
        b = new NumberSetting("Blue", 0, 255, 1, blue);
    }

    public int rgb() { return (r.asInt() << 16) | (g.asInt() << 8) | b.asInt(); }
    public int argb(int alpha) { return (Math.max(0, Math.min(255, alpha)) << 24) | rgb(); }
    public int argb() { return argb(255); }

    @Override
    public int key() { return rgb(); }
}
