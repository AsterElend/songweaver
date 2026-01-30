package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import static aster.songweaver.system.cast.SongServerCasting.sendFeedback;

public class RepairDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        ItemStack tool = caster.getOffHandStack();
        PlayerInventory inv = caster.getInventory();

        int repairSlot = -1;
        ItemStack repairStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty() && tool.getItem().canRepair(tool, stack)) {
                repairSlot = i;
                repairStack = stack;
                break;
            }
        }

        if (repairSlot == -1) {
            sendFeedback(caster, CastFeedback.NO_COMPONENTS);
            return;
        }


        int maxDurability = tool.getMaxDamage();
        int repairPerItem = maxDurability / 4;

// how much damage we can fix
        int damage = tool.getDamage();
        int itemsNeeded = (int) Math.ceil((double) damage / repairPerItem);

// consume at most what's available
        int itemsToConsume = Math.min(itemsNeeded, repairStack.getCount());

        tool.setDamage(Math.max(0, damage - itemsToConsume * repairPerItem));
        repairStack.decrement(itemsToConsume);
    }
}
