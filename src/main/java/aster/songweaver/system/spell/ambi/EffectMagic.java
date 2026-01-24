package aster.songweaver.system.spell.ambi;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.util.ParticleHelper;
import aster.songweaver.util.SpellUtil;
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
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static aster.songweaver.util.SpellUtil.resolveTarget;

public class EffectMagic implements Ritual, Draft{
    private static final double RANGE = 16.0;

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        if (data == null) {
            caster.sendMessage(
                    Text.literal("Effect spell requires data."),
                    true
            );
            return;
        }

        // Resolve StatusEffect from string
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

        StatusEffectInstance instance = new StatusEffectInstance(
                effect, duration, amplifier, ambient, showParticles, showIcon
        );

        // Determine target
        LivingEntity target = resolveTarget(caster, RANGE, true);
        if (target == null) {
            caster.sendMessage(
                    Text.literal("No valid target for effect."),
                    true
            );
            return;
        }

        target.addStatusEffect(instance);
    }

    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {

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

        List<LivingEntity> targets =  SpellUtil.radiusGetTargets(world, loom.getPos(), radius);

        BlockPos executionPos = SpellUtil.getKhipuPosOrLoomPosIfAbsent(loom);

        effectCloud(world, executionPos, 16, targets, instance, caster);

    }

    public void effectCloud(ServerWorld world, BlockPos pos, int radius, List<LivingEntity> targets, StatusEffectInstance instance, ServerPlayerEntity caster){


        if (targets == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.NO_TARGET);
            return;
        }

        for (LivingEntity entity: targets){
            entity.addStatusEffect(instance);
        }



        ParticleHelper.spawnParticleWave(world, pos, ParticleTypes.INSTANT_EFFECT, radius, 10);
    }
}
