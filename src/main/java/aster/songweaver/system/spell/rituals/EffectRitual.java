package aster.songweaver.system.spell.rituals;

import aster.songweaver.system.ParticleHelper;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.system.spell.drafts.DraftUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EffectRitual implements Ritual {
    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, RitualControllerBlockEntity loom, @Nullable JsonObject data) {

        if (data == null){
            caster.sendMessage(
                    Text.literal("Please fix the json."),
                    true
            );
        }


        String effectName = data.get("effect").getAsString();
        StatusEffect effect = world.getRegistryManager()
                .get(RegistryKeys.STATUS_EFFECT)
                .get(new Identifier(effectName));

        if (effect == null) {
            caster.sendMessage(
                    Text.literal("Unknown status effect: " + effectName),
                    true
            );
            return;
        }

        // Read optional parameters
        int duration = data.get("duration").getAsInt();
        int amplifier = data.has("amplifier") ? data.get("amplifier").getAsInt() : 0;
        boolean ambient = data.has("ambient") && data.get("ambient").getAsBoolean();
        boolean showParticles = !data.has("show_particles") || data.get("show_particles").getAsBoolean();
        boolean showIcon = !data.has("show_icon") || data.get("show_icon").getAsBoolean();
        int radius = data.get("radius").getAsInt();

        StatusEffectInstance instance = new StatusEffectInstance(
                effect, duration, amplifier, ambient, showParticles, showIcon
        );

       List<LivingEntity> targets =  DraftUtil.radiusGetTargets(world, loom.getPos(), radius);
       if (targets == null){
           SongServerCasting.sendFailure(caster, CastFailure.NO_TARGET);
           return;
       }

       for (LivingEntity entity: targets){
           entity.addStatusEffect(instance);
       }



        ParticleHelper.spawnParticleWave(world, loom.getPos(), ParticleTypes.INSTANT_EFFECT, radius, 10);




    }
}
