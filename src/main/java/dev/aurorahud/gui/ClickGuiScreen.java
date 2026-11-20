package dev.aurorahud.gui;

import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int W = 440, H = 260;
    private static Category selected = Category.PVP;
    private boolean lastDown = true; // evita clique fantasma ao abrir

    public ClickGuiScreen() {
        super(Text.literal("Aurora"));
    }

    @Override
    public boolean shouldPause() { return false; }

    private int px() { return (width - W) / 2; }
    private int py() { return (height - H) / 2; }
    private int tabX(int i) { return px() + 110 + i * 62; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        handleClick(mouseX, mouseY);
        ctx.fill(0, 0, width, height, 0x80000000);
        ctx.fill(px(), py(), px() + W, py() + H, 0xF0101018);
        ctx.fill(px(), py(), px() + W, py() + 2, HudStyle.ACCENT);
        ctx.drawTextWithShadow(textRenderer, "Aurora", px() + 16, py() + 16, HudStyle.ACCENT);

        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            boolean sel = cats[i] == selected;
            int x = tabX(i), y = py() + 10;
            ctx.fill(x, y, x + 58, y + 20, sel ? 0x558B7CFF : 0x30FFFFFF);
            ctx.drawTextWithShadow(textRenderer, cats[i].label,
                    x + (58 - textRenderer.getWidth(cats[i].label)) / 2, y + 6, HudStyle.TEXT);
        }

        List<Module> mods = ModuleManager.byCategory(selected);
        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int x = px() + 16, y = py() + 46 + i * 30;
            ctx.fill(x, y, x + W - 32, y + 26, 0x30FFFFFF);
            ctx.drawTextWithShadow(textRenderer, m.name, x + 8, y + 4, HudStyle.TEXT);
            ctx.drawTextWithShadow(textRenderer, m.description, x + 8, y + 15, 0xFF9A9AB0);
            int tx = x + W - 32 - 40, ty = y + 6;
            ctx.fill(tx, ty, tx + 30, ty + 14, m.isEnabled() ? HudStyle.ACCENT : 0xFF3A3A48);
            int knob = m.isEnabled() ? tx + 17 : tx + 3;
            ctx.fill(knob, ty + 3, knob + 10, ty + 11, 0xFFFFFFFF);
        }
        if (mods.isEmpty()) {
            ctx.drawTextWithShadow(textRenderer, "Nenhum modulo nesta aba ainda.",
                    px() + 16, py() + 52, 0xFF9A9AB0);
        }
    }

    private void handleClick(int mx, int my) {
        long win = client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean clicked = down && !lastDown;
        lastDown = down;
        if (!clicked) return;

        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            int x = tabX(i), y = py() + 10;
            if (mx >= x && mx <= x + 58 && my >= y && my <= y + 20) {
                selected = cats[i];
                return;
            }
        }
        List<Module> mods = ModuleManager.byCategory(selected);
        for (int i = 0; i < mods.size(); i++) {
            int x = px() + 16, y = py() + 46 + i * 30;
            if (mx >= x && mx <= x + W - 32 && my >= y && my <= y + 26) {
                mods.get(i).toggle();
                return;
            }
        }
    }
}
