package aster.songweaver.system.spell.drafts;

import aster.songweaver.registry.LoomMiscRegistry;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PseudoHarvestDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        EntityType<?> allowedTarget = Registries.ENTITY_TYPE.get(new Identifier(data.get("entity").getAsString()));
        LivingEntity target = DraftUtil.resolveTarget(caster, 16, false);

        if (target.getType() != allowedTarget){
            SongServerCasting.sendFailure(caster, CastFailure.WRONG_ENTITY);
            return;
        }

        if (target.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)){
            SongServerCasting.sendFailure(caster, CastFailure.WRONG_ENTITY);
            return;
        }




        spawnEntityDrops(world, target);

        int silenceTicks = data.get("cooldown").getAsInt();

        StatusEffectInstance appliedSilence = new StatusEffectInstance(LoomMiscRegistry.SONG_SILENCE, silenceTicks);



        target.addStatusEffect(appliedSilence);







    }


    public static void spawnEntityDrops(ServerWorld world, LivingEntity entity) {
        LootTable lootTable = world.getServer()
                .getLootManager()
                .getLootTable(entity.getLootTable());

        LootContextParameterSet.Builder builder =
                new LootContextParameterSet.Builder(world)
                        .add(LootContextParameters.THIS_ENTITY, entity)
                        .add(LootContextParameters.ORIGIN, entity.getPos())
                        .add(LootContextParameters.DAMAGE_SOURCE,
                                world.getDamageSources().generic())
                        .addOptional(LootContextParameters.KILLER_ENTITY, null)
                        .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, null);

        LootContextParameterSet context =
                builder.build(LootContextTypes.ENTITY);

        lootTable.generateLoot(context, stack -> {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    stack
            );
            world.spawnEntity(itemEntity);
        });
    }

}
