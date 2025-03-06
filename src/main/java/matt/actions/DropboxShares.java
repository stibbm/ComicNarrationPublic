package matt.actions;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.PathLinkMetadata;

public class DropboxShares {

    private DropboxClientWrapper dropboxClientWrapper;
    private String comicName;

    public DropboxShares(
            String dropboxApiKey,
            String appFolderPath,
            String comicName) {
        this.dropboxClientWrapper = new DropboxClientWrapper(
                dropboxClientWrapper,
                comicName);
        this.comicName = comicName;
    }

    public DropboxShares(
            DropboxClientWrapper dropboxClientWrapper,
            String comicName) {
        this.dropboxClientWrapper = dropboxClientWrapper;
        this.comicName = comicName;
    }

    public List<PathLinkMetadata> run(List<File> pngFileList, String runId)
            throws IOException, DbxException {
        List<PathLinkMetadata> pathLinkMetadataList = createDropboxShares(pngFileList, runId);
        return pathLinkMetadataList;
    }
    public List<PathLinkMetadata> createDropboxShares(List<File> pngFilesList, String runId)
            throws IOException, DbxException {
        System.out.println(pngFilesList);
        List<PathLinkMetadata> pathLinkMetadatas = new ArrayList<>();
        for (File file : pngFilesList) {
            String remotePath = buildDropboxSharePath(comicName, runId, file.getName());
            System.out.println("remote path: " + remotePath);
            System.out.println("runId = " + runId);
            FileMetadata fileMetadata = dropboxClientWrapper.createDropboxFile(file, remotePath);
            System.out.println(fileMetadata.getName());
            System.out.println(fileMetadata.getPathDisplay());
            System.out.println(fileMetadata.getPathLower());
            try {
                System.out.println(
                        "sleeping so that dropbox has time to update that the file exists");
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PathLinkMetadata pathLinkMetadata =
                    dropboxClientWrapper.createDropboxSharedLink(fileMetadata.getPathLower());
            System.out.println(pathLinkMetadata);
            // System.out.println("pathLinkMetadata: " + pathLinkMetadata);
            pathLinkMetadatas.add(pathLinkMetadata);
        }
        return pathLinkMetadatas;
    }

    private String buildDropboxSharePath(String comicName, String runId, String fileName) {
        String path = "/" + comicName + "/startingImages/" + runId + "/" + fileName;
        return path;
    }

}
