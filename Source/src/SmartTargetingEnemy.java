import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Class used for enemy which targets the player in the best way possible.
 *
 * @author Peter Hawkins
 */
public class SmartTargetingEnemy extends Enemy {
    public final int DIAGONAL_COST = 14;
    public final int V_H_COST = 10;
    private Point2D position;
    private String filePath = "assets/images/smart_targeting_enemy.png";
    private Image image;
    private GridNode[][] grid = new GridNode[0][0];//Needs to be size of map array
    private PriorityQueue<GridNode> open;
    private boolean[][] closed;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;

    /**
     * Construct a new SmartTargetingEnemy().
     *
     * @param position value to set a default point2D object.
     */
    public SmartTargetingEnemy(Point2D position) {
        this.position = position;
        try {
            image = new Image(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    @Override
    public Point2D getNextMove() {
        grid = new GridNode[Map.getInstance().getCellArray().length][Map.getInstance().getCellArray()[0].length];
        closed = new boolean[Map.getInstance().getCellArray().length][Map.getInstance().getCellArray()[0].length];

        open = new PriorityQueue<>((Object o1, Object o2) -> {
            GridNode c1 = (GridNode) o1;
            GridNode c2 = (GridNode) o2;
            return Integer.compare(c1.getFinalCost(), c2.getFinalCost());
        });

        setStartCell((int) position.getY(), (int) position.getX());
        setEndCell((int) Map.getInstance().getPlayer().getPosition().getY(), (int) Map.getInstance().getPlayer().getPosition().getX());

        for (int i = 0; i < Map.getInstance().getCellArray().length; ++i) {
            for (int j = 0; j < Map.getInstance().getCellArray()[0].length; ++j) {
                grid[i][j] = new GridNode(i, j);
                grid[i][j].setHeuristicCost(Math.abs(i - goalX) + Math.abs(j - goalY));
            }
        }

        grid[startX][startY].setFinalCost(0);

        for (int i = 0; i < Map.getInstance().getCellArray().length; i++) {
            for (int j = 0; j < Map.getInstance().getCellArray()[0].length; j++) {
                if (Map.getInstance().getCellArray()[i][j].getSymbol().equals("#")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("D")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("K")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("F")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("W")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("T")
                        || Map.getInstance().getCellArray()[i][j].getSymbol().equals("G")) {
                    grid[i][j] = null;
                }
            }
        }

        //Display initial map
        /*System.out.println("Grid: ");
        for (int i = 0; i < Map.getInstance().getCellArray().length; ++i) {
            for (int j = 0; j < Map.getInstance().getCellArray()[0].length; ++j) {
                if (i == startX && j == startY) System.out.print("SO  "); //Source
                else if (i == goalX && j == goalY) System.out.print("DE  "); //Goal
                else if (grid[i][j] != null) System.out.printf("%-3d ", 0);
                else System.out.print("BL  ");
            }
            System.out.println();
        }*/

        AStar();

        /*System.out.println("\nScores for cells: ");
        for (int i = 0; i < Map.getInstance().getCellArray().length; ++i) {
            for (int j = 0; j < Map.getInstance().getCellArray().length; ++j) {
                if (grid[i][j] != null) System.out.printf("%-3d ", grid[i][j].getFinalCost());
                else System.out.print("BL  ");
            }
            System.out.println();
        }*/

        if (closed[goalX][goalY]) {
            //Trace back the path
            ArrayList<Point2D> path = new ArrayList();
            GridNode current = grid[goalX][goalY];
            while (current.getParent() != null) {
                path.add(new Point2D(current.getCellX(), current.getCellY()));
                current = current.getParent();
            }
            double distX = path.get((path.size() - 1)).getX() - startX;
            double distY = path.get((path.size() - 1)).getY() - startY;
            return (new Point2D(distY, distX));
        } else {
            return (new Point2D(0, 0));
        }
    }

    /**
     * @param x
     * @param y
     */
    public void setStartCell(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    /**
     * @param x
     * @param y
     */
    public void setEndCell(int x, int y) {
        this.goalX = x;
        this.goalY = y;
    }

    /**
     *
     */
    public void AStar() {
        open.add(grid[startX][startY]);

        GridNode current;

        while (true) {
            current = open.poll();
            if (current == null) {
                break;
            }
            closed[current.getCellX()][current.getCellY()] = true;
            if (current.equals(grid[goalX][goalY])) {
                return;
            }

            GridNode cell;
            if (current.getCellX() - 1 >= 0) {
                cell = grid[current.getCellX() - 1][current.getCellY()];
                checkAndUpdateCost(current, cell, current.getFinalCost() + V_H_COST);

                if (current.getCellY() - 1 >= 0) {
                    cell = grid[current.getCellX() - 1][current.getCellY() - 1];
                    checkAndUpdateCost(current, cell, current.getFinalCost() + DIAGONAL_COST);
                }
                if (current.getCellY() + 1 < grid[0].length) {
                    cell = grid[current.getCellX() - 1][current.getCellY() + 1];
                    checkAndUpdateCost(current, cell, current.getFinalCost() + DIAGONAL_COST);
                }
            }

            if (current.getCellY() - 1 >= 0) {
                cell = grid[current.getCellX()][current.getCellY() - 1];
                checkAndUpdateCost(current, cell, current.getFinalCost() + V_H_COST);
            }
            if (current.getCellY() + 1 < grid[0].length) {
                cell = grid[current.getCellX()][current.getCellY() + 1];
                checkAndUpdateCost(current, cell, current.getFinalCost() + V_H_COST);
            }
            if (current.getCellX() + 1 < grid.length) {
                cell = grid[current.getCellX() + 1][current.getCellY()];
                checkAndUpdateCost(current, cell, current.getFinalCost() + V_H_COST);

                if (current.getCellY() - 1 >= 0) {
                    cell = grid[current.getCellX() + 1][current.getCellY() - 1];
                    checkAndUpdateCost(current, cell, current.getFinalCost() + DIAGONAL_COST);
                }
                if (current.getCellY() + 1 < grid[0].length) {
                    cell = grid[current.getCellX() + 1][current.getCellY() + 1];
                    checkAndUpdateCost(current, cell, current.getFinalCost() + DIAGONAL_COST);
                }
            }
        }
    }

    /**
     * @param current
     * @param cell
     * @param cost
     */
    private void checkAndUpdateCost(GridNode current, GridNode cell, int cost) {
        if (cell == null || closed[cell.getCellX()][cell.getCellY()]) return;
        int t_final_cost = cell.getHeuristicCost() + cost;

        boolean inOpen = open.contains(cell);
        if (!inOpen || t_final_cost < cell.getFinalCost()) {
            cell.setFinalCost(t_final_cost);
            cell.setParent(current);
            if (!inOpen) open.add(cell);
        }
    }

    /**
     * @param position
     */
    @Override
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * @return
     */
    @Override
    public Point2D getPosition() {
        return position;
    }

    /**
     * @param pos
     */
    @Override
    public void updatePosition(Point2D pos) {
        this.position = new Point2D(position.getX() + pos.getX(), position.getY() + pos.getY());
    }

    /**
     * @return
     */
    @Override
    public Image getImage() {
        try {
            image = new Image(new FileInputStream(filePath));
            return image;
        } catch (Exception e) {
            System.out.println("Could not load smart enemy image!");
            return null;
        }
    }

    /**
     * @return
     */
    public String toString() {
        return "Enemy " + (int) position.getY() + " " + (int) position.getX() + " ST";
    }
}
