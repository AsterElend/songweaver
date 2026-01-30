package aster.songweaver.system.spell.drafts;

import aster.songweaver.cca.HaloComponent;
import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Draft;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HaloDraft implements Draft {

    @Override
    public void cast(ServerWorld world, ServerPlayerEntity caster, @Nullable JsonObject data) {

        if (data == null || !data.has("op")) {
            SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
            return;
        }

        String op = data.get("op").getAsString();

        HaloComponent halo = SongweaverComponents.HALO.get(caster);


        switch (op) {

            case "push": {
                ItemStack offhand = caster.getOffHandStack();
                if (offhand.isEmpty() || !(offhand.getItem() instanceof BlockItem)) {
                    SongServerCasting.sendFeedback(caster, CastFeedback.WRONG_INPUT);
                    break;
                }

                halo.push(offhand);
                // NOTE: do NOT decrement offhand, halo is only a reference
                break;
            }

            case "pop": {
                ItemStack offhand = caster.getOffHandStack();
                Item preferred = offhand.isEmpty() ? null : offhand.getItem();

                halo.pop(preferred);
                break;
            }

            case "place": {
                DefaultedList<ItemStack> haloStacks = halo.getStacks();

                // Collect non-empty halo refs
                List<ItemStack> refs = new ArrayList<>();
                for (ItemStack s : haloStacks) {
                    if (!s.isEmpty()) {
                        refs.add(s);
                    }
                }

                if (refs.isEmpty()) {
                    SongServerCasting.sendFeedback(caster, CastFeedback.EMPTY_HALO);
                    break;
                }

                // Pick random halo reference
                ItemStack haloRef = refs.get(world.random.nextInt(refs.size()));
                Item item = haloRef.getItem();

                // Find matching inventory stack
                PlayerInventory inv = caster.getInventory();
                int slot = -1;
                for (int i = 0; i < inv.size(); i++) {
                    ItemStack stack = inv.getStack(i);
                    if (!stack.isEmpty() && stack.isOf(item)) {
                        slot = i;
                        break;
                    }
                }

                if (slot < 0) {
                    SongServerCasting.sendFeedback(caster, CastFeedback.NO_COMPONENTS);
                    break;
                }

                ItemStack invStack = inv.getStack(slot);
                ItemStack placeStack = invStack.copyWithCount(1);

                BlockHitResult hit = raycastPlacement(caster);
                if (hit == null) break;

                ItemPlacementContext ctx =
                        new ItemPlacementContext(caster, Hand.MAIN_HAND, placeStack, hit);

                ActionResult result =
                        ((BlockItem) item).place(ctx);

                if (result.isAccepted() && !caster.isCreative()) {
                    invStack.decrement(1);
                }

                break;
            }

            case "purge": {
                halo.purge();
                break;
            }

            default: {
                SongServerCasting.sendFeedback(caster, CastFeedback.MALFORMED_JSON);
                break;
            }
        }
    }


        private static BlockHitResult raycastPlacement(
            ServerPlayerEntity player

    ) {
        return (BlockHitResult) player.raycast(
                5.0,
                0.0f,
                false
        );
    }

}
