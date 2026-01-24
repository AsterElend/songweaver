package aster.songweaver.system.spell.definition;

public enum CastFeedback {
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
    NOTE_INTERCEPT("The notes are quietly taken up"),
    NO_STAND("No stored song"),
    MALFORMED_JSON("This is a json error! Please check the datapack.");

    private final String message;

    CastFeedback(String message) {
        this.message = message;
    }

    public String message(boolean auto) {
        if (auto){
            return message + " AUTOMATED RITUAL MESSAGE";
        }
        return message;
    }
}