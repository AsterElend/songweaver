package aster.songweaver.api.spell.requirement;

import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Requirement;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemRequirement implements Requirement {
    private final Item item;
    private final int count;

    public ItemRequirement(Identifier itemId, int count) {
        this.item = Registries.ITEM.get(itemId);
        this.count = count;
    }

    @Override
    public CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual) {
        int found = 0;

        for (ItemStack stack : caster.getInventory().main) {
            if (stack.isOf(item)) {
                found += stack.getCount();
                if (found >= count) return null;
            }
        }

        return CastFeedback.NO_COMPONENTS;
    }

}
