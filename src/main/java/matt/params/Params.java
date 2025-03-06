package matt.params;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import matt.constants.Constants;
import matt.steps.OpenAIModel;
import software.amazon.awssdk.services.polly.model.Engine;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.VoiceId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Params implements Serializable {

    private String startingDirectory;
    private String comicName;

    @Default
    private Integer storyContextSize = Constants.DEFAULT_STORY_CONTEXT_SIZE;

    @Default
    private String dropboxAppFolder = Constants.DEFAULT_DROPBOX_APP_FOLDER;

    private String dropboxApiKey;
    private String chatGptEndpointUrl;
    private String chatGptApiKey;

    @Default
    private OpenAIModel chatGptModel = OpenAIModel.GPT_4_O;

    @Default
    private OpenAIModel chatGptSkinModel = OpenAIModel.GPT_4_O;

    private String chatGptPromptFilePath;

    private String voiceS3BucketName;

    @Default
    private VoiceId voiceId = VoiceId.MATTHEW;

    @Default
    private Engine engine = Engine.STANDARD;

    @Default
    private OutputFormat outputFormat = OutputFormat.MP3;
}
