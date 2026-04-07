package aster.songweaver.client;

import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

public class SongweaverRenderLayers extends RenderLayer{

    public static final RenderLayer WARD_LAYER = RenderLayer.of(
            "ward_layer",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.COLOR_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .writeMaskState(RenderPhase.ALL_MASK)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(true)
    );

    public static final RenderLayer RIFT_LAYER = RenderLayer.of(
            "tear_portal",
            VertexFormats.POSITION_COLOR_TEXTURE,
            VertexFormat.DrawMode.TRIANGLES,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(new RenderPhase.ShaderProgram(() -> {
                        return RiftBERenderer.SHADER != null
                                ? RiftBERenderer.SHADER
                                : GameRenderer.getPositionTexProgram();
                    }))
                    .texture(new RenderPhase.Texture(
                            new Identifier("songweaver", "textures/entity/rift_texture.png"),
                            false,
                            false
                    ))
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                    .build(false)
    );

    public SongweaverRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}