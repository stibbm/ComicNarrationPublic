package matt.actions;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.io.FileUtils;

import matt.adapter.GptContentAdapter;
import matt.adapter.SceneAdapter;
import matt.adapter.ScenesAdapter;
import matt.clientwrapper.GptClientWrapper;
import matt.clientwrapper.HttpClientWrapper;
import matt.model.ChatCompletion;
import matt.model.Panel;
import matt.model.Scene;
import matt.steps.Model;

public class Bones {
    private GptClientWrapper gptClientWrapper;
    private GptContentAdapter gptContentAdapter;
    private ScenesAdapter scenesAdapter;
    private String prompt;
    private SceneAdapter sceneAdapter = new SceneAdapter();

    public Bones(String endpointUrl, String apiKey, Model model, String prompt) {
        this.sceneAdapter = new SceneAdapter();
        HttpClientWrapper httpClientWrapper = new HttpClientWrapper();
        this.gptContentAdapter = new GptContentAdapter();
        this.scenesAdapter = new ScenesAdapter();
        this.gptClientWrapper = new GptClientWrapper(httpClientWrapper, endpointUrl, apiKey, model);
        this.prompt = prompt;
    }

    public Bones(String endpointUrl, String apiKey, Model model, File promptFile)
            throws IOException {
        this(endpointUrl, apiKey, model, Files.readString(promptFile.toPath()));
    }

    public Bones(
            GptClientWrapper gptClientWrapper, GptContentAdapter gptContentAdapter, String prompt) {
        this.gptClientWrapper = gptClientWrapper;
        this.gptContentAdapter = gptContentAdapter;
        this.prompt = prompt;
    }

    public List<Panel> runWithBase64EncodedImages(List<matt.model.Panel> panelList)
            throws IOException, InterruptedException {
        for (Panel panel : panelList) {
            String absoluteFilePath = panel.getFile().getAbsolutePath();
            File imageFile = new File(absoluteFilePath);
            byte[] imageBytes = FileUtils.readFileToByteArray(imageFile);
            String base64EncodedImageString = Base64.getEncoder().encodeToString(imageBytes);
            String urlString = "data:image/" + "png" + ";base64," + base64EncodedImageString;
            String response = gptClientWrapper.send(prompt, urlString);
            ChatCompletion chatCompletion = gptContentAdapter.buildChatCompletion(response);
            Scene scene = this.sceneAdapter.buildSceneFromChatCompletion(chatCompletion);
            panel.setScene(scene);
        }
        return panelList;
    }

    public List<Panel> run(List<matt.model.Panel> panelList)
            throws IOException, InterruptedException {

        // REMOVE LATER

        // REMOVE LATE
        for (Panel panel : panelList) {
            String imageUrl = panel.getDropboxUrl();
            String response = gptClientWrapper.send(prompt, imageUrl);
            ChatCompletion chatCompletion = gptContentAdapter.buildChatCompletion(response);
            Scene scene = this.sceneAdapter.buildSceneFromChatCompletion(chatCompletion);
            panel.setScene(scene);
        }
        return panelList;
    }

    private List<Integer> getPanelNumbers(List<Panel> panelList) {
        Set<Integer> panelNumberList = new HashSet<>();
        for (matt.model.Panel panel : panelList) {
            panelNumberList.add(panel.getPanelNumber());
        }
        return panelNumberList.stream().toList();
    }

    private List<matt.model.Panel> getSortedPanelsForPanelNumber(
            List<Panel> inputPanelList, int panelNumber) {
        List<Panel> panelList =
                inputPanelList.stream()
                        .filter(
                                panel -> {
                                    return panel.getPanelNumber() == panelNumber;
                                })
                        .sorted(
                                new Comparator<Panel>() {
                                    @Override
                                    public int compare(Panel o1, Panel o2) {
                                        return o1.getImageIndex() - o2.getImageIndex();
                                    }
                                })
                        .toList();
        return panelList;
    }
}
