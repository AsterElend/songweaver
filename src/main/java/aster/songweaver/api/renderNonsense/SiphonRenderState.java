package aster.songweaver.api.renderNonsense;

import aster.songweaver.api.packetry.RitualSiphonPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SiphonRenderState {
    // Active lines: key = bobbinPos, value = controllerPos + color + expiry tick
    public record ActiveLine(Vec3d start, Vec3d end, Vector3f color, long expiryTick) {}

    private static final List<TravelingItem> travelingItems = new ArrayList<>();
    private static final List<ActiveLine> activeLines = new ArrayList<>();

    public static void enqueue(World world, RitualSiphonPacket packet) {
        DyeColor dye = DyeColor.values()[packet.color() % DyeColor.values().length];
        float[] rgb = dye.getColorComponents();
        Vector3f color = new Vector3f(rgb[0], rgb[1], rgb[2]);

        Vec3d start = Vec3d.ofCenter(packet.bobbinPos());
        Vec3d end = Vec3d.ofCenter(packet.controllerPos()).add(0, 1.5, 0);

        long now = world.getTime();
        int travelTicks = 30;
        int count = Math.min(packet.itemCount(), 8);

        ItemStack stack = new ItemStack(
                Registries.ITEM.get(packet.transferItem())
        );

        // Stagger multiple items along the line
        for (int i = 0; i < count; i++) {
            long staggeredSpawn = now + (long)(i * travelTicks / (float) count * 0.5f);
            travelingItems.add(new TravelingItem(start, end, stack, staggeredSpawn, travelTicks, color));
        }

        // Refresh or add the line, keep it alive until all items arrive
        long lineExpiry = now + travelTicks + count * 5L + 10L;
        activeLines.removeIf(l -> l.start().equals(start) && l.end().equals(end));
        activeLines.add(new ActiveLine(start, end, color, lineExpiry));
    }

    public static void tickCleanup(World world) {
        long now = world.getTime();

        travelingItems.removeIf(item -> {
            if (item.isDone(now)) {
                spawnBurst(world, item.end(), item.color());
                return true;
            }
            return false;
        });

        activeLines.removeIf(line -> now > line.expiryTick());
    }

    private static void spawnBurst(World world, Vec3d pos, Vector3f color) {
        Random random = Random.create();
        for (int i = 0; i < 10; i++) {
            world.addParticle(
                    new DustParticleEffect(color, 0.8f),
                    pos.x, pos.y, pos.z,
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3
            );
        }
    }

    public static List<TravelingItem> getTravelingItems() { return travelingItems; }
    public static List<ActiveLine> getActiveLines() { return activeLines; }
}