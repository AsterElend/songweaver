package aster.songweaver.system.drawback;

import aster.songweaver.system.definition.Drawback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConsumeItemDrawback implements Drawback {
    private final Item item;
    private final int count;

    public ConsumeItemDrawback(Identifier itemId, int count) {
        this.item = Registries.ITEM.get(itemId);
        this.count = count;
    }

    @Override
    public void apply(ServerPlayerEntity caster) {
        int remaining = count;

        for (int i = 0; i < caster.getInventory().size() && remaining > 0; i++) {
            ItemStack stack = caster.getInventory().getStack(i);

            if (stack.isOf(item)) {
                int taken = Math.min(stack.getCount(), remaining);
                stack.decrement(taken);
                remaining -= taken;
            }
        }
    }
}
