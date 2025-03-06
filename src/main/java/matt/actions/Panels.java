package matt.actions;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import matt.imageprocessing.model.Panel;
import matt.imageprocessing.service.TextRemovalService;
import matt.imageprocessing.step.PanelStep;
import matt.service.PanelSplitterService;
import matt.steps.PanelsAction;

public class Panels {
    public PanelStep panelStep = new PanelStep();
    public static final String PANELS_DIRECTORY = "splitfiles";
    public TextRemovalService textRemovalService = new TextRemovalService();
    public PanelSplitterService panelSplitterService = new PanelSplitterService();

    public Panels() throws IOException {}

    public List<matt.model.Panel> run(List<File> pngFileList) {
        List<matt.model.Panel> panelList = new ArrayList<>();
        List<File> panelFileList = new ArrayList<>();
        int panelIndex = 0;
        for (File file : pngFileList) {
            matt.model.Panel panel = new matt.model.Panel(panelIndex++, 0, file.getAbsolutePath());
            panelList.add(panel);
        }
        return panelList;
    }

    public List<matt.model.Panel> run(List<File> pngFileList, PanelsAction panelsAction)
            throws IOException {
        createPanelsDirectoryIfDoesNotExist(PANELS_DIRECTORY);
        if (panelsAction != PanelsAction.SPLIT_IMAGES_INTO_PANELS) {
            throw new IllegalArgumentException("PanelsAction not recognized");
        }
        List<matt.model.Panel> myPanelList = new ArrayList<>();
        List<File> panelFileList = new ArrayList<>();
        for (int pngFileIndex = 0; pngFileIndex < pngFileList.size(); pngFileIndex++) {
            File pngFile = pngFileList.get(pngFileIndex);
            List<Panel> panelList = panelStep.run(pngFile);
            BufferedImage bufferedImage = ImageIO.read(pngFile);
            int xstart = 0;
            int xend = bufferedImage.getWidth();
            for (int i = 0; i < panelList.size(); i++) {
                Panel panel = panelList.get(i);
                String filePath =
                        PANELS_DIRECTORY
                                + "/"
                                + pngFile.getName()
                                + "_panelIndex-"
                                + pngFileIndex
                                + "_subimage-"
                                + i
                                + ".png";
                int ystart = panel.minY;
                int yend = panel.maxY;
                int height = yend - ystart;
                int width = xend - xstart;

                BufferedImage subImage = bufferedImage.getSubimage(xstart, ystart, width, height);

                ImageIO.write(subImage, "png", new File(filePath));
                panelFileList.add(new File(filePath));
                matt.model.Panel myPanel =
                        new matt.model.Panel(
                                pngFileIndex,
                                panelFileList.size() - 1,
                                panelFileList.get(panelFileList.size() - 1).getAbsolutePath());
                myPanelList.add(myPanel);
            }
        }
        return myPanelList;
    }

    private boolean createPanelsDirectoryIfDoesNotExist(String panelsDir) {
        File dir = new File(panelsDir);
        if (dir.isDirectory()) {
            return true;
        } else if (dir.exists() == false) {
            dir.mkdir();
            return true;
        } else {
            throw new RuntimeException("file already exists and is not a directory");
        }
    }
}
