package aster.songweaver.system.spell.drawback;

import aster.songweaver.registry.LoomMiscRegistry;
import aster.songweaver.system.spell.definition.Drawback;
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
       caster.damage(LoomMiscRegistry.fray(caster.getWorld()), amount);
    }
}
