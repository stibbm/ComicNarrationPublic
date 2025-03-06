package matt.actions;

public class DropboxShares {

    private DropboxClientWrapper dropboxClientWrapper;
    private String comicName;

    public DropboxShares(
        String dropboxApiKey,
        String appFolderPath,
        String comicName
    ) {
        this.dropboxClientWrapper = new DropboxClientWrapper(
            dropboxClientWrapper,
            comicName
        );
        this.comicName = comicName;
    }

    public DropboxShares(
        DropboxClientWrapper dropboxClientWrapper,
        String comicName
    ) {
        this.dropboxClientWrapper = dropboxClientWrapper;
        this.comicName = comicName;
    }
}
