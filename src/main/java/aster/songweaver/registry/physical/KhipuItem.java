package aster.songweaver.registry.physical;

import aster.songweaver.registry.NoteHolderItem;
import aster.songweaver.system.spell.definition.Note;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
public class KhipuItem extends Item {

    private static final String COORD_KEY = "Coordinates";
    private static final String POS_KEY = "Pos";
    private static final String DIM_KEY = "Dimension";

    public KhipuItem(Settings settings) {
        super(settings);
    }

    /* ======================
       STORE / READ COORDS
       ====================== */

    public static void storeCoords(ItemStack stack, PlayerEntity player) {
        NbtCompound coordTag = new NbtCompound();

        BlockPos pos = player.getBlockPos();
        coordTag.putLong(POS_KEY, pos.asLong());
        coordTag.putString(
                DIM_KEY,
                player.getWorld().getRegistryKey().getValue().toString()
        );

        stack.getOrCreateNbt().put(COORD_KEY, coordTag);
    }

    @Nullable
    public static BlockPos readCoords(ItemStack stack) {
        if (!stack.hasNbt()) return null;

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains(COORD_KEY, NbtElement.COMPOUND_TYPE)) return null;

        NbtCompound coordTag = nbt.getCompound(COORD_KEY);
        return BlockPos.fromLong(coordTag.getLong(POS_KEY));
    }


    public static boolean hasCoords(ItemStack stack) {
        return stack.hasNbt()
                && stack.getNbt().contains(COORD_KEY, NbtElement.COMPOUND_TYPE);
    }

    @Nullable
    public static BlockPos getBlockPos(ItemStack stack) {
        if (!hasCoords(stack)) return null;

        NbtCompound tag = stack.getNbt().getCompound(COORD_KEY);
        return BlockPos.fromLong(tag.getLong(POS_KEY));
    }

    @Nullable
    public static RegistryKey<World> getDimension(ItemStack stack) {
        if (!hasCoords(stack)) return null;

        Identifier id = Identifier.tryParse(
                stack.getNbt()
                        .getCompound(COORD_KEY)
                        .getString(DIM_KEY)
        );

        return id == null
                ? null
                : RegistryKey.of(RegistryKeys.WORLD, id);
    }


    /* ======================
       RIGHT CLICK BEHAVIOR
       ====================== */

    @Override
    public TypedActionResult<ItemStack> use(World world,
                                            PlayerEntity player,
                                            Hand hand) {

        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            if (player.isSneaking()) {
                stack.removeSubNbt(COORD_KEY);
                player.sendMessage(
                        Text.literal("Cleared."),
                        true
                );
                return TypedActionResult.success(stack);

            }

            storeCoords(stack, player);

            player.sendMessage(
                    Text.literal("The khipu binds itself to this place."),
                    true
            );
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.success(stack);
    }

    /* ======================
       TOOLTIP
       ====================== */

    @Override
    public void appendTooltip(ItemStack stack,
                              @Nullable World world,
                              List<Text> tooltip,
                              TooltipContext context) {

        BlockPos pos = readCoords(stack);

        if (pos != null) {
            tooltip.add(Text.literal("Bound Coordinates:")
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.literal(
                    "X: " + pos.getX() +
                            " Y: " + pos.getY() +
                            " Z: " + pos.getZ()
            ).formatted(Formatting.AQUA));
        } else {
            tooltip.add(Text.literal("Unbound")
                    .formatted(Formatting.DARK_GRAY));
        }
    }
}

