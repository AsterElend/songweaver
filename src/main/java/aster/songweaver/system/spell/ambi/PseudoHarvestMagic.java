package aster.songweaver.system.spell.ambi;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.util.ParticleHelper;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class PseudoHarvestMagic implements Draft, Ritual {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        if (data == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        Set<EntityType<?>> allowedTargets = SpellUtil.getLegalTargets(data);
        LivingEntity target = SpellUtil.resolveTarget(caster, 16, false);

        if (target == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.NO_TARGET);
            return;
        }

        if (target == caster){
            return;
        }

        if (!allowedTargets.contains(target.getType()) || target.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)){
            SongServerCasting.sendFeedback(caster, CastFeedback.WRONG_ENTITY);
            return;
        }



    finalExecution(world, target, data, caster);




    }



    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster,
                           GrandLoomBlockEntity loom, @Nullable JsonObject data) {

      if (data == null){
          SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
          return;
      }

        Set<EntityType<?>> allowedTargets = SpellUtil.getLegalTargets(data);

        List<LivingEntity> targets =
                SpellUtil.radiusGetTargets(world, loom.getPos(), 16);

        if (targets == null || targets.isEmpty()) {
            SongServerCasting.sendFeedback(caster, CastFeedback.NO_TARGET);
            return;
        }

        for (LivingEntity target : targets) {
            if (allowedTargets.contains(target.getType())
                    && !target.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)) {

                finalExecution(world, target, data, caster);
            }
        }
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

   public static void finalExecution(ServerWorld world, LivingEntity target, @Nullable JsonObject data, ServerPlayerEntity caster){
       spawnEntityDrops(world, target);

       if (data == null){
           SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
           return;
       }
       int silenceTicks = data.get("cooldown").getAsInt();

       StatusEffectInstance appliedSilence = new StatusEffectInstance(LoomMiscRegistry.SONG_SILENCE, silenceTicks);

       target.addStatusEffect(appliedSilence);

       ParticleHelper.spawnParticleBurst(world, target.getBlockPos(), ParticleTypes.SCRAPE, 10, 0.3F);
   }


}
