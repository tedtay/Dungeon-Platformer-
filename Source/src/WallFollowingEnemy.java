import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Class used for basic wall following walking enemy.
 *
 * @author Radu Bucurescu
 * @author Harry Cassell
 * @author Peter Hawkins
 */
public class WallFollowingEnemy extends Enemy {
    private String direction; //Direction enemy faces e.g "U" = Up
    private Point2D position; //Position of enemy.
    private Cell[][] currentMapState = Map.getInstance().getCellArray(); // MapState
    private String filePath = "assets/images/slineEnemy.png";
    private static Image artAsset;

    /**
     * Construct a new WallFollowingEnemy().
     *
     * @param point2D   value to set a default point2D object.
     * @param direction starting direction of the enemy.
     */
    public WallFollowingEnemy(Point2D point2D, String direction) {
        this.position = point2D;
        this.direction = direction;
        try {
            artAsset = new Image(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to calculate the enemies next move relative to the board state.
     *
     * @return enemies new position on the board.
     */
    @Override
    public Point2D getNextMove() {
        int currentX = (int) position.getX();
        int currentY = (int) position.getY();

        Point2D nextMove = null;

        switch (direction) {
            case "U":
                if (currentMapState[currentY - 1][currentX].getClass() == Wall.class) {
                    if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                        nextMove = new Point2D(-1, 0);
                    } else if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                        nextMove = new Point2D(1, 0);
                    }
                }
                if (nextMove == null) {
                    nextMove = checkUpLeft(currentX, currentY);
                }
                break;
            case "D":
                if (currentMapState[currentY + 1][currentX].getClass() == Wall.class) {
                    if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                        nextMove = new Point2D(+1, 0);
                    } else if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                        nextMove = new Point2D(-1, 0);
                    }
                }
                if (nextMove == null) {
                    nextMove = checkDownRight(currentX, currentY);
                }
                break;
            case "R":
                if (currentMapState[currentY][currentX + 1].getClass() == Wall.class) {
                    if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                        nextMove = new Point2D(0, 1);
                    } else if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                        nextMove = new Point2D(0, -1);
                    }
                }
                if (nextMove == null) {
                    nextMove = checkUpRight(currentX, currentY);
                }
                break;
            case "L":
                if (currentMapState[currentY][currentX + 1].getClass() == Wall.class) {
                    if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                        nextMove = new Point2D(0, -1);
                    } else if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                        nextMove = new Point2D(0, 1);
                    }
                }
                if (nextMove == null) {
                    nextMove = checkDownLeft(currentX, currentY);
                }
                break;
        }
        if (nextMove != null) {
            return nextMove;
        } else {
            switch (direction) {
                case "U":
                    return new Point2D(0, -1);
                case "D":
                    return new Point2D(0, 1);
                case "R":
                    return new Point2D(1, 0);
                case "L":
                    return new Point2D(-1, 0);
            }
            return new Point2D(0, 0);
        }
    }

    /**
     * Checks the upper left corner to consider its next position.
     *
     * @param currentX The enemie's current x coordinate.
     * @param currentY The enemie's current y coordinate.
     * @return The direction of the movement if it's executable, null otherwise.
     */
    private Point2D checkUpLeft(int currentX, int currentY) {
        if (currentMapState[currentY - 1][currentX - 1].getClass() == Wall.class) {
            if (direction.equals("R")) {
                if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                    return new Point2D(0, -1);
                } else if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                    return new Point2D(-1, 0);
                }
            } else if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                return new Point2D(-1, 0);
            } else if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                return new Point2D(0, -1);
            }
        }

        if ("R".equals(direction)) {
            return null;
        }
        return checkUpRight(currentX, currentY);
    }

    /**
     * Checks the lower left corner to consider its next position.
     *
     * @param currentX The enemie's current x coordinate.
     * @param currentY The enemie's current y coordinate.
     * @return The direction of the movement if it's executable, null otherwise.
     */
    private Point2D checkDownLeft(int currentX, int currentY) {
        if (currentMapState[currentY + 1][currentX - 1].getClass() == Wall.class) {
            if (direction.equals("R")) {
                if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                    return new Point2D(0, 1);
                } else if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                    return new Point2D(-1, 0);
                }
            } else if (currentMapState[currentY][currentX - 1].getClass() == Ground.class) {
                return new Point2D(-1, 0);
            } else if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                return new Point2D(0, 1);
            }
        }

        if ("U".equals(direction)) {
            return null;
        }
        return checkUpLeft(currentX, currentY);
    }

    /**
     * Checks the upper right corner to consider its next position.
     *
     * @param currentX enemies current x coordinate.
     * @param currentY enemies current y coordinate.
     * @return The direction of the movement if it's executable, null otherwise.
     */
    private Point2D checkUpRight(int currentX, int currentY) {
        if (currentMapState[currentY - 1][currentX + 1].getClass() == Wall.class) {
            if (direction.equals("L")) {
                if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                    return new Point2D(0, -1);
                } else if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                    return new Point2D(1, 0);
                }
            } else if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                return new Point2D(1, 0);
            } else if (currentMapState[currentY - 1][currentX].getClass() == Ground.class) {
                return new Point2D(0, -1);
            }
        }

        if ("D".equals(direction)) {
            return null;
        }
        return checkDownRight(currentX, currentY);
    }

    /**
     * Checks the lower right corner to consider its next position.
     *
     * @param currentX The enemie's current x coordinate.
     * @param currentY The enemie's current y coordinate.
     * @return The direction of the movement if it's executable, null otherwise.
     */
    private Point2D checkDownRight(int currentX, int currentY) {
        if (currentMapState[currentY + 1][currentX + 1].getClass() == Wall.class) {
            if (direction.equals("L")) {
                if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                    return new Point2D(0, 1);
                } else if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                    return new Point2D(1, 0);
                }
            } else if (currentMapState[currentY][currentX + 1].getClass() == Ground.class) {
                return new Point2D(1, 0);
            } else if (currentMapState[currentY + 1][currentX].getClass() == Ground.class) {
                return new Point2D(0, 1);
            }
        }

        if ("L".equals(direction)) {
            return null;
        }
        return checkDownLeft(currentX, currentY);
    }

    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }

    /**
     * @param direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * @return
     */
    @Override
    public Point2D getPosition() {
        return position;
    }

    /**
     * @param position
     */
    @Override
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Updates the enemies position and direction.
     *
     * @param pos The direction to which the enemy is moving.
     */
    @Override
    public void updatePosition(Point2D pos) {
        String newDirection;
        if (pos.getX() > 0) {
            setDirection("R");
        } else if (pos.getX() < 0) {
            setDirection("L");
        } else if (pos.getY() > 0) {
            setDirection("D");
        } else if (pos.getY() < 0) {
            setDirection("U");
        }
        this.position = new Point2D(position.getX() + pos.getX(), position.getY() + pos.getY());
    }

    /**
     * @return
     */
    @Override
    public Image getImage() {
        if (artAsset == null) {
            return null;
        }
        return artAsset;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "Enemy " + (int) position.getY() + " " + (int) position.getX() + " WF " + direction;
    }
}
