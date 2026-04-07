import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class StaticTextureSheetGenerator {

    /**
     * Generates a black-and-white static texture sheet.
     *
     * @param frameSize size of each frame in pixels (e.g., 16)
     * @param frames    number of frames stacked vertically (e.g., 16)
     * @param output    output file path
     * @throws IOException if writing fails
     */
    public static void generateStaticSheet(int frameSize, int frames, File output) throws IOException {
        int width = frameSize;
        int height = frameSize * frames;

        BufferedImage sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Random random = new Random();

        for (int f = 0; f < frames; f++) {
            for (int y = 0; y < frameSize; y++) {
                for (int x = 0; x < frameSize; x++) {
                    // Random black or white pixel
                    int color = random.nextBoolean() ? 0xFFFFFFFF : 0xFF000000;
                    sheet.setRGB(x, y + f * frameSize, color);
                }
            }
        }

        ImageIO.write(sheet, "png", output);
        System.out.println("Generated static texture sheet: " + output.getAbsolutePath());
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        generateStaticSheet(16, 16, new File("lethean_water_static.png"));
    }
}