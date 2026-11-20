package dev.aurorahud.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Fonte propria do client (Outfit, licenca OFL). Para voltar a fonte padrao, troque USE_CUSTOM para false. */
public final class Fonts {
    public static final boolean USE_CUSTOM = true;

    private static final Style REGULAR = Style.EMPTY.withFont(
            new StyleSpriteSource.Font(Identifier.of("aurorahud", "ui")));
    private static final Style BOLD = Style.EMPTY.withFont(
            new StyleSpriteSource.Font(Identifier.of("aurorahud", "ui_bold")));

    private Fonts() {}

    public static Text t(String s) {
        return USE_CUSTOM ? Text.literal(s).setStyle(REGULAR) : Text.literal(s);
    }

    public static Text bold(String s) {
        return USE_CUSTOM ? Text.literal(s).setStyle(BOLD) : Text.literal(s);
    }

    public static int width(TextRenderer tr, String s) { return tr.getWidth(t(s)); }
    public static int widthBold(TextRenderer tr, String s) { return tr.getWidth(bold(s)); }

    public static void draw(DrawContext ctx, TextRenderer tr, String s, int x, int y, int color) {
        ctx.drawTextWithShadow(tr, t(s), x, y, color);
    }

    public static void drawBold(DrawContext ctx, TextRenderer tr, String s, int x, int y, int color) {
        ctx.drawTextWithShadow(tr, bold(s), x, y, color);
    }
}
