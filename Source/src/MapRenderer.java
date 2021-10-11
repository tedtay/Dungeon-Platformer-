import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * The initial file used to test JavaFX and the rendering of the game.
 * Yes, Harry, the file is an absolute mess, I will fix it later.
 * This file has been heavily modified by Radu Bucurescu, the original creator of the file being Liam O'Reilly.
 *
 * @author Radu Bucurescu
 */
public class MapRenderer {
    private final String SFX_DEATH = "sound/Death.mp3";
    private final String SFX_GOAL = "sound/Goal.mp3";
    // The size of each cell
    public static final int GRID_CELL_SIDE = 100;
    // Variables for the rendered cells the player will see.
    private int RENDERED_CELLS_X;
    private int RENDERED_CELLS_Y;
    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;
    //Values used for the rendering of the game.
    private Canvas gameMap; //The canvas on which we draw the map.
    private Pane enemiesParent; //We are gonna place the enemies here
    private Pane gameGraphics; //This will hold both the game map and the enemies
    private Canvas blackPause;
    private InGameMenus menus;
    //The variables used to check the input from the player
    //to control the animation.
    private boolean canMove = true;
    private boolean isPaused = false;
    private boolean playerIsReversed = false;
    private boolean hasDied = false;
    //Values used for the animation of the game
    private float animationTime;
    private TranslateTransition translateTransition;
    // Loaded images
    //Don't worry, those are all test values too, I'll put them in tilemaps after everything is working fine.
    private ImageView player;
    private Image wall;
    private Map mapClass;
    private Cell[][] map;
    private int playerX;
    private int playerY;
    private Player playerClass;
    private GameMaster game;
    private Pane root;

    /**
     * The constructor for the map renderer.
     *
     * @param root          pane on which the game is rendered.
     * @param WINDOW_WIDTH  of the window.
     * @param WINDOW_HEIGHT of the window.
     */
    public MapRenderer(Pane root, final int WINDOW_WIDTH, final int WINDOW_HEIGHT, Map map, GameMaster game) {
        // The dimensions of the canvas
        CANVAS_WIDTH = (WINDOW_WIDTH + 2 * GRID_CELL_SIDE);
        CANVAS_HEIGHT = (WINDOW_HEIGHT + 2 * GRID_CELL_SIDE);
        RENDERED_CELLS_X = CANVAS_WIDTH / GRID_CELL_SIDE;
        RENDERED_CELLS_Y = CANVAS_HEIGHT / GRID_CELL_SIDE;

        enemiesParent = new Pane();
        gameMap = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gameGraphics = new Pane(gameMap, enemiesParent);
        gameMap.setTranslateX(-GRID_CELL_SIDE);
        gameMap.setTranslateY(-GRID_CELL_SIDE);
        this.mapClass = map;
        this.map = mapClass.getCellArray();
        this.game = game;
        this.root = root;

        blackPause = new Canvas();
        blackPause.setWidth(CANVAS_WIDTH);
        blackPause.setHeight(CANVAS_HEIGHT);
        GraphicsContext gc = blackPause.getGraphicsContext2D();

        gc.setFill(new Color(0, 0, 0, 0.6f));

        menus = new InGameMenus(WINDOW_WIDTH, WINDOW_HEIGHT, this);

        playerClass = mapClass.getPlayer();
        playerX = (int) playerClass.getPosition().getX();
        playerY = (int) playerClass.getPosition().getY();

        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(animationTime));
        translateTransition.setNode(gameGraphics);
        translateTransition.setInterpolator(Interpolator.LINEAR);

