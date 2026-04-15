package aster.songweaver.mixin;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.LoomTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public abstract class PlayerLetheanCheck {
    @Inject(method = "tick", at = @At("TAIL"))
    void forgettingTick(CallbackInfo ci){

       PlayerEntity ambiPlayer = (PlayerEntity) (Object) this;
        if (ambiPlayer.getWorld().isClient){
            return;
        }

        ServerPlayerEntity tickingPlayer = (ServerPlayerEntity) ambiPlayer;
        boolean isInTheRiverOfForgottenThings = (tickingPlayer.isSubmergedIn(LoomTags.LETHE));
        int ageSlice = tickingPlayer.age % 100;
        if (isInTheRiverOfForgottenThings && ageSlice == 0){
            SongweaverComponents.FORGOTTEN.get(tickingPlayer).forgetRandom(tickingPlayer);
        }

        Random random = Random.create();
        if (ageSlice == random.nextInt(100) && SongweaverComponents.FORGOTTEN.get(tickingPlayer).isAnyForgotten()){
            SongweaverComponents.FORGOTTEN.get(tickingPlayer).rememberRandom(tickingPlayer);
        }
    }


}
