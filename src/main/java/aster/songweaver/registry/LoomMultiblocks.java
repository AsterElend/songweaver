package aster.songweaver.registry;

import aster.songweaver.Songweaver;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;

import java.util.function.Predicate;


public class LoomMultiblocks {
    public static IMultiblock LOOM_HALL;
    public static String[][] LOOM_PATTERN = new String[][] {

        {
            "       ",
                    "       ",
                    "       ",
                    "       ",
                    " PPGPP ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       "
        },


        {
            "       ",
                    "       ",
                    "       ",
                    " PPPPP ",
                    " L   L ",
                    " PPPPP ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " PPGPP ",
                    " PPGPP ",
                    "       ",
                    "       "
        },

        {
            "       ",
                    "       ",
                    " PPGPP ",
                    " L   L ",
                    " L   L ",
                    " L   L ",
                    " PPGPP ",
                    " PPLPP ",
                    " PPLPP ",
                    " PPLPP ",
                    " PPPPP ",
                    " L   L ",
                    " L   L ",
                    " PPPPP ",
                    "       "



        },

        {
            "PPPPPPP",
                    "PPPPPPP",
                    "PP   PP",
                    "PL   LP",
                    "PP   PP",
                    "PP   PP",
                    "PP   PP",
                    "PP   PP",
                    "PL   LP",
                    "PP   PP",
                    "PP   PP",
                    "PP   PP",
                    "PP   PP",
                    "PL   LP",
                    "       "
        },

        {
            " PPPPP ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       "


        },

        {
            " LLLLL ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       "
        },



        {
            " LLGLL ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       ",
                    "       ",
                    "       ",
                    "       ",
                    " L   L ",
                    "       "
        },


        {
            " LLLLL ",
                    " P   P ",
                    " P S P ",
                    " L   L ",
                    " P   P ",
                    " P   P ",
                    " P   P ",
                    " P   P ",
                    " L   L ",
                    " P   P ",
                    " P   P ",
                    " P   P ",
                    " P   P ",
                    " L   L ",
                    "       "
        },


        {
                    " PPPPP ",
                    " PPPPP ",
                    " PP0PP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP ",
                    " PPPPP "
        }

    };






    public static void init(){
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();

        LOOM_HALL = api.makeMultiblock(
                LOOM_PATTERN,
                'P', api.predicateMatcher(Blocks.POLISHED_DEEPSLATE, state -> state.isIn(LoomTags.POLISHED_STONE) ),
                'L', api.predicateMatcher(Blocks.POLISHED_BASALT, state -> state.isIn(BlockTags.LOGS)|| state.isOf(Blocks.POLISHED_BASALT)),
                'G', Blocks.GLOWSTONE,
                'S', LoomMiscRegistry.RITUAL_CONTROLLER,
                '0', api.predicateMatcher(Blocks.POLISHED_DEEPSLATE, state -> state.isIn(LoomTags.POLISHED_STONE))
        );


        api.registerMultiblock(
                Songweaver.locate("loom_hall"),
                LOOM_HALL
        );
    }

}
