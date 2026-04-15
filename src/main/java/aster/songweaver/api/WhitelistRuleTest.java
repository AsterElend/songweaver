package aster.songweaver.api;

import aster.songweaver.registry.LoomTags;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.Random;

public class WhitelistRuleTest extends RuleTest {
    public static final Codec<WhitelistRuleTest> CODEC = Codec.unit(new WhitelistRuleTest());

    @Override
    public boolean test(BlockState state, Random random) {
        return state.isIn(LoomTags.TERRAFORM_WHITELIST);
    }

    @Override
    protected RuleTestType<?> getType() {
        return LoomRuleTests.WHITELIST;
    }
}
