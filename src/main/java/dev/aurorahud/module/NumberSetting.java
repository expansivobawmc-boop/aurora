package dev.aurorahud.module;

public class NumberSetting extends Setting {
    public final double min, max, step;
    public double value;

    public NumberSetting(String name, double min, double max, double step, double def) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = def;
    }

    public void set(double v) {
        if (step > 0) v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    public int asInt() { return (int) Math.round(value); }
    public float asFloat() { return (float) value; }
    public double fraction() { return (value - min) / (max - min); }

    @Override
    public int key() { return (int) Math.round(value * 100); }
}
