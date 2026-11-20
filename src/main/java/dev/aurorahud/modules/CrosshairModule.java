package dev.aurorahud.modules;

import dev.aurorahud.module.BooleanSetting;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.ModeSetting;
import dev.aurorahud.module.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {
    private final ModeSetting style = mode("Style", 0, "Cross", "Dot", "Cross + Dot", "Square");
    private final ColorSetting color = color("Color", 139, 124, 255);
    private final NumberSetting opacity = num("Opacity", 20, 100, 5, 100);
    private final NumberSetting thickness = num("Thickness", 1, 6, 1, 2);
    private final NumberSetting length = num("Length", 1, 20, 1, 6);
    private final NumberSetting gap = num("Gap", 0, 12, 1, 3);
    private final BooleanSetting outline = bool("Outline", true);
    private final BooleanSetting hideVanilla = bool("Hide vanilla crosshair", true);

    public CrosshairModule() {
        super("Crosshair", "Fully customizable crosshair.", Category.PVP, false);
    }

    public boolean hidesVanilla() { return hideVanilla.value; }

    /** Chamado pelo elemento de HUD que substitui a mira original. */
    public void draw(DrawContext ctx) {
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        int t = thickness.asInt(), len = length.asInt(), g = gap.asInt();
        int col = color.argb(Math.round(opacity.asInt() * 2.55f));
        int h = t / 2;
        String s = style.get();

        boolean cross = s.equals("Cross") || s.equals("Cross + Dot");
        boolean dot = s.equals("Dot") || s.equals("Cross + Dot");
        boolean square = s.equals("Square");

        int[][] rects = new int[8][];
        int n = 0;
        if (cross) {
            rects[n++] = new int[]{cx - g - len, cy - h, cx - g, cy - h + t};
            rects[n++] = new int[]{cx + g, cy - h, cx + g + len, cy - h + t};
            rects[n++] = new int[]{cx - h, cy - g - len, cx - h + t, cy - g};
            rects[n++] = new int[]{cx - h, cy + g, cx - h + t, cy + g + len};
        }
        if (dot) {
            int d = Math.max(t, 2);
            rects[n++] = new int[]{cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d};
        }
        if (square) {
            int r = g + Math.max(len / 2, 2);
            rects[n++] = new int[]{cx - r, cy - r, cx + r, cy - r + t};
            rects[n++] = new int[]{cx - r, cy + r - t, cx + r, cy + r};
            rects[n++] = new int[]{cx - r, cy - r, cx - r + t, cy + r};
            rects[n++] = new int[]{cx + r - t, cy - r, cx + r, cy + r};
        }

        if (outline.value) {
            for (int i = 0; i < n; i++) {
                int[] r = rects[i];
                ctx.fill(r[0] - 1, r[1] - 1, r[2] + 1, r[3] + 1, 0xFF000000);
            }
        }
        for (int i = 0; i < n; i++) {
            int[] r = rects[i];
            ctx.fill(r[0], r[1], r[2], r[3], col);
        }
    }
}
