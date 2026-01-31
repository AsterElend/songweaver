package aster.songweaver.datagen;

import aster.songweaver.registry.physical.LoomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, LoomItems.DISTAFF_BASIC)
                .pattern("  A").pattern( " AB").pattern("A  ").input('A', Items.STICK).input('B', Blocks.NOTE_BLOCK)
                .criterion(FabricRecipeProvider.hasItem(Blocks.NOTE_BLOCK), FabricRecipeProvider.conditionsFromItem(Blocks.NOTE_BLOCK)).offerTo(consumer);
        applyDistaffUpgrade(LoomItems.DISTAFF_BASIC, Items.IRON_INGOT, LoomItems.DISTAFF_IRON, consumer);
        applyDistaffUpgrade(LoomItems.DISTAFF_BASIC, Items.DIAMOND, LoomItems.DISTAFF_DIAMOND, consumer);

        SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItems(LoomItems.DISTAFF_DIAMOND),
                Ingredient.ofItems(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, LoomItems.DISTAFF_NETHERITE).criterion(FabricRecipeProvider.hasItem(LoomItems.DISTAFF_DIAMOND),
                FabricRecipeProvider.conditionsFromItem(LoomItems.DISTAFF_DIAMOND)).offerTo(consumer, new Identifier("songweaver:distaff_netherite_upgrade"));

        SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItems(Items.AIR), Ingredient.ofItems(LoomItems.DISTAFF_NETHERITE),
                Ingredient.ofItems(Items.NETHER_STAR), RecipeCategory.TOOLS, LoomItems.DISTAFF_ASTRAL).criterion(FabricRecipeProvider.hasItem(LoomItems.DISTAFF_NETHERITE),
                FabricRecipeProvider.conditionsFromItem(LoomItems.DISTAFF_NETHERITE)).offerTo(consumer, new Identifier("songweaver:distaff_astral_upgrade"));
    }

    public static void applyDistaffUpgrade(ItemConvertible input, ItemConvertible surround, ItemConvertible output, Consumer<RecipeJsonProvider> exporter){
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, output).pattern("AAA").pattern("ABA").pattern("AAA")
                .input('A', surround).input('B', input)
                .criterion(FabricRecipeProvider.hasItem(input), FabricRecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }
}
