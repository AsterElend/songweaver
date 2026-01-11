package aster.songweaver.ritual;

import aster.songweaver.registry.LoomMiscRegistry;
import aster.songweaver.registry.RitualRegistry;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.definition.*;
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


public class RitualControllerBlockEntity extends BlockEntity {

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




    public static @Nullable RitualControllerBlockEntity findNearby(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos();
        int radius = 5;

        // Iterate over a cube around the player
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof RitualControllerBlockEntity ritualController) {
                        return ritualController; // found one
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






    public RitualControllerBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.RITUAL_ENTITY, pos, state);
    }

    public void tryStartRitual(ServerPlayerEntity player,
                               RitualDefinition ritual) {



        // Requirements
        for (Requirement req : ritual.requirements()) {
            CastFailure failure = req.check(player);
            if (failure != null) {
                SongServerCasting.sendFailure(player, failure);
                return;
            }
        }

        // Ingredient availability
        if (!consumeIngredients(ritual.ingredients())) {
            SongServerCasting.sendFailure(
                    player,
                    CastFailure.NO_COMPONENTS
            );
            return;
        }

        // Drawbacks at start
        for (Drawback drawback : ritual.drawbacks()) {
            drawback.apply(player);
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

        Ritual ritual = RitualRegistry.get(activeRitual.definition().ritualId());
        ServerPlayerEntity caster = activeRitual.caster;
        if (ritual != null) {
            ritual.ritualCast(world, caster, activeRitual.definition.data());
        }

        // Apply drawbacks AFTER completion
        for (Drawback drawback : activeRitual.definition.drawbacks()) {
            drawback.apply(caster);
        }

        clearRitual();
    }

    private void clearRitual() {
        activeRitual = null;
        ritualTicks = 0;
        markDirty();
    }

}
