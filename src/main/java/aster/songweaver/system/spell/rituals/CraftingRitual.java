package aster.songweaver.system.spell.rituals;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CraftingRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, RitualControllerBlockEntity loom, @Nullable JsonObject data) {
        if (data == null){
            caster.sendMessage(
                    Text.literal("crafting ritual requires data."),
                    true
            );
            return;
        }

        int count = getCountOrDefaultOne(data);

        if (!data.has("item")){
            caster.sendMessage(
                    Text.literal("Crafting ritual requires item."),
                    true
            );
            return;
        }


        Identifier id = new Identifier(data.get("item").getAsString());
        Item item = Registries.ITEM.get(id);
        ItemStack stack = new ItemStack(item, count);
        BlockPos loompos = loom.getPos();

        ItemEntity output = new ItemEntity(world, loompos.getX() + 0.5, loompos.getY() + 2, loompos.getZ()+0.5, stack );

        world.spawnEntity(output);
        world.spawnParticles(
                ParticleTypes.END_ROD,
                loompos.getX() + 0.5,
                loompos.getY() + 1.2,
                loompos.getZ() + 0.5,
                6,
                0.4, 0.2, 0.4,
                0.01
        );










    }



    public int getCountOrDefaultOne(JsonObject json) {
        // Return the "count" field, or 1 if it's missing
        return json.has("count") ? json.get("count").getAsInt() : 1;
    }


}
