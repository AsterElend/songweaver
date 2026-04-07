package aster.songweaver.cca;

import aster.songweaver.api.NadirToast;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class ForgottenAdvancementComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private final Set<Identifier> forgotten = new HashSet<>();

    public ForgottenAdvancementComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean isForgotten(Identifier id) {
        return forgotten.contains(id);
    }

    public Set<Identifier> getAll() {
        return forgotten;
    }

    public void forget(Identifier id) {
        forgotten.add(id);
        SongweaverComponents.FORGOTTEN.sync(player);
        NadirToast.sendPacket(id, true);
    }

    public void remember(Identifier id) {
        forgotten.remove(id);
        SongweaverComponents.FORGOTTEN.sync(player);
        NadirToast.sendPacket(id, false);
    }

    public void rememberRandom(ServerPlayerEntity player){
       Optional<Identifier> chosen = forgotten.stream().skip(player.getRandom().nextInt(forgotten.size())).findFirst();
        chosen.ifPresent(this::remember);
    }



    public void forgetRandom(ServerPlayerEntity player) {
        PlayerAdvancementTracker tracker = player.getAdvancementTracker();

        List<Advancement> completed = new ArrayList<>();

        for (Advancement adv : player.server.getAdvancementLoader().getAdvancements()) {
            if (tracker.getProgress(adv).isDone() && !isForgotten(adv.getId())) {
                completed.add(adv);
            }
        }

        if (completed.isEmpty()) return;

        Advancement chosen = completed.get(player.getRandom().nextInt(completed.size()));
        forget(chosen.getId());

    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        forgotten.clear();
        NbtList list = tag.getList("Forgotten", NbtElement.STRING_TYPE);

        for (int i = 0; i < list.size(); i++) {
            forgotten.add(new Identifier(list.getString(i)));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();

        for (Identifier id : forgotten) {
            list.add(NbtString.of(id.toString()));
        }

        tag.put("Forgotten", list);
    }


}
