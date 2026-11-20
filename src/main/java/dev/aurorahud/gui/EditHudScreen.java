package dev.aurorahud.gui;

import dev.aurorahud.module.Module;
import dev.aurorahud.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EditHudScreen extends Screen {
    private Module dragging;
    private int lastMx, lastMy;
    private boolean lastDown = true;

    public EditHudScreen() {
        super(Text.literal("Edit HUD"));
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() {
        client.setScreen(new ClickGuiScreen());
    }

    private List<Module> editable() {
        List<Module> out = new ArrayList<>();
        for (Module m : ModuleManager.all()) if (m.hud && m.isEnabled() && m.bw > 0) out.add(m);
        return out;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        long win = client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean pressed = down && !lastDown;
        lastDown = down;

        List<Module> mods = editable();
        int rx = width / 2 - 55, ry = height - 40;

        if (pressed) {
            if (mx >= rx && mx < rx + 110 && my >= ry && my < ry + 20) {
                for (Module m : ModuleManager.all()) { m.offX = 0; m.offY = 0; }
            } else {
                for (int i = mods.size() - 1; i >= 0; i--) {
                    Module m = mods.get(i);
                    if (mx >= m.bx - 2 && mx < m.bx + m.bw + 2 && my >= m.by - 2 && my < m.by + m.bh + 2) {
                        dragging = m;
                        break;
                    }
                }
            }
        }
        if (!down) dragging = null;
        if (dragging != null) {
            dragging.offX += mx - lastMx;
            dragging.offY += my - lastMy;
        }
        lastMx = mx;
        lastMy = my;

        ctx.fill(0, 0, width, height, 0x66000000);

        for (Module m : mods) {
            int c = m == dragging ? 0xFFFFFFFF : HudStyle.ACCENT;
            HudStyle.border(ctx, m.bx - 2, m.by - 2, m.bw + 4, m.bh + 4, c);
            Fonts.draw(ctx, textRenderer, m.name, m.bx - 2, m.by - 13, c);
        }

        String title = "Edit HUD";
        String hint = "Drag the elements to move them. Press ESC to go back.";
        Fonts.drawBold(ctx, textRenderer, title, (width - Fonts.width(textRenderer, title)) / 2, 12, HudStyle.ACCENT);
        Fonts.draw(ctx, textRenderer, hint, (width - Fonts.width(textRenderer, hint)) / 2, 26, 0xFFC8C8D8);

        boolean hov = mx >= rx && mx < rx + 110 && my >= ry && my < ry + 20;
        HudStyle.roundRect(ctx, rx, ry, 110, 20, hov ? 0x66FFFFFF : 0x40FFFFFF);
        String rt = "Reset positions";
        Fonts.draw(ctx, textRenderer, rt, rx + (110 - Fonts.width(textRenderer, rt)) / 2, ry + 6, HudStyle.TEXT);
    }
}
