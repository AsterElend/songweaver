package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.MagicRegistry;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class GrandLoomBlockEntity extends BlockEntity {
    public ServerPlayerEntity lastCaster;
    public Tier stockpiledTier;

@Nullable
   private RitualInstance activeRitual;
private int ritualTicks;

    public record RitualInstance(
            RitualDefinition definition,
            ServerPlayerEntity caster
    ){}

    public boolean hasActiveRitual(){
        return activeRitual != null;
    }






    public static @Nullable GrandLoomBlockEntity findNearby(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos();
        int radius = 5;

        // Iterate over a cube around the player
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof GrandLoomBlockEntity ritualController) {
                        return ritualController; // found one
                    }
                }
            }
        }

        return null; // none found
    }

    public  @Nullable MusicStandBlockEntity getMusicStand(BlockPos center) {
        ServerWorld world = (ServerWorld) this.world;

        int radius = 5;

        // Iterate over a cube around the player
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof MusicStandBlockEntity musicStand) {
                        return musicStand; // found one
                    }
                }
            }
        }

        return null; // none found
    }

    public  @Nullable KhipuHookBlockEntity getKhipuHook(BlockPos center) {
        ServerWorld world = (ServerWorld) this.world;

        int radius = 5;

        // Iterate over a cube around the player
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof KhipuHookBlockEntity khipuHook) {
                        return khipuHook; // found one
                    }
                }
            }
        }

        return null; // none found
    }

    public boolean cancelRitual(PlayerEntity player) {
        if (activeRitual == null) return false;

        if (player != activeRitual.caster()) return false;

        activeRitual = null;
        ritualTicks = 0;

        markDirty();
        return true;
    }






    public GrandLoomBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.RITUAL_ENTITY, pos, state);
    }

    public void tryStartRitual(ServerPlayerEntity player,
                               RitualDefinition ritual) {



        // Requirements
        for (Requirement req : ritual.requirements()) {
            CastFeedback failure = req.check(player, this, true);
            if (failure != null) {
                SongServerCasting.sendFeedback(player, failure);
                return;
            }
        }

        // Ingredient availability
        if (!consumeIngredients(ritual.ingredients())) {

            SongServerCasting.sendFeedback(
                    player,
                    CastFeedback.NO_COMPONENTS
            );
            return;
        }



        this.activeRitual =
                new RitualInstance(ritual, player);

        this.ritualTicks = 0;

        markDirty();
    }

    private List<BobbinBlockEntity> findNearbyBobbins() {
        List<BobbinBlockEntity> bobbins = new ArrayList<>();
        int range = 6;

        BlockPos start = pos.add(-range, -range, -range);
        BlockPos end = pos.add(range, range, range);

        for (BlockPos p : BlockPos.iterate(start, end)) {
            BlockEntity be = world.getBlockEntity(p);
            if (be instanceof BobbinBlockEntity bobbin) {
                bobbins.add(bobbin);
            }
        }

        return bobbins;
    }

    private boolean consumeIngredients(List<ItemStack> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return false;

        List<BobbinBlockEntity> bobbins = findNearbyBobbins();

        for (ItemStack required : ingredients) {
            int remaining = required.getCount();

            for (BobbinBlockEntity bobbin : bobbins) {
                ItemStack stack = bobbin.getStack();

                if (ItemStack.areItemsEqual(stack, required)) {
                    remaining -= bobbin.removeItems(remaining);
                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) return false; // failed to find enough
        }

        return true; // all ingredients satisfied
    }


    public void tick() {

        if (!(world instanceof ServerWorld serverWorld)) return;
        if (activeRitual == null) return;

        ritualTicks++;

        // Optional per-tick logic
        int interval = activeRitual.definition.tickInterval();
        if (interval > 0 && ritualTicks % interval == 0) {
            onRitualTick(serverWorld);
        }

        // Finish ritual
        if (ritualTicks >= activeRitual.definition.duration()) {
            completeRitual(serverWorld);
        }
    }

    private void onRitualTick(ServerWorld world) {
        // Example: ambient particles
        world.spawnParticles(
                ParticleTypes.ENCHANT,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                6,
                0.4, 0.2, 0.4,
                0.01
        );
    }


    private void completeRitual(ServerWorld world) {

        Ritual ritual = MagicRegistry.getRitual(activeRitual.definition().ritualId());
        ServerPlayerEntity caster = activeRitual.caster;
        if (ritual != null) {
            ritual.ritualCast(world, caster, this , activeRitual.definition.data());
        }

        // Apply drawbacks AFTER completion
        for (Drawback drawback : activeRitual.definition.drawbacks()) {
            drawback.apply(caster);
        }

        clearRitual();
    }

    private void clearRitual() {
        lastCaster = activeRitual.caster;
        activeRitual = null;
        ritualTicks = 0;
        markDirty();
    }

}
