package aster.songweaver.registry.status;

import aster.songweaver.cca.AmberComponent;
import aster.songweaver.cca.SongweaverComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class AmberStepsEffect extends StatusEffect {

    public AmberStepsEffect(){
        super(
                StatusEffectCategory.NEUTRAL,
                0xeb9c2f
        );

    }



    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {

        if (entity.getWorld().isClient) return;

        AmberComponent comp = SongweaverComponents.AMBER_STEPS.get(entity);

        comp.setOrigin((ServerWorld) entity.getWorld(), entity.getBlockPos());

    }


    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {

        if (entity.getWorld().isClient) return;

        AmberComponent comp = SongweaverComponents.AMBER_STEPS.get(entity);

        if (!comp.hasOrigin()) return;

        BlockPos pos = comp.getOrigin();
        ServerWorld world = entity.getServer().getWorld(comp.getWorldKey());

        if (world == null) return;

        entity.teleport(
                world,
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                Set.of(),
                entity.getYaw(),
                entity.getPitch()
        );

        comp.clear();
    }


}
