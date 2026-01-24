package aster.songweaver.system.spell.rituals;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
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

        BlockPos khipuPos = SpellUtil.getKhipuPosOrLoomPosIfAbsent(loom);

        if (khipuPos != null){
            summonTheItem(world, stack, khipuPos);
        } else {
            summonTheItem(world, stack, loom.getPos());
        }













    }



    public int getCountOrDefaultOne(JsonObject json) {
        // Return the "count" field, or 1 if it's missing
        return json.has("count") ? json.get("count").getAsInt() : 1;
    }

    public void summonTheItem(ServerWorld world, ItemStack summon, BlockPos pos){
        ItemEntity output = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 2, pos.getZ()+0.5, summon );

        world.spawnEntity(output);
        world.spawnParticles(
                ParticleTypes.END_ROD,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                6,
                0.4, 0.2, 0.4,
                0.01
        );
    }


}
