package aster.songweaver.system.definition;

public enum CastFailure {
    UNKNOWN_SONG("Unknown song"),
    LACKING_KNOWLEDGE("Lacking knowledge"),
    INSUFFICIENT_POWER("Insufficient power"),
    NO_COMPONENTS("Missing components"),
    SILENCED("Your voice is stilled"),
    INVALID_STRUCTURE("The song does not resonate"),
    WRONG_DIMENSION("This realm suppresses your music"),
    RITUAL_BUSY("Your song was heard, but ignored");

    private final String message;

    CastFailure(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}