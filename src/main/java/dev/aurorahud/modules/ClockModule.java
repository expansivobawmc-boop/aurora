package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.BooleanSetting;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockModule extends Module {
    private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private final BooleanSetting twentyFour = bool("24-hour format", true);
    private final ColorSetting accent = color("Accent color", 139, 124, 255);

    public ClockModule() {
        super("Clock", "Shows your computer's time.", Category.HUD, false);
        hud = true;
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        int x = 8 + offX, y = 92 + offY;
        String t = LocalTime.now().format(twentyFour.value ? H24 : H12);
        int[] sz = HudStyle.panel(ctx, mc.textRenderer, x, y, List.of(t), accent.argb());
        bounds(x, y, sz[0], sz[1]);
    }
}
