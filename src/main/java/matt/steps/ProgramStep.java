package matt.steps;

public enum ProgramStep {
    FILES("Files"),
    DBX_SHARED_FILES("Dropbox Shared Files"),
    BONES("Bones"),
    SKIN("Skin"),
    VOICE("Voice"),
    VIDEO("Video"),
    COMBINED("Combine");

    private String displayName;

    ProgramStep(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
