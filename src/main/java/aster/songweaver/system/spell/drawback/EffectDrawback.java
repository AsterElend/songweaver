package aster.songweaver.system.spell.drawback;

import aster.songweaver.Songweaver;
import aster.songweaver.system.spell.definition.Drawback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectDrawback implements Drawback {

    private final Identifier effectId;
    private final int duration;
    private final int amplifier;

    public EffectDrawback(
            Identifier effectId,
            int duration,
            int amplifier

    ) {
        this.effectId = effectId;
        this.duration = duration;
        this.amplifier = amplifier;
    }


    @Override
    public void apply(ServerPlayerEntity caster) {
        StatusEffect effect = caster.getServerWorld()
                .getRegistryManager()
                .get(RegistryKeys.STATUS_EFFECT)
                .get(effectId);

        if (effect == null) {
            Songweaver.LOGGER.warn(
                    "Unknown effect in drawback: {}",
                    effectId
            );
            return;
        }

        caster.addStatusEffect(new StatusEffectInstance(
                effect,
                duration,
                amplifier
        ));
    }
}

