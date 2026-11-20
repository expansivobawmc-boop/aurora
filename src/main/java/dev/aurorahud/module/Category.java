package dev.aurorahud.module;

public enum Category {
    PVP("PvP"), VISUALS("Visuals"), HUD("HUD"), PLAYER("Player"), MISC("Misc");

    public final String label;
    Category(String label) { this.label = label; }
}
