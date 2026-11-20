package dev.aurorahud.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();
    private boolean enabled;

    // Elementos de HUD arrastaveis
    public boolean hud;
    public int offX, offY;
    public int bx, by, bw, bh;

    protected Module(String name, String description, Category category, boolean enabledByDefault) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabledByDefault;
    }

    public <T extends Setting> T add(T s) { settings.add(s); return s; }

    protected BooleanSetting bool(String n, boolean def) { return add(new BooleanSetting(n, def)); }
    protected NumberSetting num(String n, double min, double max, double step, double def) {
        return add(new NumberSetting(n, min, max, step, def));
    }
    protected ModeSetting mode(String n, int def, String... modes) { return add(new ModeSetting(n, def, modes)); }
    protected ColorSetting color(String n, int r, int g, int b) { return add(new ColorSetting(n, r, g, b)); }

    protected void bounds(int x, int y, int w, int h) { bx = x; by = y; bw = w; bh = h; }

    public int settingsKey() {
        int k = 17;
        for (Setting s : settings) k = k * 31 + s.key();
        return k;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onHud(DrawContext ctx, MinecraftClient mc) {}
}
