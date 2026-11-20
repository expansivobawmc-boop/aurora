package dev.aurorahud.modules;

import dev.aurorahud.gui.Fonts;
import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

public class KeystrokesModule extends Module {
    private final ColorSetting pressed = color("Pressed color", 139, 124, 255);

    public KeystrokesModule() {
        super("Keystrokes", "Shows W A S D on screen.", Category.HUD, true);
        hud = true;
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int size = 20, gap = 2;
        int x0 = 8 + offX;
        int y0 = ctx.getScaledWindowHeight() - (size * 2 + gap) - 8 + offY;
        key(ctx, tr, mc.options.forwardKey, "W", x0 + size + gap, y0, size);
        key(ctx, tr, mc.options.leftKey, "A", x0, y0 + size + gap, size);
        key(ctx, tr, mc.options.backKey, "S", x0 + size + gap, y0 + size + gap, size);
        key(ctx, tr, mc.options.rightKey, "D", x0 + (size + gap) * 2, y0 + size + gap, size);
        bounds(x0, y0, size * 3 + gap * 2, size * 2 + gap);
    }

    private void key(DrawContext ctx, TextRenderer tr, KeyBinding kb, String label, int x, int y, int size) {
        int bg = kb.isPressed() ? pressed.argb(176) : HudStyle.PANEL;
        ctx.fill(x, y, x + size, y + size, bg);
        Fonts.draw(ctx, tr, label, x + (size - Fonts.width(tr, label)) / 2,
                y + (size - tr.fontHeight) / 2 + 1, HudStyle.TEXT);
    }
}
