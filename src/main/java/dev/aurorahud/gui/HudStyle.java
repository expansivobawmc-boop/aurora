package dev.aurorahud.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public final class HudStyle {
    public static final int ACCENT = 0xFF8B7CFF;
    public static final int PANEL = 0x90000000;
    public static final int TEXT = 0xFFFFFFFF;

    private HudStyle() {}

    public static void panel(DrawContext ctx, TextRenderer tr, int x, int y, List<String> lines) {
        int pad = 4, lineH = 11, maxW = 0;
        for (String s : lines) maxW = Math.max(maxW, tr.getWidth(s));
        int w = maxW + pad * 2 + 2;
        int h = lines.size() * lineH + pad * 2 - 2;
        ctx.fill(x, y, x + w, y + h, PANEL);
        ctx.fill(x, y, x + 2, y + h, ACCENT);
        int ty = y + pad;
        for (String s : lines) {
            ctx.drawTextWithShadow(tr, s, x + pad + 2, ty, TEXT);
            ty += lineH;
        }
    }
}
