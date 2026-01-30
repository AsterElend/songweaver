package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ProjectileDraft implements Draft {

        @Override
        public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

            if (data == null || !data.has("projectile")) {
                SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
                return;
            }

            Identifier attemptProjectile = new Identifier(data.get("projectile").getAsString());
            EntityType<?> projectileType = Registries.ENTITY_TYPE.get(attemptProjectile);

            Entity entity = projectileType.create(world);

            if (!(entity instanceof ProjectileEntity toSummon)) {
                SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
                return;
            }

            // Spawn at caster's eye position
            toSummon.updatePosition(caster.getX(), caster.getEyeY(), caster.getZ());

            // Speed from JSON
            float speed = data.has("speed") ? data.get("speed").getAsFloat() : 1.0f;
            Vec3d look = caster.getRotationVec(1.0f);
            toSummon.setVelocity(look.x , look.y , look.z, speed, 0.0F);
            toSummon.velocityModified = true;
            // Set owner and optional pickup
            toSummon.setOwner(caster);


            if (toSummon instanceof PersistentProjectileEntity p) {
                if (data.has("canPickUp")) {
                    boolean canPickUp = data.get("canPickUp").getAsBoolean();
                    p.pickupType = canPickUp ? PersistentProjectileEntity.PickupPermission.ALLOWED
                            : PersistentProjectileEntity.PickupPermission.DISALLOWED;
                }
            }

            world.spawnEntity(toSummon);
        }
    }


