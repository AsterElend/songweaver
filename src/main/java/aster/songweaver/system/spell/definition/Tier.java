package aster.songweaver.system.spell.definition;

public enum Tier {
    WOOD,
    IRON,
    DIAMOND,
    NETHERITE,
    ASTRAL;

    public boolean meets(Tier required) {
        return this.ordinal() >= required.ordinal();
    }
}