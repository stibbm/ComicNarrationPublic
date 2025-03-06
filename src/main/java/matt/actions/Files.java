package matt.actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Files {
    private ImageFileService imageFileService;
    private PageSizeImageAdapter pageSizeImageAdapter = new PageSizeAdapterImpl();
    private RandomFileNamingService randomFileNamingService = new RandomFileNamingService();
    private int maxHeight = 5000;
    private FFmpegManager fFmpegManager = new FFmpegManager();
    private ImageConversion imageConversion = new ImageConversionImpl();

    public Files(ImageFileService imageFileService) throws IOException {
        this.imageFileService = imageFileService;
    }

    public Files() throws IOException {
        this(new ImageFileService());
    }

    public List<File> run(File imagesDirectory) throws IOException {
        List<File> fileList = Arrays.asList(imagesDirectory.listFiles());
        for (File file : fileList) {
            System.out.println("file: " + file.getAbsolutePath());
        }
        fileList = convertImagesToPng(fileList);
        for (File file : fileList) {
            System.out.println("file: " + file.getAbsolutePath());
        }
        fileList = filterOutNonPngs(fileList);

        List<File> scaledFiles = new ArrayList<>();
        for (File file : fileList) {
            Size size = imageConversion.getSize(file);
            if (size.getHeight() > 10000) {
                String randResizedName =
                        randomFileNamingService.generateRandomFileName("png", true);
                int newHeight = (int) (size.height / 2.0);
                int newWidth = (int) (size.width / 2.0);
                File resizedFile =
                        imageConversion.resizeImage(file, randResizedName, newHeight, newWidth);
                scaledFiles.add(resizedFile);
            } else {
                scaledFiles.add(file);
            }
        }
        fileList = scaledFiles;
        // fileList = enforceMaxImageStripHeight(fileList, maxHeight);
        return fileList;
    }

    public List<File> enforceMaxImageStripHeight(List<File> fileList, int maxHeight)
            throws IOException {
        List<File> filteredFileList = new ArrayList<>();
        // 1. get list of page images
        // 2. combine them back together abiding by the height limit
        // 3. return them
        for (File file : fileList) {
            BufferedImage panelBufferedImage = ImageIO.read(file);
            ImageData panelImageData = ImageData.fromBufferedImage(panelBufferedImage, "png");
            List<ImageData> pageImageDataList =
                    pageSizeImageAdapter.convertImageStripToPageSizeImages(file);
            List<ImageData> maxHeightPanelImageDataList =
                    createPanelsWithMaxHeight(panelImageData, pageImageDataList, maxHeight);
            for (ImageData maxHeightImageData : maxHeightPanelImageDataList) {
                File maxHeightFile = maxHeightImageData.getAsFile();
                filteredFileList.add(maxHeightFile);
            }
        }
        return filteredFileList;
    }

    public List<ImageData> createPanelsWithMaxHeight(
            ImageData panelImageData, List<ImageData> pageImageDataList, int maxHeight)
            throws IOException {
        System.out.println("panelImageData: " + panelImageData.getHeight());
        System.out.println("panelImageData: " + panelImageData.getWidth());
        int currentHeight = 0;
        List<ImageData> fixedStripImageDataList = new ArrayList<>();

        for (int i = 0; i < pageImageDataList.size(); i++) {
            ImageData pageImageData = pageImageDataList.get(i);
            int heightIfCombine = currentHeight + pageImageData.getHeight();
            if ((heightIfCombine <= maxHeight) && (i == pageImageDataList.size() - 1)) {
                fixedStripImageDataList.add(pageImageData);
                return fixedStripImageDataList;
            } else if ((heightIfCombine <= maxHeight) && (i < (pageImageDataList.size() - 1))) {
                currentHeight = heightIfCombine;
                continue;
            } else if ((heightIfCombine > maxHeight)) {
                System.out.println("height if combine = " + heightIfCombine);
                System.out.println("maxHeight = " + maxHeight);
                BufferedImage strip = panelImageData.getBufferedImage();
                System.out.println(
                        "strip size height, width= "
                                + panelImageData.getWidth()
                                + ", "
                                + panelImageData.getHeight());
                System.out.println("current height = " + currentHeight);
                System.out.println(heightIfCombine);
                BufferedImage firstPart = strip.getSubimage(0, 0, strip.getWidth(), currentHeight);
                ImageData firstPartImageData = ImageData.fromBufferedImage(firstPart, "png");
                fixedStripImageDataList.add(firstPartImageData);

                int remainingHeight = strip.getHeight() - firstPart.getHeight();
                System.out.println("remaining height: " + remainingHeight);
                BufferedImage secondPart =
                        strip.getSubimage(
                                0,
                                firstPartImageData.getHeight(),
                                strip.getWidth(),
                                remainingHeight);
                ImageData secondPartImageData = ImageData.fromBufferedImage(secondPart, "png");
                List<ImageData> recImageDataList =
                        createPanelsWithMaxHeight(
                                secondPartImageData,
                                pageImageDataList.subList(i, pageImageDataList.size()),
                                maxHeight);
                i = pageImageDataList.size() + 1;
                List<ImageData> secondPartImageDataList = new ArrayList<>();
                secondPartImageDataList.add(secondPartImageData);

                fixedStripImageDataList.addAll(recImageDataList);
                return fixedStripImageDataList;
            }
        }
        return fixedStripImageDataList;
    }

    public List<File> filterOutNonPngs(List<File> fileList) {
        List<File> filteredList = new ArrayList<>();
        for (File file : fileList) {
            try {
                String name = file.getName();
                int dot = file.getName().lastIndexOf(".");
                String ext = name.substring(dot + 1);
                if (ext.equalsIgnoreCase("png")) {
                    filteredList.add(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filteredList;
    }

    public List<File> convertImagesToPng(List<File> fileList) {
        FuzzyNumbers fuzzyNumbers = new FuzzyNumbersImpl();
        ImageConversion imageConversion = new ImageConversionImpl();
        List<File> convertedFileList = new ArrayList<>();
        for (File file : fileList) {
            String name = file.getName();
            if (!name.endsWith(".png")) {
                if (!name.contains(".")) {
                    System.out.println(
                            String.format(
                                    "file must have an extension to be processed, skipping: {}",
                                    name));
                    continue;
                }
                String outputFileName =
                        file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
                                + ".png";
                System.out.println("outputFileName" + outputFileName);
                File outputFile = new File(outputFileName);
                System.out.println("input file = " + file.getAbsolutePath());
                File fixedFile = imageConversion.convertImageType(file, outputFile);
                System.out.println(fixedFile.getAbsolutePath());
                System.out.println(fixedFile.getName());
                convertedFileList.add(fixedFile);
                file.delete();
            } else {
                convertedFileList.add(file);
            }
        }
        convertedFileList =
                convertedFileList.stream()
                        .sorted(
                                (file1, file2) -> {
                                    Integer val1 =
                                            fuzzyNumbers.extractIndexFromFuzzyFileName(
                                                    file1.getName());
                                    Integer val2 =
                                            fuzzyNumbers.extractIndexFromFuzzyFileName(
                                                    file2.getName());
                                    return val1.compareTo(val2);
                                })
                        .toList();
        return convertedFileList;
    }
}
