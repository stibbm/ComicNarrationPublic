package matt.params;

import java.io.Serializable;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Params implements Serializable {
    private String startingDirectory;
    private String comicName;
    @Default private Integer storyContextSize = Constants.DEFAULT_STORY_CONTEXT_SIZE;
    @Default private String dropboxAppFolder = Constants.DEFAULT_DROPBOX_APP_FOLDER;
    private String dropboxApiKey;
    private String chatGptEndpointUrl;
    private String chatGptApiKey;
    @Default private Model chatGptModel = Model.GPT_4_O;
    @Default private Model chatGptSkinModel = Model.GPT_4_O;
    private String chatGptPromptFilePath;

    private String voiceS3BucketName;
    @Default private VoiceId voiceId = VoiceId.MATTHEW;
    @Default private Engine engine = Engine.STANDARD;
    @Default private OutputFormat outputFormat = OutputFormat.MP3;
}
