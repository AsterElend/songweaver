package aster.songweaver.system.spell.drafts;

import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GiveItemDraft implements Draft {
    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        if (data == null) {
            caster.sendMessage(
                    Text.literal("Give spell requires data."),
                    true
            );
            return;
        }

        Identifier id = new Identifier(data.get("item").getAsString());
        Item item = Registries.ITEM.get(id);
        int count = data.has("count") ? data.get("count").getAsInt() : 1;
        ItemStack give = new ItemStack(item, count);

        caster.getInventory().insertStack(give);



    }
}
