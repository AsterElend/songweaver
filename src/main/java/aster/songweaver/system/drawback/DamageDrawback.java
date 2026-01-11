package aster.songweaver.system.drawback;

import aster.songweaver.system.definition.Drawback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class DamageDrawback implements Drawback {

    private final float percent;

    public DamageDrawback(float percent) {
        this.percent = percent;
    }

    @Override
    public void apply(ServerPlayerEntity caster) {
        float maxHealth = caster.getMaxHealth();
        float amount = maxHealth * percent;

        DamageSource source = new DamageSource(
                caster.getServerWorld()
                        .getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(DamageTypes.MAGIC)
        );

        caster.damage(source, amount);
    }
}
