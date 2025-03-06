package matt.actions;

import java.io.IOException;
import java.util.List;

public class StripSplit {

    private PanelSplitterService panelSplitterService =
        new PanelSplitterService();

    public StripSplit() throws IOException {}

    public List<Panel> run(List<Panel> panelList) throws IOException {
        panelList = panelSplitterService.runWithPanelList(panelList);
        return panelList;
    }
}
