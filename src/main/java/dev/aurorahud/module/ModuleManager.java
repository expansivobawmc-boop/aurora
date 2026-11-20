package dev.aurorahud.module;

import dev.aurorahud.modules.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.add(new CrosshairDotModule());
        MODULES.add(new CpsModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new InfoHudModule());
        MODULES.add(new ToggleSprintModule());
        MODULES.add(new ClockModule());
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
        for (Module m : MODULES) if (m.isEnabled()) m.onHud(ctx, mc);
    }
}
