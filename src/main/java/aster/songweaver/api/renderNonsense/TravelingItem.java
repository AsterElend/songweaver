package aster.songweaver.api.renderNonsense;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

/**
 * @param color for the burst
 */
@Environment(EnvType.CLIENT)
public record TravelingItem(Vec3d start, Vec3d end, ItemStack stack, long spawnTick, int travelTicks, Vector3f color) {

    // 0.0 → 1.0, smooth
    public double getT(long currentTick, float partialTick) {
        double elapsed = (currentTick - spawnTick) + partialTick;
        return Math.min(1.0, Math.max(0.0, elapsed / travelTicks));
    }

    public boolean isDone(long currentTick) {
        return currentTick >= spawnTick + travelTicks;
    }
}