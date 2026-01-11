package aster.songweaver.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class LoomTags {

    public static final TagKey<Item> DISTAFFS =
            TagKey.of(
                    RegistryKeys.ITEM,
                    new Identifier("songweaver", "distaffs")
            );



}
