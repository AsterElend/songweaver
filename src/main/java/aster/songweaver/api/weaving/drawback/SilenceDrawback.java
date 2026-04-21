package aster.songweaver.api.weaving.drawback;

import aster.songweaver.api.weaving.Drawback;
import aster.songweaver.cca.SongweaverComponents;
import net.minecraft.server.network.ServerPlayerEntity;

public class SilenceDrawback implements Drawback {
    private final int ticks;

    public SilenceDrawback(int ticks){
        this.ticks = ticks;
    }

    @Override
    public void apply(ServerPlayerEntity caster){
        SongweaverComponents.SILENCE.get(caster).setSilence(ticks);
    }
}
