package aster.songweaver.registry.status;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class UnravelingEffect extends StatusEffect {

    public static final int BASE_DURATION = 20 * 60 * 3; // 3 minutes

    public UnravelingEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7a3db8);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        if (entity.getWorld().isClient) return;

        int interval = Math.max(40 - amplifier * 5, 10);

        if (entity.age % interval == 0) {
            float damage = 1.0f + amplifier * 0.75f;

            entity.damage(LoomMiscRegistry.fray(entity.getWorld()), damage);
        }



    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {

        if (entity.isDead() || SongweaverComponents.UNRAVELING_STATE.get(entity).isClearable()) return;

        entity.addStatusEffect(new StatusEffectInstance(
                LoomMiscRegistry.UNRAVELING,
                BASE_DURATION,
                amplifier + 1,
                true,
                true,
                true
        ));
    }


}
