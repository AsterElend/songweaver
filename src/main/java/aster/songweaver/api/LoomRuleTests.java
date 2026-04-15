package aster.songweaver.api;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;

import static aster.songweaver.Songweaver.MOD_ID;

public class LoomRuleTests {
    public static RuleTestType<WhitelistRuleTest> WHITELIST;

    public static void register() {
        WHITELIST = Registry.register(
                Registries.RULE_TEST,
                new Identifier(MOD_ID, "whitelist"),
                () -> WhitelistRuleTest.CODEC
        );
    }
}
