package dev.aurorahud.gui;

import net.minecraft.client.gui.DrawContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Desenha a imagem de fundo do menu usando so retangulos coloridos.
 * Assim nao depende da API de texturas, que muda bastante entre versoes.
 */
public final class BackgroundImage {
    private static final String PATH = "/assets/aurorahud/aurora_bg.png";
    private static final int CELL = 4;

    private static BufferedImage src;
    private static boolean tried;
    private static int[] grid;
    private static int gCols, gRows, gW, gH;

    private BackgroundImage() {}

    private static void load() {
        if (tried) return;
        tried = true;
        try (InputStream in = BackgroundImage.class.getResourceAsStream(PATH)) {
            if (in != null) src = ImageIO.read(in);
        } catch (Exception e) {
            src = null;
        }
    }

    public static boolean available() {
        load();
        return src != null;
    }

    private static void build(int cols, int rows, int w, int h) {
        int sw = src.getWidth(), sh = src.getHeight();
        double scale = Math.max(w / (double) sw, h / (double) sh);   // "cover"
        double viewW = w / scale, viewH = h / scale;
        double x0 = (sw - viewW) / 2.0, y0 = (sh - viewH) / 2.0;

        grid = new int[cols * rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double fx0 = x0 + (c * CELL) / scale, fx1 = x0 + ((c + 1) * CELL) / scale;
                double fy0 = y0 + (r * CELL) / scale, fy1 = y0 + ((r + 1) * CELL) / scale;
                int rr = 0, gg = 0, bb = 0, n = 0;
                for (int sy = 0; sy < 3; sy++) {
                    for (int sx = 0; sx < 3; sx++) {
                        int px = (int) (fx0 + (fx1 - fx0) * (sx + 0.5) / 3.0);
                        int py = (int) (fy0 + (fy1 - fy0) * (sy + 0.5) / 3.0);
                        px = Math.max(0, Math.min(sw - 1, px));
                        py = Math.max(0, Math.min(sh - 1, py));
                        int p = src.getRGB(px, py);
                        rr += (p >> 16) & 255;
                        gg += (p >> 8) & 255;
                        bb += p & 255;
                        n++;
                    }
                }
                // quantiza para juntar faixas iguais e desenhar menos retangulos
                int qr = ((rr / n) >> 2) << 2, qg = ((gg / n) >> 2) << 2, qb = ((bb / n) >> 2) << 2;
                grid[r * cols + c] = (qr << 16) | (qg << 8) | qb;
            }
        }
        gCols = cols;
        gRows = rows;
        gW = w;
        gH = h;
    }

    public static void draw(DrawContext ctx, int x, int y, int w, int h) {
        load();
        if (src == null || w <= 0 || h <= 0) return;
        int cols = (w + CELL - 1) / CELL, rows = (h + CELL - 1) / CELL;
        if (grid == null || w != gW || h != gH || cols != gCols || rows != gRows) build(cols, rows, w, h);

        for (int r = 0; r < rows; r++) {
            int c = 0;
            while (c < cols) {
                int col = grid[r * cols + c];
                int c2 = c + 1;
                while (c2 < cols && grid[r * cols + c2] == col) c2++;
                ctx.fill(x + c * CELL, y + r * CELL,
                        Math.min(x + w, x + c2 * CELL), Math.min(y + h, y + (r + 1) * CELL),
                        0xFF000000 | col);
                c = c2;
            }
        }
    }
}
