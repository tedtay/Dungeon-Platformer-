import javafx.scene.image.Image;

import java.io.FileInputStream;

public class TileMap {
    private Image[][] playerFrames;
    private Image[][] mapTiles;
    private int gridCellSize;

    public TileMap(int gridCellSize) {
        this.gridCellSize = gridCellSize;
        playerFrames = new Image[3][3];
        String prefix = "assets/images/Player_sprites/";

        /*
         * 0 - facing forward
         * 1 - facing left
         * 2 - facing backwards
         */
        try {
            playerFrames[0][0] = new Image(new FileInputStream(prefix + "player_dwn1.png"));
            playerFrames[0][1] = new Image(new FileInputStream(prefix + "player_dwn2.png"));
            playerFrames[0][2] = new Image(new FileInputStream(prefix + "player_dwn3.png"));
            playerFrames[1][0] = new Image(new FileInputStream(prefix + "player_left1.png"));
            playerFrames[1][1] = new Image(new FileInputStream(prefix + "player_left2.png"));
            playerFrames[1][2] = new Image(new FileInputStream(prefix + "player_left3.png"));
            playerFrames[2][0] = new Image(new FileInputStream(prefix + "player_up1.png"));
            playerFrames[2][1] = new Image(new FileInputStream(prefix + "player_up2.png"));
            playerFrames[2][2] = new Image(new FileInputStream(prefix + "player_up3.png"));
        } catch (Exception e) {
            System.out.println("Couldn't find the images");
        }
    }

    public Image getFrame(int x, int y) {
        return playerFrames[x][y];
    }

    public Image[] getSplices(int i) {
        return playerFrames[i];
    }
}

