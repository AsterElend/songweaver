package aster.songweaver.system.spell.rituals;

import aster.songweaver.util.ParticleHelper;
import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SummoningRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
        if (data == null) {
            caster.sendMessage(
                    Text.literal("Summoning ritual requires data."),
                    true
            );
            return;
        }

        BlockPos khipuPos = SpellUtil.getKhipuPosOrLoomPosIfAbsent(loom);
        String entity = data.get("entity").getAsString();


        if (khipuPos != null){
            getSummonPosAndThenSummon(world, entity, khipuPos);
        } else {
            getSummonPosAndThenSummon(world, entity, loom.getPos());
        }



    }


    public static void getSummonPosAndThenSummon(ServerWorld world, String entity, BlockPos pos){
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0; // just above the block
        double z = pos.getZ() + 0.5;

        Vec3d summonPos = new Vec3d(x, y, z);

        spawnEntityFromString(world, entity, summonPos);
        ParticleHelper.spawnParticleBurst(world, pos, ParticleTypes.SOUL, 100, 3);

    }



    public static void spawnEntityFromString(ServerWorld world, String entityId, Vec3d pos) {
        Identifier id = new Identifier(entityId);
        EntityType<?> type = Registries.ENTITY_TYPE.get(id);

        if (type == null) {
            System.out.println("Unknown entity type: " + entityId);
            return;
        }

        Entity entity = type.create(world);
        if (entity == null) {
            System.out.println("Failed to create entity: " + entityId);
            return;
        }

        entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0f, 0f);
        world.spawnEntity(entity);
    }

}
