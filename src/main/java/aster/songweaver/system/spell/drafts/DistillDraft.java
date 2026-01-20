package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DistillDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {
        LivingEntity target = DraftUtil.resolveTarget(caster, 16.0, false );

        if (target == null) {
            caster.sendMessage(
                    Text.literal("No target to distill."),
                    true
            );
            return;
        }

        Collection<StatusEffectInstance> effects = target.getStatusEffects();

        if (effects.isEmpty()) {
            caster.sendMessage(
                    Text.literal("The song finds nothing to draw forth."),
                    true
            );
            return;
        }
        ItemStack potion = createPotion(effects);

        target.clearStatusEffects();

        givePotion(caster, potion);

    }
    private ItemStack createPotion(
            Collection<StatusEffectInstance> effects) {

        ItemStack stack =
                new ItemStack(Items.SPLASH_POTION);

        NbtList list = new NbtList();

        for (StatusEffectInstance effect : effects) {
            list.add(effect.writeNbt(new NbtCompound()));
        }

        stack.getOrCreateNbt()
                .put("CustomPotionEffects", list);

        return stack;
    }

    private void givePotion(ServerPlayerEntity caster,
                            ItemStack stack) {

        if (!caster.getInventory().insertStack(stack)) {
            caster.dropItem(stack, false);
        }
    }



}
