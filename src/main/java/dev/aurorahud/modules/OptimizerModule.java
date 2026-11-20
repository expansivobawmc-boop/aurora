package dev.aurorahud.modules;

import dev.aurorahud.module.Category;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

import java.util.function.Consumer;

/**
 * Otimizador generico: guarda o valor original de uma opcao do jogo, aplica um ajuste
 * e restaura ao desligar. Reaplica quando as configuracoes do modulo mudam.
 */
public class OptimizerModule extends Module {
    public interface Capture { Runnable run(GameOptions options); }

    private final Capture capture;
    private final Consumer<GameOptions> apply;
    private Runnable restore;
    private int lastKey = Integer.MIN_VALUE;
    private int tick;

    public OptimizerModule(String name, String description, Capture capture,
                           Consumer<GameOptions> apply, Setting... extra) {
        super(name, description, Category.OPTIMIZERS, true);
        this.capture = capture;
        this.apply = apply;
        for (Setting s : extra) add(s);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (++tick % 10 != 0) return;
        int key = settingsKey();
        if (restore != null && key == lastKey) return;
        if (restore != null) restore.run();
        restore = capture.run(mc.options);
        apply.accept(mc.options);
        lastKey = key;
    }

    @Override
    public void onDisable() {
        if (restore != null) {
            restore.run();
            restore = null;
            lastKey = Integer.MIN_VALUE;
        }
    }
}
