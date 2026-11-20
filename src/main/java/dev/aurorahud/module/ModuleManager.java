package dev.aurorahud.module;

import dev.aurorahud.modules.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.particle.ParticlesMode;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    public static CrosshairModule crosshair;
    public static MenuThemeModule menu;

    private ModuleManager() {}

    public static void init() {
        // Visuals (desenhado primeiro, fica por baixo do resto)
        MODULES.add(new AuroraSkyModule());

        // PvP
        crosshair = new CrosshairModule();
        MODULES.add(crosshair);

        // HUD
        MODULES.add(new InfoHudModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new MouseButtonsModule());
        MODULES.add(new CpsModule());
        MODULES.add(new ClockModule());

        // Player
        MODULES.add(new ToggleSprintModule());

        // Optimizers (ajustes de FPS nas opcoes do jogo)
        NumberSetting rd = new NumberSetting("Max chunks", 4, 16, 1, 8);
        MODULES.add(new OptimizerModule("Render Distance", "Caps the render distance.",
                o -> { int old = o.getViewDistance().getValue(); return () -> o.getViewDistance().setValue(old); },
                o -> { int v = rd.asInt(); if (o.getViewDistance().getValue() > v) o.getViewDistance().setValue(v); },
                rd));

        NumberSetting sd = new NumberSetting("Max chunks", 5, 12, 1, 6);
        MODULES.add(new OptimizerModule("Simulation Distance", "Caps how far entities and blocks tick.",
                o -> { int old = o.getSimulationDistance().getValue(); return () -> o.getSimulationDistance().setValue(old); },
                o -> { int v = sd.asInt(); if (o.getSimulationDistance().getValue() > v) o.getSimulationDistance().setValue(v); },
                sd));

        NumberSetting ed = new NumberSetting("Percent", 50, 100, 5, 75);
        MODULES.add(new OptimizerModule("Entity Distance", "Renders entities only up to a shorter distance.",
                o -> { double old = o.getEntityDistanceScaling().getValue(); return () -> o.getEntityDistanceScaling().setValue(old); },
                o -> { double v = ed.value / 100.0; if (o.getEntityDistanceScaling().getValue() > v) o.getEntityDistanceScaling().setValue(v); },
                ed));

        NumberSetting bb = new NumberSetting("Radius", 0, 7, 1, 1);
        MODULES.add(new OptimizerModule("Biome Blend", "Lowers the biome color blending radius.",
                o -> { int old = o.getBiomeBlendRadius().getValue(); return () -> o.getBiomeBlendRadius().setValue(old); },
                o -> { int v = bb.asInt(); if (o.getBiomeBlendRadius().getValue() > v) o.getBiomeBlendRadius().setValue(v); },
                bb));

        MODULES.add(new OptimizerModule("Fast Graphics", "Switches to the Fast graphics mode.",
        o -> {
            GraphicsMode old = o.getPreset().getValue();
            return () -> o.applyGraphicsMode(old);
        },
        o -> o.applyGraphicsMode(GraphicsMode.FAST)));

        MODULES.add(new OptimizerModule("Minimal Particles", "Reduces particles to the minimum.",
                o -> { ParticlesMode old = o.getParticles().getValue(); return () -> o.getParticles().setValue(old); },
                o -> o.getParticles().setValue(ParticlesMode.MINIMAL)));

        MODULES.add(new OptimizerModule("Clouds Off", "Turns clouds off.",
                o -> { CloudRenderMode old = o.getCloudRenderMode().getValue(); return () -> o.getCloudRenderMode().setValue(old); },
                o -> o.getCloudRenderMode().setValue(CloudRenderMode.OFF)));

        MODULES.add(new OptimizerModule("Smooth Lighting Off", "Turns smooth lighting off.",
                o -> { boolean old = o.getAo().getValue(); return () -> o.getAo().setValue(old); },
                o -> o.getAo().setValue(false)));

        MODULES.add(new OptimizerModule("Unlimited FPS", "Removes the FPS cap and turns VSync off.",
                o -> {
                    int fps = o.getMaxFps().getValue();
                    boolean vs = o.getEnableVsync().getValue();
                    return () -> { o.getMaxFps().setValue(fps); o.getEnableVsync().setValue(vs); };
                },
                o -> { o.getMaxFps().setValue(260); o.getEnableVsync().setValue(false); }));

        // Misc
        menu = new MenuThemeModule();
        MODULES.add(menu);
    }

    public static List<Module> all() { return MODULES; }

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category == c) out.add(m);
        return out;
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick(mc);
    }

    public static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        ClickTracker.update(mc.getWindow().getHandle());
        for (Module m : MODULES) if (m.isEnabled()) m.onHud(ctx, mc);
    }
}
