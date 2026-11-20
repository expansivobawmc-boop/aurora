package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.BooleanSetting;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Efeito de aurora desenhado sobre a tela quando voce olha para cima.
 * E uma sobreposicao, nao uma troca do ceu real.
 */
public class AuroraSkyModule extends Module {
    private final NumberSetting intensity = num("Intensity", 10, 100, 5, 70);
    private final NumberSetting speed = num("Speed", 1, 10, 1, 4);
    private final ColorSetting colorA = color("Color A", 139, 92, 246);
    private final ColorSetting colorB = color("Color B", 52, 211, 153);
    private final BooleanSetting nightOnly = bool("Night only", true);

    public AuroraSkyModule() {
        super("Aurora Sky", "Purple and green aurora when you look up.", Category.VISUALS, true);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;

        float look = Math.max(0f, Math.min(1f, (-mc.player.getPitch() - 5f) / 45f));
        if (look <= 0f) return;

        float night = 1f;
        if (nightOnly.value) {
            long t = mc.world.getTimeOfDay() % 24000L;
            if (t < 12000) night = 0f;
            else if (t < 13500) night = (t - 12000) / 1500f;
            else if (t < 22500) night = 1f;
            else night = (24000 - t) / 1500f;
        }
        float strength = look * night * (intensity.asInt() / 100f);
        if (strength < 0.02f) return;

        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int n = 28;
        double time = System.currentTimeMillis() / 1000.0 * (speed.asInt() / 4.0);
        int maxAlpha = (int) (120 * strength);

        for (int i = 0; i < n; i++) {
            int x1 = i * w / n;
            int x2 = (i + 1) * w / n;
            double wave = 0.5 + 0.5 * Math.sin(time * 0.8 + i * 0.45);
            double drift = Math.sin(time * 0.5 + i * 0.2);
            int top = (int) (h * (0.05 + 0.04 * drift));
            int band = (int) (h * (0.22 + 0.14 * wave) * (0.5 + 0.5 * look));
            float mix = (float) (0.5 + 0.5 * Math.sin(time * 0.3 + i * 0.3));
            int rgb = HudStyle.lerpColor(colorA.argb(), colorB.argb(), mix) & 0x00FFFFFF;
            int clear = rgb;                          // alpha 0
            int solid = (maxAlpha << 24) | rgb;
            HudStyle.vGradient(ctx, x1, top, x2, top + band / 2, clear, solid);
            HudStyle.vGradient(ctx, x1, top + band / 2, x2, top + band, solid, clear);
        }
    }
}
