package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class UnravelingStateComponent implements AutoSyncedComponent {
    public boolean clearable = false;
    public final LivingEntity entity;
    public UnravelingStateComponent(LivingEntity entity){
        this.entity = entity;
    }

    public void setClearable(boolean yesorno){
        this.clearable = yesorno;
        SongweaverComponents.UNRAVELING_STATE.sync(entity);
    }

    public boolean isClearable(){
        return clearable;
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        clearable = nbtCompound.getBoolean("unraveling_state");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putBoolean("unraveling_state", clearable);
    }
}
