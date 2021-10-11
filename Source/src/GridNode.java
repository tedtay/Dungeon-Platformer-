/**
 * Class represents a node in A* algorithm pathfinder.
 */
public class GridNode {
    private int heuristicCost = 0; //Heuristic cost
    private int finalCost = 0; //G+H
    private int cellX;
    private int cellY;
    private GridNode parent;

    /**
     * @param x
     * @param y
     */
    GridNode(int x, int y) {
        this.cellX = x;
        this.cellY = y;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "[" + this.cellX + ", " + this.cellY + "]";
    }

    /**
     * @return
     */
    public int getHeuristicCost() {
        return heuristicCost;
    }

    /**
     * @param heuristicCost
     */
    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    /**
     * @return
     */
    public int getFinalCost() {
        return finalCost;
    }

    /**
     * @param finalCost
     */
    public void setFinalCost(int finalCost) {
        this.finalCost = finalCost;
    }

    /**
     * @return
     */
    public int getCellX() {
        return cellX;
    }

    /**
     * @param cellX
     */
    public void setCellX(int cellX) {
        this.cellX = cellX;
    }

    /**
     * @return
     */
    public int getCellY() {
        return cellY;
    }

    /**
     * @param cellY
     */
    public void setCellY(int cellY) {
        this.cellY = cellY;
    }

    /**
     * @return
     */
    public GridNode getParent() {
        return parent;
    }

    /**
     * @param parent
     */
    public void setParent(GridNode parent) {
        this.parent = parent;
    }
}
