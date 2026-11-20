package dev.aurorahud;

import dev.aurorahud.gui.ClickGuiScreen;
import dev.aurorahud.modules.CrosshairModule;
import dev.aurorahud.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AuroraHudClient implements ClientModInitializer {
    public static final String NAME = "Aurora";
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aurorahud.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("aurorahud", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openGuiKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ClickGuiScreen());
            }
            if (mc.player != null) ModuleManager.tick(mc);
        });

        // Modulos de HUD e efeitos
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.of("aurorahud", "modules"),
                (ctx, tick) -> ModuleManager.renderHud(ctx));

        // Mira customizada: substitui a mira original quando o modulo esta ligado
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (ctx, tick) -> {
            CrosshairModule c = ModuleManager.crosshair;
            if (c != null && c.isEnabled()) {
                if (!c.hidesVanilla()) original.render(ctx, tick);
                c.draw(ctx);
            } else {
                original.render(ctx, tick);
            }
        });
    }
}
