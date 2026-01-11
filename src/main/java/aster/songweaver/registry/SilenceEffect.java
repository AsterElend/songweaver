package aster.songweaver.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SilenceEffect extends StatusEffect {
    public SilenceEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x5A5A5A // muted gray
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
