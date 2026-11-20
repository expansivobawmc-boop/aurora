package dev.aurorahud.modules;

import dev.aurorahud.gui.BackgroundImage;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.ModeSetting;
import dev.aurorahud.module.NumberSetting;

/** Opcoes de aparencia do proprio menu. */
public class MenuThemeModule extends Module {
    public static final int DEFAULT_ACCENT = 0xFF8B7CFF;

    private final ModeSetting background = mode("Background", 0, "Purple", "Aurora");
    private final NumberSetting dimming = num("Background dimming", 0, 80, 5, 35);
    private final ColorSetting accent = color("Accent color", 139, 124, 255);

    public MenuThemeModule() {
        super("Customize Menu", "Change the menu background and colors.", Category.MISC, true);
    }

    public boolean aurora() {
        return isEnabled() && background.index == 1 && BackgroundImage.available();
    }

    public int dimPercent() { return dimming.asInt(); }

    public int accentArgb() { return isEnabled() ? accent.argb() : DEFAULT_ACCENT; }
}
