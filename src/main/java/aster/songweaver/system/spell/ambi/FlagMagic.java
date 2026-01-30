package aster.songweaver.system.spell.ambi;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.util.SpellUtil;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FlagMagic implements Draft, Ritual {

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        if (data == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        boolean allowCaster = data.has("allow_caster") && data.get("allow_caster").getAsBoolean();
        String flag = data.get("flag").getAsString();

        if (flag == null) {
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }





        SpellUtil.resolveTarget(caster, 16, allowCaster);



        Set<EntityType<?>> allowedTargets = SpellUtil.getLegalTargets(data);
        LivingEntity target = SpellUtil.resolveTarget(caster, 16, false);

        if (target == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.NO_TARGET);
            return;
        }

        if (!allowedTargets.contains(target.getType())){
            SongServerCasting.sendFeedback(caster, CastFeedback.WRONG_ENTITY);
            return;
        }



        if (!toggleFlag(flag, target)) {
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
        }





    }

    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, GrandLoomBlockEntity loom, @Nullable JsonObject data) {
        if (data == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        boolean allowCaster = data.has("allow_caster") && data.get("allow_caster").getAsBoolean();
        String flag = data.get("flag").getAsString();


        Set<EntityType<?>> allowedTargets = SpellUtil.getLegalTargets(data);
        List<LivingEntity> targets = SpellUtil.radiusGetTargets(world, SpellUtil.getKhipuPosOrLoomPosIfAbsent(loom), 16);

        if (targets == null){
            SongServerCasting.sendFeedback(caster, CastFeedback.NO_TARGET);
            return;
        }

        for (LivingEntity target: targets){

            if (!allowedTargets.contains(target.getType())){
                SongServerCasting.sendFeedback(caster, CastFeedback.WRONG_ENTITY);
                return;
            }

            if (!allowCaster && target == caster){
                return;
            }


            if (flag == null) {
                SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
                return;
            }

            toggleFlag(flag, target);
        }






    }

    public static boolean toggleFlag(String flag, LivingEntity entity) {
        switch (flag.toLowerCase(Locale.ROOT)) {

            case "silent" -> {
                entity.setSilent(!entity.isSilent());
                return true;
            }

            case "no_gravity", "nogravity", "gravity" -> {
                entity.setNoGravity(!entity.hasNoGravity());
                return true;
            }

            case "invulnerable" -> {
                entity.setInvulnerable(!entity.isInvulnerable());
                return true;
            }

            case "glowing" -> {
                entity.setGlowing(!entity.isGlowing());
                return true;
            }

            case "invisible" -> {
                entity.setInvisible(!entity.isInvisible());
                return true;
            }

            default -> {
                return false; // unknown flag
            }
        }
    }



}
