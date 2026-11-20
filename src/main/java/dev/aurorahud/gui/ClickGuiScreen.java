package dev.aurorahud.gui;

import dev.aurorahud.module.BooleanSetting;
import dev.aurorahud.module.Category;
import dev.aurorahud.module.ColorSetting;
import dev.aurorahud.module.ModeSetting;
import dev.aurorahud.module.Module;
import dev.aurorahud.module.ModuleManager;
import dev.aurorahud.module.NumberSetting;
import dev.aurorahud.module.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClickGuiScreen extends Screen {
    private static final int HEADER = 36;
    private static final int SIDE = 176;
    private static final int ROW_H = 26;
    private static final int ROW_GAP = 4;

    private static final int BG = 0xF00D0D15;
    private static final int GRAY = 0xFF9A9AB0;
    private static final int OFF = 0xFF3A3A4A;

    private static Category cat = Category.PVP;
    private static Module sel;
    private static String query = "";
    private static int sideScroll, scroll;

    private record Hit(int x, int y, int w, int h, int vy1, int vy2, Runnable action) {}
    private record SliderHit(int x, int y, int w, int h, int vy1, int vy2, NumberSetting s, int tx, int tw) {}

    private final List<Hit> hits = new ArrayList<>();
    private final List<SliderHit> sliders = new ArrayList<>();
    private NumberSetting dragging;
    private int dragX, dragW;
    private boolean lastDown = true; // evita clique fantasma ao abrir
    private TextFieldWidget search;
    private int sideContentH, contentH;

    public ClickGuiScreen() {
        super(Text.literal("Aurora"));
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    protected void init() {
        search = new TextFieldWidget(textRenderer, px() + 20, py() + HEADER + 16, SIDE - 42, 12, Text.literal("Search"));
        search.setDrawsBackground(false);
        search.setMaxLength(24);
        search.setPlaceholder(Text.literal("Search modules and settings"));
        search.setText(query);
        search.setChangedListener(s -> { query = s; sideScroll = 0; scroll = 0; });
        addSelectableChild(search);
        setFocused(search);
    }

    // ---------- geometria ----------
    private int pw() { return Math.min(width - 20, 600); }
    private int ph() { return Math.min(height - 20, 340); }
    private int px() { return (width - pw()) / 2; }
    private int py() { return (height - ph()) / 2; }
    private int listTop() { return py() + HEADER + 40; }
    private int listBottom() { return py() + ph() - 38; }
    private int cTop() { return py() + HEADER + 56; }
    private int cBottom() { return py() + ph() - 10; }
    private int cx() { return px() + SIDE + 18; }
    private int cw() { return pw() - SIDE - 36; }

    private int tabW(Category c) { return Fonts.width(textRenderer, c.label) + 22; }
    private int tabX(int i) {
        int x = px() + 112;
        Category[] cs = Category.values();
        for (int k = 0; k < i; k++) x += tabW(cs[k]);
        return x;
    }

    private static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private List<Module> visible() {
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return ModuleManager.byCategory(cat);
        List<Module> out = new ArrayList<>();
        for (Module m : ModuleManager.all()) {
            boolean match = m.name.toLowerCase(Locale.ROOT).contains(q);
            if (!match) {
                for (Setting s : m.settings) {
                    if (s.name.toLowerCase(Locale.ROOT).contains(q)) { match = true; break; }
                }
            }
            if (match) out.add(m);
        }
        return out;
    }

    private int settingH(Setting s) {
        if (s instanceof NumberSetting) return 36;
        if (s instanceof ColorSetting) return 92;
        return 28;
    }

    // ---------- entrada ----------
    private void processInput(int mx, int my, List<Hit> prev, List<SliderHit> prevS) {
        long win = client.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean pressed = down && !lastDown;
        lastDown = down;

        if (dragging != null) {
            if (down) {
                double f = (mx - dragX) / (double) dragW;
                dragging.set(dragging.min + f * (dragging.max - dragging.min));
            } else {
                dragging = null;
            }
        }
        if (!pressed) return;

        for (int i = prevS.size() - 1; i >= 0; i--) {
            SliderHit s = prevS.get(i);
            if (in(mx, my, s.x(), s.y(), s.w(), s.h()) && my >= s.vy1() && my < s.vy2()) {
                dragging = s.s();
                dragX = s.tx();
                dragW = s.tw();
                double f = (mx - dragX) / (double) dragW;
                dragging.set(dragging.min + f * (dragging.max - dragging.min));
                return;
            }
        }
        for (int i = prev.size() - 1; i >= 0; i--) {
            Hit h = prev.get(i);
            if (in(mx, my, h.x(), h.y(), h.w(), h.h()) && my >= h.vy1() && my < h.vy2()) {
                h.action().run();
                return;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        int amt = (int) (-vAmount * 16);
        if (mx < px() + SIDE) {
            int max = Math.max(0, sideContentH - (listBottom() - listTop()));
            sideScroll = Math.max(0, Math.min(max, sideScroll + amt));
        } else {
            int max = Math.max(0, contentH - (cBottom() - cTop()));
            scroll = Math.max(0, Math.min(max, scroll + amt));
        }
        return true;
    }

    // ---------- desenho ----------
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        List<Hit> prev = new ArrayList<>(hits);
        List<SliderHit> prevS = new ArrayList<>(sliders);
        hits.clear();
        sliders.clear();
        processInput(mx, my, prev, prevS);

        List<Module> vis = visible();
        if (sel == null || !vis.contains(sel)) sel = vis.isEmpty() ? null : vis.get(0);

        TextRenderer tr = textRenderer;
        int px = px(), py = py(), pw = pw(), ph = ph();

        HudStyle.ACCENT = ModuleManager.menu.accentArgb();
        boolean aurora = ModuleManager.menu.aurora();

        ctx.fill(0, 0, width, height, 0x90000000);
        HudStyle.roundRect(ctx, px - 1, py - 1, pw + 2, ph + 2, 0x55FFFFFF);
        HudStyle.roundRect(ctx, px, py, pw, ph, BG);
        if (aurora) {
            BackgroundImage.draw(ctx, px + 2, py + 2, pw - 4, ph - 4);
            int a = Math.round(ModuleManager.menu.dimPercent() * 2.55f);
            ctx.fill(px + 2, py + 2, px + pw - 2, py + ph - 2, a << 24);
            ctx.fill(px + 2, py + HEADER + 1, px + SIDE, py + ph - 2, 0x55000000);
        }
        int tint = aurora ? 0x38 : 0x55;
        HudStyle.vGradient(ctx, px + 2, py + HEADER + 1, px + pw - 2, py + ph - 2,
                0x005A2D9C, (tint << 24) | 0x5A2D9C);

        // cabecalho
        HudStyle.logo(ctx, px + 14, py + 10);
        Fonts.drawBold(ctx, tr, "Aurora", px + 36, py + 14, HudStyle.TEXT);
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            final Category c = cats[i];
            int x = tabX(i), w = tabW(c);
            boolean selected = c == cat && query.trim().isEmpty();
            boolean hov = in(mx, my, x, py, w, HEADER);
            Fonts.draw(ctx, tr, c.label, x + 11, py + 14,
                    selected ? HudStyle.TEXT : (hov ? 0xFFD0D0E0 : GRAY));
            if (selected) ctx.fill(x + 8, py + HEADER - 2, x + w - 8, py + HEADER, HudStyle.ACCENT);
            hits.add(new Hit(x, py, w, HEADER, 0, height, () -> {
                cat = c;
                sel = null;
                sideScroll = 0;
                scroll = 0;
                search.setText("");
            }));
        }
        ctx.fill(px + 1, py + HEADER, px + pw - 1, py + HEADER + 1, 0x22FFFFFF);

        // busca
        HudStyle.roundRect(ctx, px + 10, py + HEADER + 10, SIDE - 20, 24, 0x30FFFFFF);
        ctx.fill(px + 14, py + HEADER + 16, px + 16, py + HEADER + 28, HudStyle.ACCENT);
        search.render(ctx, mx, my, delta);

        // lista de modulos
        sideContentH = vis.size() * (ROW_H + ROW_GAP);
        int viewH = listBottom() - listTop();
        sideScroll = Math.max(0, Math.min(Math.max(0, sideContentH - viewH), sideScroll));
        ctx.enableScissor(px + 4, listTop(), px + SIDE - 2, listBottom());
        for (int i = 0; i < vis.size(); i++) {
            final Module m = vis.get(i);
            int x = px + 10, w = SIDE - 20;
            int y = listTop() + i * (ROW_H + ROW_GAP) - sideScroll;
            if (y + ROW_H < listTop() || y > listBottom()) continue;
            boolean isSel = m == sel;
            boolean hov = in(mx, my, x, y, w, ROW_H) && my >= listTop() && my < listBottom();
            if (isSel) HudStyle.roundRect(ctx, x, y, w, ROW_H, 0x558B7CFF);
            else if (hov) HudStyle.roundRect(ctx, x, y, w, ROW_H, 0x22FFFFFF);
            Fonts.draw(ctx, tr, m.name, x + 10, y + 9, isSel ? HudStyle.TEXT : 0xFFC8C8D8);
            drawSwitch(ctx, x + w - 32, y + 7, m.isEnabled());
            hits.add(new Hit(x, y, w, ROW_H, listTop(), listBottom(), () -> { sel = m; scroll = 0; }));
            hits.add(new Hit(x + w - 36, y + 3, 36, 20, listTop(), listBottom(), m::toggle));
        }
        ctx.disableScissor();

        // botao Edit HUD
        int bx = px + 10, by = py + ph - 32, bw = SIDE - 20;
        boolean bh = in(mx, my, bx, by, bw, 22);
        HudStyle.roundRect(ctx, bx, by, bw, 22, bh ? 0x55FFFFFF : 0x30FFFFFF);
        String eh = "Edit HUD";
        Fonts.draw(ctx, tr, eh, bx + (bw - Fonts.width(tr, eh)) / 2, by + 7, HudStyle.TEXT);
        hits.add(new Hit(bx, by, bw, 22, 0, height, () -> client.setScreen(new EditHudScreen())));

        ctx.fill(px + SIDE, py + HEADER + 1, px + SIDE + 1, py + ph - 1, 0x22FFFFFF);

        // conteudo
        if (sel == null) {
            Fonts.draw(ctx, tr, "No modules found.", cx(), py + HEADER + 18, GRAY);
            return;
        }
        final Module m = sel;
        Fonts.drawBold(ctx, tr, m.name, cx(), py + HEADER + 13, HudStyle.TEXT);
        Fonts.draw(ctx, tr, m.description, cx(), py + HEADER + 28, GRAY);
        drawSwitch(ctx, cx() + cw() - 26, py + HEADER + 16, m.isEnabled());
        hits.add(new Hit(cx() + cw() - 34, py + HEADER + 10, 36, 22, 0, height, m::toggle));
        ctx.fill(cx(), py + HEADER + 46, cx() + cw(), py + HEADER + 47, 0x22FFFFFF);

        int vpTop = cTop(), vpBot = cBottom();
        scroll = Math.max(0, Math.min(Math.max(0, contentH - (vpBot - vpTop)), scroll));
        int y = vpTop - scroll;
        int total = 0;
        ctx.enableScissor(px + SIDE + 1, vpTop, px + pw - 2, vpBot);
        if (m.settings.isEmpty()) {
            Fonts.draw(ctx, tr, "This module has no settings.", cx(), vpTop + 6, GRAY);
        }
        for (Setting s : m.settings) {
            int h = settingH(s);
            if (y + h > vpTop && y < vpBot) drawSetting(ctx, s, y, h, vpTop, vpBot);
            y += h;
            total += h;
        }
        ctx.disableScissor();
        contentH = total;
    }

    private void drawSwitch(DrawContext ctx, int x, int y, boolean on) {
        HudStyle.roundRect(ctx, x, y, 26, 12, on ? HudStyle.ACCENT : OFF);
        int kx = on ? x + 15 : x + 1;
        HudStyle.roundRect(ctx, kx, y + 1, 10, 10, 0xFFFFFFFF);
    }

    private void drawSlider(DrawContext ctx, NumberSetting n, int tx, int ty, int tw, int vy1, int vy2) {
        HudStyle.roundRect(ctx, tx, ty, tw, 6, OFF);
        int fw = (int) (tw * n.fraction());
        if (fw > 0) HudStyle.roundRect(ctx, tx, ty, Math.max(fw, 4), 6, HudStyle.ACCENT);
        int kx = Math.max(tx, Math.min(tx + tw - 10, tx + fw - 5));
        HudStyle.roundRect(ctx, kx, ty - 2, 10, 10, 0xFFFFFFFF);
        sliders.add(new SliderHit(tx - 4, ty - 7, tw + 8, 20, vy1, vy2, n, tx, tw));
    }

    private String fmt(NumberSetting n) {
        return n.step >= 1 ? String.valueOf(n.asInt()) : String.format(Locale.ROOT, "%.2f", n.value);
    }

    private void drawSetting(DrawContext ctx, Setting s, int y, int h, int vy1, int vy2) {
        TextRenderer tr = textRenderer;
        int x = cx(), w = cw();

        if (s instanceof BooleanSetting b) {
            Fonts.draw(ctx, tr, b.name, x, y + 10, 0xFFE0E0F0);
            drawSwitch(ctx, x + w - 26, y + 8, b.value);
            hits.add(new Hit(x, y, w, h, vy1, vy2, b::toggle));
        } else if (s instanceof NumberSetting n) {
            Fonts.draw(ctx, tr, n.name, x, y + 5, 0xFFE0E0F0);
            String v = fmt(n);
            Fonts.draw(ctx, tr, v, x + w - Fonts.width(tr, v), y + 5, GRAY);
            drawSlider(ctx, n, x, y + 21, w, vy1, vy2);
        } else if (s instanceof ModeSetting ms) {
            Fonts.draw(ctx, tr, ms.name, x, y + 10, 0xFFE0E0F0);
            String v = ms.get();
            int pw = Fonts.width(tr, v) + 18;
            HudStyle.roundRect(ctx, x + w - pw, y + 4, pw, 20, 0x40FFFFFF);
            Fonts.draw(ctx, tr, v, x + w - pw + 9, y + 10, HudStyle.TEXT);
            hits.add(new Hit(x + w - pw, y + 4, pw, 20, vy1, vy2, ms::cycle));
        } else if (s instanceof ColorSetting cs) {
            Fonts.draw(ctx, tr, cs.name, x, y + 6, 0xFFE0E0F0);
            HudStyle.roundRect(ctx, x + w - 30, y + 3, 30, 16, 0xFF000000 | cs.rgb());
            NumberSetting[] ch = {cs.r, cs.g, cs.b};
            for (int k = 0; k < 3; k++) {
                int ry = y + 26 + k * 20;
                Fonts.draw(ctx, tr, ch[k].name, x, ry + 1, GRAY);
                drawSlider(ctx, ch[k], x + 52, ry + 3, w - 52, vy1, vy2);
            }
        }
        ctx.fill(x, y + h - 1, x + w, y + h, 0x14FFFFFF);
    }
}
