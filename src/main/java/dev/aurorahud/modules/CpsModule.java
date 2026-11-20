package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.ClickTracker;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class CpsModule extends Module {
    private final ColorSetting accent = color("Accent color", 139, 124, 255);

    public CpsModule() {
        super("CPS", "Shows your clicks per second.", Category.HUD, false);
        hud = true;
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        int x = 8 + offX, y = 70 + offY;
        int[] sz = HudStyle.panel(ctx, mc.textRenderer, x, y,
                List.of("CPS: " + ClickTracker.left() + " | " + ClickTracker.right()), accent.argb());
        bounds(x, y, sz[0], sz[1]);
    }
}
