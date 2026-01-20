package aster.songweaver.system.spell.requirement;

import aster.songweaver.registry.LoomItems;
import aster.songweaver.registry.LoomTags;
import aster.songweaver.system.spell.definition.Tier;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;

public class TierCheckHelper {

    public static Tier getHeldTier(ServerPlayerEntity player) {
        Item item = player.getMainHandStack().getItem();

        if (!player.getMainHandStack().isIn(LoomTags.DISTAFFS)){return null;}

        if (item == LoomItems.DISTAFF_BASIC) return Tier.WOOD;
        if (item == LoomItems.DISTAFF_IRON) return Tier.IRON;
        if (item == LoomItems.DISTAFF_DIAMOND) return Tier.DIAMOND;
        if (item == LoomItems.DISTAFF_NETHERITE) return Tier.NETHERITE;
        if (item == LoomItems.DISTAFF_ASTRAL) return Tier.ASTRAL;
        if (item == LoomItems.SPINDLE) return Tier.IRON;

        return null;
    }
}
