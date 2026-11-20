package dev.aurorahud.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public final class HudStyle {
    public static int ACCENT = 0xFF8B7CFF;
    public static final int PANEL = 0x90000000;
    public static final int TEXT = 0xFFFFFFFF;

    private HudStyle() {}

    public static int lerpColor(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aa = (int) (((a >>> 24) & 255) * (1 - t) + ((b >>> 24) & 255) * t);
        int ar = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int ag = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int ab = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return (aa << 24) | (ar << 16) | (ag << 8) | ab;
    }

    /** Degrade vertical feito com faixas, sem depender de APIs especificas. */
    public static void vGradient(DrawContext ctx, int x1, int y1, int x2, int y2, int top, int bottom) {
        int h = y2 - y1;
        if (h <= 0) return;
        int steps = Math.min(h, 14);
        for (int s = 0; s < steps; s++) {
            int ya = y1 + h * s / steps;
            int yb = y1 + h * (s + 1) / steps;
            ctx.fill(x1, ya, x2, yb, lerpColor(top, bottom, (s + 0.5f) / steps));
        }
    }

    /** Retangulo com cantos levemente arredondados. */
    public static void roundRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x + 2, y, x + w - 2, y + h, color);
        ctx.fill(x, y + 2, x + w, y + h - 2, color);
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, color);
    }

    public static void border(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    /** Icone "A" original, desenhado por codigo. */
    public static void logo(DrawContext ctx, int x, int y) {
        for (int r = 0; r < 16; r++) {
            int half = r / 2;
            int lx = x + 7 - half;
            int rx = x + 9 + half;
            int c = lerpColor(0xFFC4A8FF, 0xFF7C5CFF, r / 15f);
            ctx.fill(lx, y + r, lx + 2, y + r + 1, c);
            ctx.fill(rx - 2, y + r, rx, y + r + 1, c);
        }
        ctx.fill(x + 4, y + 11, x + 12, y + 13, 0xFFC4A8FF);
    }

    public static int[] panel(DrawContext ctx, TextRenderer tr, int x, int y, List<String> lines, int accent) {
        int pad = 4, lineH = 11, maxW = 0;
        for (String s : lines) maxW = Math.max(maxW, Fonts.width(tr, s));
        int w = maxW + pad * 2 + 2;
        int h = lines.size() * lineH + pad * 2 - 2;
        ctx.fill(x, y, x + w, y + h, PANEL);
        ctx.fill(x, y, x + 2, y + h, accent);
        int ty = y + pad;
        for (String s : lines) {
            Fonts.draw(ctx, tr, s, x + pad + 2, ty, TEXT);
            ty += lineH;
        }
        return new int[]{w, h};
    }
}
