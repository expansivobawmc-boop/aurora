package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.BooleanSetting;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public class InfoHudModule extends Module {
    private final BooleanSetting showFps = bool("Show FPS", true);
    private final BooleanSetting showPing = bool("Show ping", true);
    private final BooleanSetting showCoords = bool("Show coordinates", true);
    private final BooleanSetting showDir = bool("Show direction", true);
    private final ColorSetting accent = color("Accent color", 139, 124, 255);

    public InfoHudModule() {
        super("Info HUD", "FPS, ping, coordinates and direction.", Category.HUD, true);
        hud = true;
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        List<String> lines = new ArrayList<>();
        lines.add("Aurora");
        if (showFps.value) lines.add("FPS: " + mc.getCurrentFps());
        if (showPing.value) lines.add("Ping: " + ping(mc) + " ms");
        if (showCoords.value) {
            lines.add(String.format("XYZ: %d %d %d",
                    (int) Math.floor(mc.player.getX()),
                    (int) Math.floor(mc.player.getY()),
                    (int) Math.floor(mc.player.getZ())));
        }
        if (showDir.value) lines.add("Dir: " + mc.player.getHorizontalFacing().asString());
        int x = 8 + offX, y = 8 + offY;
        int[] sz = HudStyle.panel(ctx, mc.textRenderer, x, y, lines, accent.argb());
        bounds(x, y, sz[0], sz[1]);
    }

    private int ping(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e == null ? 0 : e.getLatency();
    }
}
