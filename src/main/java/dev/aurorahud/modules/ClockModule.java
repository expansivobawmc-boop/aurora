package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockModule extends Module {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClockModule() {
        super("Clock", "Mostra a hora do seu computador.", Category.MISC, false);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        HudStyle.panel(ctx, mc.textRenderer, 8, 92, List.of(LocalTime.now().format(FMT)));
    }
}
