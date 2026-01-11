package aster.songweaver.system.definition;

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