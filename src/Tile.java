import java.util.ArrayList;
import java.util.List;

public class Tile {
	private int col;
	private int row;
	private int type = 0;
	// 0 = can build and enemy can pass through
	// 1 = cannot build but enemy can pass through (start, goal, checkpoints)
	// 2 = cannot build and enemy cannot pass through (towers and walls)
	private boolean explored = false;
	private boolean isHighlighted = false;

	public Tile(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public Tile(int col, int row, int type) {
		this.col = col;
		this.row = row;
		this.type = type;
	}

	/*
	 * Purpose: Determines all adjacent Tiles to this Tile depending on whether they can be explored or not
	 * Parameters: the Tile 2D array and boolean of whether walls should be ignored (flying enemies)
	 * Return: a List of all adjacent Tiles
	 */
	public List<Tile> getAdjacentTiles(Tile[][] grid, boolean ignoreType) {
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		if (col > 0 && grid[col - 1][row].canExplore(ignoreType))
			adjacentTiles.add(new Tile(col - 1, row));
		if (row > 0 && grid[col][row - 1].canExplore(ignoreType))
			adjacentTiles.add(new Tile(col, row - 1));
		if (col < 39 && grid[col + 1][row].canExplore(ignoreType))
			adjacentTiles.add(new Tile(col + 1, row));
		if (row < 39 && grid[col][row + 1].canExplore(ignoreType))
			adjacentTiles.add(new Tile(col, row + 1));
		return adjacentTiles;
	}

	/*
	 * Purpose: Determines whether a Tile is deemed explorable
	 * Parameters: boolean of whether walls should be ignored
	 * Return: true if Tile is not explored yet and is either not a wall or walls are ignored
	 */
	public boolean canExplore(boolean ignoreType) {
		if (!explored && (ignoreType || type < 2))
			return true;
		return false;
	}

	// getters and setters
	public String toString() {
		return "[" + getTileCenterX() + "," + getTileCenterY() + "]";
	}

	public void setPos(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setExplored(boolean isExplored) {
		explored = isExplored;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public int getX() {
		return col * 20;
	}

	public int getY() {
		return row * 20;
	}

	public int getTileCenterX() {
		return col * 20 + 10;
	}

	public int getTileCenterY() {
		return row * 20 + 10;
	}

	public int getTowerCenterX() {
		return col * 20 + 20;
	}

	public int getTowerCenterY() {
		return row * 20 + 20;
	}

	public int getType() {
		return type;
	}

	public boolean isExplored() {
		return explored;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlight(boolean highlight) {
		isHighlighted = highlight;
	}

	public int hashCode() {
		return col * 100 + row;
	}

	public boolean equals(Object o) {
		if (this.hashCode() == o.hashCode())
			return true;
		else
			return false;
	}
}