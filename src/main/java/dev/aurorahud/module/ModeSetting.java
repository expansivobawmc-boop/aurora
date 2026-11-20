package dev.aurorahud.module;

public class ModeSetting extends Setting {
    public final String[] modes;
    public int index;

    public ModeSetting(String name, int index, String... modes) {
        super(name);
        this.index = index;
        this.modes = modes;
    }

    public String get() { return modes[index]; }
    public void cycle() { index = (index + 1) % modes.length; }

    @Override
    public int key() { return index; }
}
