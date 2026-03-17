package aster.songweaver.registry.status;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LightfootEffect extends StatusEffect {
    public LightfootEffect(){
        super(
        StatusEffectCategory.BENEFICIAL,
        0xa7f4fc);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