        // don't forget to remove this later.
        // createTestObjects(); //only used for the testing phase.
        // Load images
        try {
            player = new ImageView();
            wall = new Image(new FileInputStream("assets/images/Wall.png"));
            player.setTranslateY(-10);
        } catch (Exception e) {
            System.out.println("Couldn't find the images");
        }
        root.getChildren().addAll(gameGraphics, player, blackPause);
        drawGame(0, 0);
    }

    /**
     * Execute the animation for the player movement.
     *
     * @param x coordinate the player moved with.
     * @param y coordinate the player moved with.
     */
    public void animatePlayerMove(int x, int y, boolean hasDied) {
        if (x != 0 || y != 0) {
            if (x == 1) {
                if (!playerIsReversed) {
                    reversePlayerSprite();
                }
            } else if (playerIsReversed) {
                reversePlayerSprite();
            }
        }
        canMove = false;
        animateAccordingToMovement(-x, -y, hasDied);
        drawGame(x, y);
    }

    /**
     * Execute the transition animation for the player movement.
     *
     * @param x The x coordinate the player moved with.
     * @param y The y coordinate the player moved with.
     */
    private void animateAccordingToMovement(int x, int y, boolean willDie) {
        double destX;
        double destY;

        //If the player is going to die, cut the animation by half
        if (willDie) {
            destX = (x * GRID_CELL_SIDE) / 2.0;
            destY = (y * GRID_CELL_SIDE) / 2.0;
            System.out.println(animationTime / 2.0);
            translateTransition.setDuration(Duration.seconds(animationTime / 2.0));
        } else {
            destX = (x * GRID_CELL_SIDE);
            destY = (y * GRID_CELL_SIDE);
        }

        translateTransition.setToX(destX);
        translateTransition.setToY(destY);
        translateTransition.play();

        translateTransition.setOnFinished(e -> {
            if (willDie) {
                playerDeath();
            } else {
                playerX -= x;
                playerY -= y;
                Point2D pos = new Point2D(playerX, playerY);
                playerClass.setPosition(pos);
                gameGraphics.setTranslateX(0);
                gameGraphics.setTranslateY(0);

                if (!game.isTeleporting()) {
                    canMove = !isPaused;
                    game.checkGround();
                } else {
                    game.teleportPlayer();
                }
                drawGame(0, 0);
            }
        });
    }

    /**
     * Checks if an item/enemy is on the screen.
     *
     * @param x coordinate of the element.
     * @param y coordinate of the element.
     * @return True if the item is on-screen or false otherwise.
     */
    private boolean isToBeRendered(int x, int y) {
        boolean satisfiesX = false;
        boolean satisfiesY = false;

        if (x >= (playerX - RENDERED_CELLS_X / 2) && x <= (playerX + RENDERED_CELLS_X / 2)) {
            satisfiesX = true;
        }
        if (y >= (playerY - RENDERED_CELLS_Y / 2) && y <= (playerY + RENDERED_CELLS_Y / 2)) {
            satisfiesY = true;
        }
        return satisfiesX && satisfiesY;
    }

    /**
     * The method used to draw the entire game.
     *
     * @param x coordinate the player moved with.
     * @param y coordinate the player moved with.
     */
    public void drawGame(int x, int y) {
        if (hasDied) {
            return;
        }

        drawMap();
        drawItems();
        ///removes the enemies from the scene
        enemiesParent.getChildren().remove(0, enemiesParent.getChildren().size());
        drawEnemies(x, y);
    }

    /**
     * Draws the items that are on screen on the canvas and
     * executes their moving animation.
     */
    private void drawEnemies(int inpx, int inpy) {
        ArrayList<Enemy> enemies = mapClass.getEnemyArray();

        for (Enemy enemy : enemies) {
            Point2D coord = enemy.getPosition();
            int y = (int) coord.getY();
            int x = (int) coord.getX();

            if (isToBeRendered(x, y)) {
                ImageView enemyImage = new ImageView();
                enemyImage.setImage(enemy.getImage());
                enemiesParent.getChildren().add(enemyImage);

                double xCoord = (x - playerX + RENDERED_CELLS_X / 2 - 1) * GRID_CELL_SIDE;
                double yCoord = (y - playerY + RENDERED_CELLS_Y / 2 - 1) * GRID_CELL_SIDE;

                enemyImage.setTranslateX(xCoord);
                enemyImage.setTranslateY(yCoord);

                if (!game.isTeleporting() && (inpx != 0 || inpy != 0)) {
                    animateEnemies(enemyImage, enemy);
                }
            }
            if (!game.isTeleporting() && (inpx != 0 || inpy != 0)) {
                enemy.updatePosition(enemy.getNextMove());
            }
        }
    }

    /**
     * Execute the transition animation for the player movement.
     *
     * @param enemyImage render of the enemyImage we are animating.
     * @param enemy      enemyImage object of the enemy.
     */
    private void animateEnemies(ImageView enemyImage, Enemy enemy) {
        Point2D movingCoords = enemy.getNextMove();

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(animationTime));

        double destX = enemyImage.getTranslateX() + (movingCoords.getX()) * GRID_CELL_SIDE;
        double destY = enemyImage.getTranslateY() + (movingCoords.getY()) * GRID_CELL_SIDE;

        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setToX(destX);
        translateTransition.setToY(destY);
        translateTransition.setNode(enemyImage);
        translateTransition.play();
    }

    /**
     * Draws the items that are on screen on the canvas.
     */
    private void drawItems() {
        GraphicsContext gc = gameMap.getGraphicsContext2D();
        ArrayList<Item> items = mapClass.getItemArray();

        for (Item item : items) {
            Point2D coord = item.getPosition();
            int x = (int) coord.getX(), y = (int) coord.getY();

            if (isToBeRendered(x, y)) {
                double xCoord = (x - playerX + RENDERED_CELLS_X / 2) * GRID_CELL_SIDE;
                double yCoord = (y - playerY + RENDERED_CELLS_Y / 2) * GRID_CELL_SIDE;
                gc.drawImage(item.getImage(), xCoord, yCoord);
            }
        }
    }

    /**
     * Draw the game on the canvas.
     */
    private void drawMap() {
        // Get the Graphic Context of the canvas. This is what we draw on.
        GraphicsContext gc = gameMap.getGraphicsContext2D();
        gc.clearRect(0, 0, gameMap.getWidth(), gameMap.getHeight());

        for (int x = 0; x < RENDERED_CELLS_X; x++) {
            for (int y = 0; y < RENDERED_CELLS_Y; y++) {
                drawCell(x, y, gc);
            }
        }
    }

    /**
     * Draw the cell at the respective coordinates.
     *
     * @param x  coordinate of the point we are drawing on the screen.
     * @param y  coordinate of the point we are drawing on the screen.
     * @param gc graphics context of the game.
     **/
    private void drawCell(int x, int y, GraphicsContext gc) {
        Image cell = wall;

        try {
            cell = map[y + playerY - RENDERED_CELLS_Y / 2][x + playerX - RENDERED_CELLS_X / 2].getImage();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        double xCoord = x * GRID_CELL_SIDE;
        double yCoord = y * GRID_CELL_SIDE;
        gc.drawImage(cell, xCoord, yCoord);
    }

    /**
     * Executes the death sequence.
     */
    public void playerDeath() {
        playAudioClip(SFX_DEATH);
        hasDied = true;
        if (isPaused) {
            unPause();
        }
        canMove = false;

        gameGraphics.getChildren().remove(enemiesParent);
        this.gameMap.getGraphicsContext2D().setFill(Color.BLACK);
        this.gameMap.getGraphicsContext2D().fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    FadeTransition fd = new FadeTransition(Duration.millis(1000), player);
                    fd.setFromValue(1.0f);
                    fd.setToValue(0.0f);
                    fd.play();
                    fd.setOnFinished((ActionEvent e) -> {
                        AnchorPane deathMenu = menus.getDeathMenu();
                        deathMenu.setOpacity(0.0f);
                        FadeTransition fdm = new FadeTransition(Duration.millis(1000), deathMenu);
                        fdm.setFromValue(0.0f);
                        fdm.setToValue(1.0f);
                        fdm.play();
                        root.getChildren().add(deathMenu);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Executes the death sequence.
     */
    public void renderGoal() {
        playAudioClip(SFX_GOAL);
        drawGame(0, 0);
        hasDied = true;
        if (isPaused) {
            unPause();
        }
        canMove = false;

        FadeTransition fd = new FadeTransition(Duration.millis(1000), player);
        fd.setFromValue(1.0f);
        fd.setToValue(0.0f);
        fd.play();
        blackPause.setOpacity(0.0f);
        FadeTransition fdScreen = new FadeTransition(Duration.millis(1000), blackPause);
        fdScreen.setFromValue(0.0f);
        fdScreen.setToValue(1.0f);
        fdScreen.play();
        blackPause.getGraphicsContext2D().fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        fd.setOnFinished((ActionEvent e) -> {
            AnchorPane goalMenu = menus.getGoalMenu();
            goalMenu.setOpacity(0.0f);
            FadeTransition fdm = new FadeTransition(Duration.millis(1000), goalMenu);
            fdm.setFromValue(0.0f);
            fdm.setToValue(1.0f);
            fdm.play();
            root.getChildren().add(goalMenu);
        });
    }

    /**
     * Pauses the game.
     */
    public void pause() {
        if (hasDied) {
            return;
        }
        canMove = false;
        isPaused = true;
        blackPause.getGraphicsContext2D().fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        root.getChildren().add(menus.getPauseMenu());
    }


    /**
     * Unpause the game.
     */
    public void unPause() {
        canMove = true;
        isPaused = false;
        blackPause.getGraphicsContext2D().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        root.getChildren().remove(menus.getPauseMenu());
    }

    /**
     * Check if the player can move.
     *
     * @return canMove
     */
    public boolean canMove() {
        return canMove;
    }

    /**
     * Set the current sprite for the player.
     *
     * @param playerSprite Current sprite.
     */
    public void setPlayerSprite(Image playerSprite) {
        player.setImage(playerSprite);
    }

    /**
     * Get the animation time in nano seconds.
     *
     * @return The animation time.
     */
    public long getAnimationTime() {
        return (long) this.animationTime * 1000;
    }

    /**
     * Mirrors the player's sprite.
     */
    public void reversePlayerSprite() {
        player.setScaleX(player.getScaleX() * (-1));
        playerIsReversed = !playerIsReversed;
    }

    /**
     * Checks if the player is reverse.
     *
     * @return playerIsReversed;
     */
    public boolean playerIsReversed() {
        return playerIsReversed;
    }

    public void makePlayerInvisible() {
        player.setOpacity(0.0f);
    }

    public void makePlayerVisible() {
        player.setOpacity(1.0f);
    }

    /**
     * Set the animation time for the transition.
     *
     * @param time
     */
    public void setAnimationTime(float time) {
        translateTransition.setDuration(Duration.seconds(time));
        animationTime = time;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Plays the specified audio file, used for short SFX.
     *
     * @param path path to the audio file.
     */
    private void playAudioClip(String path) {
        AudioClip soundFX = new AudioClip(new File(path).toURI().toString());
        soundFX.play();
    }
}