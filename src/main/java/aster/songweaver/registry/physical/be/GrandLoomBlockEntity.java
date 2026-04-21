package aster.songweaver.registry.physical.be;

import aster.songweaver.api.cast.SongweaverPackets;
import aster.songweaver.api.weaving.*;
import aster.songweaver.api.weaving.loaders.RitualReloadListener;
import aster.songweaver.registry.MagicRegistry;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GrandLoomBlockEntity extends BlockEntity {
    public Tier stockpiledTier = Tier.WOOD;
    public UUID redstonePlayer = UUID.randomUUID();
@Nullable
   private RitualDefinition activeRitual;
private int ritualTicks;



    public boolean hasActiveRitual(){
        return activeRitual != null;
    }

    public GrandLoomBlockEntity(BlockPos pos, BlockState state ) {
        super(LoomBlockEntities.GRAND_LOOM_BLOCK_ENTITY, pos, state);
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

    public boolean cancelRitual() {
        if (activeRitual == null) return false;


        activeRitual = null;
        ritualTicks = 0;

        markDirty();
        return true;
    }








    public void tryStartRitual(ServerPlayerEntity player,
                               RitualDefinition ritual) {



        // Requirements
        for (Requirement req : ritual.requirements()) {
            CastFeedback failure = req.check(player, this, true);
            if (failure != null) {
                SongweaverPackets.sendFeedback(player, failure);
                return;
            }
        }

        // Ingredient availability
        if (!consumeIngredients(ritual.ingredients())) {

            SongweaverPackets.sendFeedback(
                    player,
                    CastFeedback.NO_COMPONENTS
            );
            return;
        }

        this.activeRitual = ritual;


        this.ritualTicks = 0;


        for (Drawback drawback : ritual.drawbacks()) {
            drawback.apply(player);
        }

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
        if (ingredients == null || ingredients.isEmpty()) return true;

        List<BobbinBlockEntity> bobbins = findNearbyBobbins();

        // --- Phase 1: dry run to verify all ingredients are available ---
        for (ItemStack required : ingredients) {
            int remaining = required.getCount();

            for (BobbinBlockEntity bobbin : bobbins) {
                ItemStack stack = bobbin.getStack();

                if (ItemStack.areItemsEqual(stack, required)) {
                    remaining -= Math.min(stack.getCount(), remaining);
                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) return false; // not enough — nothing consumed yet
        }

        // --- Phase 2: all ingredients confirmed, now actually consume ---
        for (ItemStack required : ingredients) {
            int remaining = required.getCount(); // fresh count per ingredient

            for (BobbinBlockEntity bobbin : bobbins) {
                if (remaining <= 0) break; // <-- make sure this is here
                ItemStack stack = bobbin.getStack();

                if (ItemStack.areItemsEqual(stack, required)) {
                    int toTake = Math.min(stack.getCount(), remaining); // cap it
                    remaining -= bobbin.removeCountForRitual(toTake, this.getPos());
                }
            }
        }

        return true;
    }


    public void tick() {

        if (!(world instanceof ServerWorld serverWorld)) return;

        if (activeRitual == null) return;


        ritualTicks++;

        // Optional per-tick logic
        int interval = activeRitual.tickInterval();
        if (interval > 0 && ritualTicks % interval == 0 && ritualTicks >= 0) {
            onRitualTick(serverWorld);
        }

        // Finish ritual
        if (ritualTicks >= activeRitual.duration()) {
            clearRitual();
        }
    }

    private void onRitualTick(ServerWorld world) {
        Ritual running = MagicRegistry.getRitual(activeRitual.ritualId());
        running.ritualCast(world, getRedstoneOrClosestPlayer(world), this, activeRitual.data());

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

    public ServerPlayerEntity getRedstoneOrClosestPlayer(ServerWorld world){
        BlockPos pos = this.getPos();
        ServerPlayerEntity entity = world.getServer().getPlayerManager().getPlayer(redstonePlayer);
        if (entity != null){
            return entity;
        }
        return (ServerPlayerEntity) world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64, false);

    }


    private void clearRitual() {
        activeRitual = null;
        ritualTicks = 0;
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt){
        NbtCompound compound = new NbtCompound();
        compound.putInt("ritualTicks", ritualTicks);
        compound.putInt("tier", stockpiledTier.ordinal());
        List<Integer> notesForNbt = new ArrayList<>();
        if (activeRitual != null){
            for (Note note: activeRitual.pattern().notes()){
                notesForNbt.add(note.ordinal());
            }
        } else {
            notesForNbt = List.of();
        }

        compound.putIntArray("notes", notesForNbt);


    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        ritualTicks = nbt.getInt("ritualTicks");
        stockpiledTier = Tier.values()[nbt.getInt("tier")];
        int[] noteOrdinals = nbt.getIntArray("notes");
        List<Note> notes = new ArrayList<>();
        for (int ordinal: noteOrdinals){
            notes.add(Note.values()[ordinal]);
        }

        if (notes.isEmpty()) return;

        activeRitual = RitualReloadListener.matchForRitual(notes);


    }


}
