package matt.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Skin {
    public static final Integer DEFAULT_CONTEXT_SIZE = 4;
    public static final String SKIN_GPT_REQUEST_LOGS_DIR = "MyLogs/SkinGptRequestsLog";
    public static final String SKIN_GPT_RESULT_LOGS_DIR = "MyLogs/SkinGptResultsLog";
    public static final String BONES_GPT_RESPONSE_LOG = "MyLogs/BonesGptResponseLog";
    public static final String BONES_GPT_REQUESTS_LOG = "MyLogs/BonesGptRequestsLog";

    static {
        File skinGptRequestsDir = new File(SKIN_GPT_REQUEST_LOGS_DIR);
        File skinGptResultsDir = new File(SKIN_GPT_RESULT_LOGS_DIR);
        File bonesGptResponseDir = new File(BONES_GPT_RESPONSE_LOG);
        File bonesGptRequestsDir = new File(BONES_GPT_REQUESTS_LOG);
        if (skinGptRequestsDir.exists() == false) {
            skinGptRequestsDir.mkdirs();
        }
        if (skinGptResultsDir.exists() == false) {
            skinGptResultsDir.mkdirs();
        }
        if (bonesGptResponseDir.exists() == false) {
            bonesGptResponseDir.mkdirs();
        }
        if (bonesGptRequestsDir.exists() == false) {
            bonesGptRequestsDir.mkdirs();
        }
    }

    private String prompt;
    private Model model;
    private GptContentAdapter gptContentAdapter;
    private OpenAiService openAiService;
    private int contextSize = DEFAULT_CONTEXT_SIZE;
    private Gson gson = new Gson();
    public List<ChatCompletionRequest> chatCompletionRequestHistory = new ArrayList<>();

    public Skin(String apiKey, Model model, int contextSize) throws IOException {
        this.model = model;
        this.contextSize = contextSize;
        this.prompt = Files.readString(new File("prompts/skinsmall.txt").toPath());
        this.openAiService = new OpenAiService(apiKey);
        this.gptContentAdapter = new GptContentAdapter();
    }

    public Skin(
            String prompt,
            Model model,
            GptContentAdapter gptContentAdapter,
            OpenAiService openAiService,
            int contextSize) {
        this.prompt = prompt;
        this.model = model;
        this.gptContentAdapter = gptContentAdapter;
        this.openAiService = openAiService;
        this.contextSize = contextSize;
    }

    public List<Panel> runWithScenesListInput(List<matt.model.Panel> panelList) throws IOException {
        Conversation conversation = new Conversation(contextSize);
        List<ChatCompletionResult> chatCompletionResultList = new ArrayList<>();
        for (matt.model.Panel panel : panelList) {
            Scene scene = panel.getScene();
            scene.filterReusedCharacterDetails();
            String sceneString = gson.toJson(scene);
            ChatMessage chatMessage = new ChatMessage();
            String messageContent = prompt.replace("<json>", sceneString);
            logString(messageContent);
            chatMessage.setContent(messageContent);
            chatMessage.setRole("user");
            conversation.addChatMessage(chatMessage);
            List<ChatMessage> chatMessageList = conversation.getChatMessageContextWindow();
            ChatCompletionRequest chatCompletionRequest =
                    ChatCompletionRequest.builder()
                            .model(this.model.getDisplayName())
                            .messages(chatMessageList)
                            .build();

            ChatCompletionResult chatCompletionResult =
                    openAiService.createChatCompletion(chatCompletionRequest);
            chatCompletionResultList.add(chatCompletionResult);
            ChatMessage chatCompletionResultMessage =
                    chatCompletionResult.getChoices().get(0).getMessage();
            conversation.addChatMessage(chatCompletionResultMessage);
            String resultString = chatCompletionResultMessage.getContent();
            panel.setSkin(resultString);
        }
        return panelList;
    }

    private void logString(String text) throws IOException {
        FileNamingService fileNamingService = new FileNamingService();
        String filePath =
                fileNamingService.getRandomAvailableFileNameWithPrefix("mylogs/", true, "json");
        Files.write(new File(filePath).toPath(), text.getBytes());
    }

    private void logBonesResponseString(String bonesResponseString) throws IOException {
        FileNamingService fileNamingService = new FileNamingService();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filePath =
                fileNamingService.getRandomAvailableFileNameWithPrefix(
                        BONES_GPT_RESPONSE_LOG, true, "json");
        Files.write(new File(filePath).toPath(), bonesResponseString.getBytes());
        try {
            Scene scene = gson.fromJson(bonesResponseString, Scene.class);
            String sceneFilePath =
                    fileNamingService.getRandomAvailableFileNameWithPrefix(
                            BONES_GPT_RESPONSE_LOG, true, "json");
            Files.write(new File(sceneFilePath).toPath(), gson.toJson(scene).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logChatGptRequest(
            List<ChatMessage> chatMessageHistory,
            List<ChatMessage> includedChatMessages,
            ChatCompletionRequest chatCompletionRequest)
            throws IOException {
        FileNamingService fileNamingService = new FileNamingService();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("chat message history size: " + chatMessageHistory.size());
        System.out.println("included chat messages size: " + includedChatMessages.size());

        String chatMessageHistoryString = gson.toJson(chatMessageHistory);
        String includedChatMessagesString = gson.toJson(includedChatMessages);
        String chatCompletionRequestString = gson.toJson(chatCompletionRequest);
        String contentText = "";
        contentText += "ChatMessageHistoryString::\n";
        contentText += ("SIZE: " + chatMessageHistory.size() + "\n");
        contentText += "IncludedChatMessagesString: \n";
        contentText += includedChatMessages.size();

        String combinedText =
                chatCompletionRequestString
                        + "\n"
                        + includedChatMessagesString
                        + "\n"
                        + chatMessageHistoryString;
        contentText += "\n" + combinedText;
        String gptLogFilePath =
                fileNamingService.getRandomAvailableFileNameWithPrefix(
                        SKIN_GPT_REQUEST_LOGS_DIR, true, "json");
        System.out.println("gptLogFilePath: " + contentText);
        Files.write(new File(gptLogFilePath).toPath(), combinedText.getBytes());
    }

    private void logChatGptResult(ChatCompletionResult chatCompletionResult) throws IOException {
        FileNamingService fileNamingService = new FileNamingService();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String chatCompletionResultString = gson.toJson(chatCompletionResult);
        String gptLogFilePath =
                fileNamingService.getRandomAvailableFileNameWithPrefix(
                        SKIN_GPT_RESULT_LOGS_DIR, true, "json");
        Files.write(new File(gptLogFilePath).toPath(), chatCompletionResultString.getBytes());
    }
}
