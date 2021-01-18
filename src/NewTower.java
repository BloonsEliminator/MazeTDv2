import java.awt.Graphics2D;

public class NewTower {
	private int width = 40;
	private int towerCol;
	private int towerRow;
	private Tile tile; // at top left corner of Tower
	private boolean isVisible;

	public NewTower() {
		tile = new Tile(0, 0, 2);
	}

	/*
	 * Purpose: Updates the position of the newTower
	 * Parameters: current x and y coordinates of the mouse
	 * Return: void
	 */
	public void updatePos(int mouseX, int mouseY) {
		if (mouseX <= width / 4)
			towerCol = 0;
		else if (mouseX >= DrawingPanel.dpWidth - width / 4)
			towerCol = DrawingPanel.dpWidth / 20 - 2;
		else
			towerCol = (mouseX - 10) / 20;

		if (mouseY <= width / 4)
			towerRow = 0;
		else if (mouseY >= DrawingPanel.dpHeight - width / 4)
			towerRow = DrawingPanel.dpHeight / 20 - 2;
		else
			towerRow = (mouseY - 10) / 20;
		tile.setPos(towerCol, towerRow);
	}

	/*
	 * Purpose: Draws the newTower
	 * Parameters: Graphics2D object
	 * Return: void
	 */
	public void draw(Graphics2D g) {
		g.fill3DRect(tile.getX(), tile.getY(), width, width, true);
	}

	// getters and setters
	public int getWidth() {
		return width;
	}

	public int getCol() {
		return towerCol;
	}

	public int getRow() {
		return towerRow;
	}

	public Tile getTile() {
		return tile;
	}

	public void setVisible(boolean vis) {
		isVisible = vis;
	}

	public boolean isVisible() {
		return isVisible;
	}
}