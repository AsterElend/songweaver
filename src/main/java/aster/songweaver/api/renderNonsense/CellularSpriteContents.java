package aster.songweaver.api.renderNonsense;

import aster.songweaver.mixin.SpriteContentsAccessor;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.util.Identifier;

public class CellularSpriteContents extends SpriteContents {
    private final CellularStaticAnimator automaton;
    private final NativeImage image;

    public CellularSpriteContents(Identifier id, int size, boolean flowing) {

        super(id, new SpriteDimensions(size, size),
                new NativeImage(size, size, false),
                AnimationResourceMetadata.EMPTY);

        this.image = ((SpriteContentsAccessor) this).getImage();
        this.automaton = new CellularStaticAnimator(size, size, flowing);
    }


    @Override
    public void upload(int x, int y) {
        automaton.step();
        int[] pixels = automaton.toPixels();

        for (int py = 0; py < getHeight(); py++)
            for (int px = 0; px < getWidth(); px++)
                image.setColor(px, py, pixels[py * getWidth() + px]);

        image.upload(0, x, y, 0, 0, getWidth(), getHeight(), false, false);
    }
}
