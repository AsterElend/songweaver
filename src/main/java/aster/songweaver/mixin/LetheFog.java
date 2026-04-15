package aster.songweaver.mixin;

import aster.songweaver.registry.LoomTags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class LetheFog {

    @Inject(
            method = "applyFog",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onApplyFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            float viewDistance,
            boolean thickFog,
            float tickDelta,
            CallbackInfo ci
    ) {
        if (!isInCustomFluid(camera)) return;

        // Mimic what water fog does:
        if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            RenderSystem.setShaderFogStart(0.0f);
            RenderSystem.setShaderFogEnd(viewDistance * 0.5f); // tighten for denser fog
        } else {
            RenderSystem.setShaderFogStart(0.25f);
            RenderSystem.setShaderFogEnd(12.0f); // short range = thick fog
        }

        ci.cancel();
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onRender(
            Camera camera,
            float tickDelta,
            ClientWorld world,
            int viewDistance,
            float skyDarkness,
            CallbackInfo ci
    ) {
        if (!isInCustomFluid(camera)) return;

        // Your fluid's fog color (R, G, B) — this example is a murky green
        RenderSystem.clearColor(1f, 0.125f, 0.624f, 0.9f);
        ci.cancel();
    }

    @Unique
    private static boolean isInCustomFluid(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        World world = entity.getWorld();
        BlockPos eyePos = BlockPos.ofFloored(camera.getPos());
        FluidState fluidState = world.getFluidState(eyePos);
        return fluidState.isIn(LoomTags.LETHE);
    }
}
