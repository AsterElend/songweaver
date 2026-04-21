package aster.songweaver.api.weaving.drawback;

import aster.songweaver.api.weaving.Drawback;
import aster.songweaver.registry.physical.LoomMiscRegistry;
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
