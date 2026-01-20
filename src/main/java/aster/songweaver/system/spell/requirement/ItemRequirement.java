package aster.songweaver.system.spell.requirement;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Requirement;
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
    public CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller) {
        int found = 0;

        for (ItemStack stack : caster.getInventory().main) {
            if (stack.isOf(item)) {
                found += stack.getCount();
                if (found >= count) return null;
            }
        }

        return CastFailure.NO_COMPONENTS;
    }

}
