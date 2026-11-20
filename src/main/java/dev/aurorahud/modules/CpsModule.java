package dev.aurorahud.modules;

import dev.aurorahud.gui.HudStyle;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class CpsModule extends Module {
    private final Deque<Long> left = new ArrayDeque<>();
    private final Deque<Long> right = new ArrayDeque<>();
    private boolean lastL, lastR;

    public CpsModule() {
        super("CPS", "Mostra cliques por segundo.", Category.PVP, true);
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        long win = mc.getWindow().getHandle();
        long now = System.currentTimeMillis();
        boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (l && !lastL) left.add(now);
        if (r && !lastR) right.add(now);
        lastL = l;
        lastR = r;
        while (!left.isEmpty() && now - left.peekFirst() > 1000) left.pollFirst();
        while (!right.isEmpty() && now - right.peekFirst() > 1000) right.pollFirst();

        HudStyle.panel(ctx, mc.textRenderer, 8, 70, List.of("CPS: " + left.size() + " | " + right.size()));
    }
}
