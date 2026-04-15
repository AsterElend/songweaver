package aster.songweaver.api.renderNonsense;

import net.minecraft.util.math.random.Random;

public class CellularStaticAnimator {

    private final int width;
    private final int height;
    private final boolean flowing;
    private boolean[][] grid;
    private final Random random = Random.create();

    private static final int COLOR_ON  = 0xFFD966FF;
    private static final int COLOR_DIM = 0xFFAA44CC;
    private static final int COLOR_OFF = 0xFF220033;

    public CellularStaticAnimator(int width, int height, boolean flowing) {
        this.width = width;
        this.height = height;
        this.flowing = flowing;
        this.grid = new boolean[height][width];
        randomize();
    }

    private void randomize() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                grid[y][x] = random.nextFloat() < 0.4f;
    }

    public void step() {
        boolean[][] next = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int neighbors = countNeighbors(x, y);
                int hNeighbors = flowing ? countHorizontalNeighbors(x, y) : 0;
                boolean alive = grid[y][x];

                if (flowing) {
                    // Bias: horizontal neighbors count extra
                    // so patterns stretch and flow sideways
                    int weighted = neighbors + hNeighbors;
                    next[y][x] = alive
                            ? (weighted >= 2 && weighted <= 4)
                            : (weighted == 3 || weighted == 4);
                } else {
                    next[y][x] = alive
                            ? (neighbors == 2 || neighbors == 3)
                            : (neighbors == 3);
                }
            }
        }

        // Shift the whole grid one pixel left each step for scroll effect
        if (flowing) {
            boolean[][] shifted = new boolean[height][width];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    shifted[y][x] = next[y][(x + 1) % width];
            grid = shifted;
        } else {
            grid = next;
        }

        if (random.nextInt(40) == 0) randomize();
    }

    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = (x + dx + width)  % width;
                int ny = (y + dy + height) % height;
                if (grid[ny][nx]) count++;
            }
        }
        return count;
    }

    // Only counts left and right neighbours
    private int countHorizontalNeighbors(int x, int y) {
        int left  = grid[y][(x - 1 + width) % width] ? 1 : 0;
        int right = grid[y][(x + 1)         % width] ? 1 : 0;
        return left + right;
    }

    public int[] toPixels() {
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int neighbors = countNeighbors(x, y);
                pixels[y * width + x] = grid[y][x]
                        ? (neighbors >= 4 ? COLOR_ON : COLOR_DIM)
                        : COLOR_OFF;
            }
        }
        return pixels;
    }
}