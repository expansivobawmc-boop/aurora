package dev.aurorahud.module;

public abstract class Setting {
    public final String name;
    protected Setting(String name) { this.name = name; }
    /** Valor usado para detectar mudancas. */
    public abstract int key();
}
