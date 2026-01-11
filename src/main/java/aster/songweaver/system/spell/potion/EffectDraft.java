package aster.songweaver.system.spell.potion;

import aster.songweaver.system.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static aster.songweaver.system.spell.DraftUtil.resolveTarget;

public class EffectDraft implements Draft {

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
        LivingEntity target = resolveTarget(caster, RANGE);
        if (target == null) {
            caster.sendMessage(
                    Text.literal("No valid target for effect."),
                    true
            );
            return;
        }

        target.addStatusEffect(instance);
    }


}