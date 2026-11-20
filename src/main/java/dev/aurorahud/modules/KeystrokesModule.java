package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

public class KeystrokesModule extends Module {
    public KeystrokesModule() {
        super("Keystrokes", "Mostra W A S D na tela.", Category.PVP, true);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int size = 20, gap = 2;
        int baseX = 8 + size + gap;
        int baseY = ctx.getScaledWindowHeight() - (size * 2 + gap) - 8;
        key(ctx, tr, mc.options.forwardKey, "W", baseX, baseY, size);
        key(ctx, tr, mc.options.leftKey, "A", baseX - size - gap, baseY + size + gap, size);
        key(ctx, tr, mc.options.backKey, "S", baseX, baseY + size + gap, size);
        key(ctx, tr, mc.options.rightKey, "D", baseX + size + gap, baseY + size + gap, size);
    }

    private void key(DrawContext ctx, TextRenderer tr, KeyBinding kb, String label, int x, int y, int size) {
        int bg = kb.isPressed() ? 0xB08B7CFF : HudStyle.PANEL;
        ctx.fill(x, y, x + size, y + size, bg);
        ctx.drawTextWithShadow(tr, label, x + (size - tr.getWidth(label)) / 2,
                y + (size - tr.fontHeight) / 2 + 1, HudStyle.TEXT);
    }
}
