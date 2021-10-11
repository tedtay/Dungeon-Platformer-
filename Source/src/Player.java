import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * This Class models a player character.
 *
 * @author Radu Bucurescu.
 * @author Jordan w/e
 * @author Harry Cassel.
 */
public class Player {
    private String filePath = "assets/images/Player.png";
    private Image artAsset;
    private ArrayList<Item> inventory;
    private Point2D position;

    /**
     * Constructor.
     */
    public Player() {
        position = new Point2D(0, 0);
        inventory = new ArrayList<Item>();
        inventory = new ArrayList<>();
        position = new Point2D(0.0, 0.0);

        try {
            artAsset = new Image(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set players position.
     *
     * @param position position to be set.
     */

    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Retrieve players position.
     *
     * @return position of player.
     */
    public Point2D getPosition() {
        return this.position;
    }

    /**
     * Retrieve players inventory.
     *
     * @return players inventory.
     */
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item i) {
        inventory.add(i);
    }

    /**
     * Returns the Image representation of this class.
     *
     * @return Image object containing an art asset.
     */
    public Image getImage() {
        if (artAsset == null) {
            return null;
        }
        return artAsset;
    }

    /**
     * Converts the players current inventory into a string.
     *
     * @return String representation of players inventory.
     */
    public String getInventoryContents() {
        StringBuilder contents = new StringBuilder();

        for (Item item : inventory) {
            String itemData = "Inventory ";
            String currentItem = item.getClass().getName();

            if (currentItem.equals("Key")) {
                Key key = (Key) item;
                itemData = itemData + currentItem + " " + key.getColour();
            } else {
                itemData = itemData + currentItem;
            }
            contents.append(itemData).append("\n");
        }
        return contents.toString();
    }

    /**
     * Converts an instance of this class to a String.
     *
     * @return String instance.
     */
    @Override
    public String toString() {
        return "Player " + (int) position.getY() + " " + (int) position.getX();
    }
}
