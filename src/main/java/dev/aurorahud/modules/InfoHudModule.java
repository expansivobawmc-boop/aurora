package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public class InfoHudModule extends Module {
    public InfoHudModule() {
        super("Info HUD", "FPS, ping, coordenadas e direcao.", Category.HUD, true);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        List<String> lines = new ArrayList<>();
        lines.add("Aurora");
        lines.add("FPS: " + mc.getCurrentFps());
        lines.add("Ping: " + ping(mc) + " ms");
        lines.add(String.format("XYZ: %d %d %d",
                (int) Math.floor(mc.player.getX()),
                (int) Math.floor(mc.player.getY()),
                (int) Math.floor(mc.player.getZ())));
        lines.add("Dir: " + mc.player.getHorizontalFacing().asString());
        HudStyle.panel(ctx, mc.textRenderer, 8, 8, lines);
    }

    private int ping(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e == null ? 0 : e.getLatency();
    }
}
