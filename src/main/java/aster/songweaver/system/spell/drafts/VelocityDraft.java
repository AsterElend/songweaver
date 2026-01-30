package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class VelocityDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        double speed = caster.getVelocity().length();
        caster.setVelocity(0,0,0);
        caster.velocityModified = true;
        Vec3d direction = caster.getRotationVector();
        Vec3d appliedVec = direction.multiply(speed);

        caster.setVelocity(appliedVec);
        caster.velocityModified = true;
    }
}
