package aster.songweaver.registry;

import net.minecraft.block.Block;
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
    public static final TagKey<Block> POLISHED_STONE =
            TagKey.of(
                    RegistryKeys.BLOCK,
                    new Identifier("songweaver", "polished_stone")
            );

public static final TagKey<Block> TERRAFORM_WHITELIST =
            TagKey.of(
                    RegistryKeys.BLOCK,
                    new Identifier("songweaver", "terraform_whitelist")
            );



}
