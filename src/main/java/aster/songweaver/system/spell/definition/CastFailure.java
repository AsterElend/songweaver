package aster.songweaver.system.spell.definition;

public enum CastFailure {
    UNKNOWN_SONG("Unknown song"),
    LACKING_KNOWLEDGE("Lacking knowledge"),
    INSUFFICIENT_POWER("Insufficient power"),
    NO_COMPONENTS("Missing components"),
    SILENCED("Your voice is stilled"),
    INVALID_STRUCTURE("The song does not resonate"),
    WRONG_DIMENSION("This realm suppresses your music"),
    RITUAL_BUSY("Your song was heard, but ignored"),
    RITUAL_DRAFT_MISMATCH("This is a json problem. Maybe check weather you used the Structure requirements in a non ritual spell."),
    WRONG_ENTITY("The one who hears does not understand"),
    NO_TARGET("There is none to hear your song"),
    MALFORMED_JSON("This is a json error! Please check the datapack.");

    private final String message;

    CastFailure(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}