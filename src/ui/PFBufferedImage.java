package ui;

import java.awt.image.BufferedImage;

public class PFBufferedImage extends BufferedImage {

    private final PathfinderWrapper pfw;

    public PFBufferedImage(int width, int height, int imageType, PathfinderWrapper pfw) {
        super(width, height, imageType);
        this.pfw = pfw;
    }

    public PathfinderWrapper getPathfinder() {
        return pfw;
    }
}
