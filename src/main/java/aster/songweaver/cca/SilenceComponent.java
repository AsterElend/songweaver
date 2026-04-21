package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class SilenceComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final LivingEntity entity;
    private int silenceDuration = 0;
    public SilenceComponent(LivingEntity entity){
        this.entity = entity;
    }

    public boolean isSilenced(){
        return (silenceDuration > 0);
    }

    public void clearSilence(){
        silenceDuration = 0;
        sync();
    }

    public void setSilence(int duration){
        silenceDuration =duration;
        sync();
    }

    public int getSilenceDuration(){
        return silenceDuration;
    }



    public void readFromNbt(NbtCompound nbt){
        silenceDuration = nbt.getInt("silenceDuration");
    }
    public void writeToNbt(NbtCompound nbt){
        nbt.putInt("silenceDuration", silenceDuration);
    }

    public void sync(){
        SongweaverComponents.SILENCE.sync(entity);
    }

    @Override
    public void serverTick() {
        if (silenceDuration > 0) {
            silenceDuration--;
            if ( silenceDuration == 0 || silenceDuration % 20 == 0 ) {
                sync();
            }
        }
    }
}
