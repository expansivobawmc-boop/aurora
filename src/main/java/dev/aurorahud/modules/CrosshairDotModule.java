package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CrosshairDotModule extends Module {
    public CrosshairDotModule() {
        super("Crosshair Dot", "Ponto colorido no centro da mira.", Category.PVP, false);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, HudStyle.ACCENT);
    }
}
