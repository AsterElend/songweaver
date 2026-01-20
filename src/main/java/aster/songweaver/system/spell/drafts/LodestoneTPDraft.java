package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class LodestoneTPDraft implements Draft {

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity player, @Nullable JsonObject data) {
        ItemStack compass = player.getOffHandStack();

        if (!isActiveLodestoneCompass(compass)) {
            player.sendMessage(
                    Text.literal("Your compass does not sing of a lodestone."),
                    true
            );
            return;
        }

        BlockPos lodestonePos = getLodestonePos(compass);
        Identifier lodestoneDim = getLodestoneDimension(compass);

        // Dimension check
        if (!world.getRegistryKey().getValue().equals(lodestoneDim)) {
            player.sendMessage(
                    Text.literal("The lodestone lies beyond this world."),
                    true
            );
            return;
        }

        // Block still exists?
        if (!(world.getBlockState(lodestonePos).isOf(Blocks.LODESTONE))) {
            player.sendMessage(
                    Text.literal("The lodestone has gone silent."),
                    true
            );
            return;
        }

        // Find safe landing position
        Vec3d target = findSafeLanding(world, lodestonePos);
        if (target == null) {
            player.sendMessage(
                    Text.literal("The song finds no safe place to land."),
                    true
            );
            return;
        }

        player.requestTeleport(target.x, target.y, target.z);
        player.fallDistance = 0;

    }

    private static boolean isActiveLodestoneCompass(ItemStack stack) {
        if (stack.getItem() != Items.COMPASS) return false;

        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        return nbt.contains("LodestonePos", NbtElement.COMPOUND_TYPE)
                && nbt.getBoolean("LodestoneTracked");
    }

    private static BlockPos getLodestonePos(ItemStack stack) {
        NbtCompound pos = stack.getNbt().getCompound("LodestonePos");
        return new BlockPos(
                pos.getInt("X"),
                pos.getInt("Y"),
                pos.getInt("Z")
        );
    }

    private static Identifier getLodestoneDimension(ItemStack stack) {
        return new Identifier(stack.getNbt().getString("LodestoneDimension"));
    }

    @Nullable
    private static Vec3d findSafeLanding(ServerWorld world, BlockPos base) {

        for (int yOffset = 1; yOffset <= 3; yOffset++) {
            BlockPos pos = base.up(yOffset);

            if (world.getBlockState(pos).isAir()
                    && world.getBlockState(pos.up()).isAir()) {

                return Vec3d.ofBottomCenter(pos);
            }
        }

        return null;
    }



}
