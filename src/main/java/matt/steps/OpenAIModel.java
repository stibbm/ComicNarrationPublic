package matt.steps;

public enum OpenAIModel {
    GPT_3_POINT_5_TURBO("gpt-3.5-turbo"),
    GPT_4_O("gpt-4o"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4("gpt-4");

    String displayName;

    OpenAIModel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static OpenAIModel fromDisplayName(String displayName) {
        for (OpenAIModel openAIModel : values()) {
            if (openAIModel.displayName.equalsIgnoreCase(displayName)) {
                return openAIModel;
            }
        }
        throw new IllegalArgumentException("Could not find an enum with that display name");
    }
}
