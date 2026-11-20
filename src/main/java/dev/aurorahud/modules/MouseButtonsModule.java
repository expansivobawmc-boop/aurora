package dev.aurorahud.modules;

import dev.aurorahud.gui.Fonts;
import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ClickTracker;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class MouseButtonsModule extends Module {
    private final ColorSetting pressed = color("Pressed color", 139, 124, 255);

    public MouseButtonsModule() {
        super("Mouse Buttons", "Shows LMB and RMB with CPS.", Category.HUD, true);
        hud = true;
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int w = 31, h = 28, gap = 2;
        int x = 8 + offX;
        int y = ctx.getScaledWindowHeight() - 52 - gap - h + offY;
        box(ctx, tr, "LMB", ClickTracker.left(), ClickTracker.leftDown(), x, y, w, h);
        box(ctx, tr, "RMB", ClickTracker.right(), ClickTracker.rightDown(), x + w + gap, y, w, h);
        bounds(x, y, w * 2 + gap, h);
    }

    private void box(DrawContext ctx, TextRenderer tr, String label, int cps, boolean down,
                     int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, down ? pressed.argb(176) : HudStyle.PANEL);
        Fonts.draw(ctx, tr, label, x + (w - Fonts.width(tr, label)) / 2, y + 5, HudStyle.TEXT);
        String c = String.valueOf(cps);
        Fonts.draw(ctx, tr, c, x + (w - Fonts.width(tr, c)) / 2, y + 16, 0xFFB8B8C8);
    }
}
