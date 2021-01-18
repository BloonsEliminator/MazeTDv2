public class Grid {
	private Tile[][] Grid;
	
	public Grid(int length, Tile start, Tile goal, Tile[] checkpoints) {
		Grid = new Tile[length][length];
		for (int i = 0; i < Grid.length; i++) {
			for (int j = 0; j < Grid[0].length; j++) {
				Grid[i][j] = new Tile(j,i);
			}
		}
		// start and goal tiles
		Grid[start.getCol()][start.getRow()].setType(1);
		Grid[goal.getCol()][goal.getRow()].setType(1);
	}
	
	public void setCheckpoints(Tile[] checkpoints) {
		for (int i = 0; i < checkpoints.length; i++) {
			int col = checkpoints[i].getCol();
			int row = checkpoints[i].getRow();
			setSquareTypes(col,row,1);
			if (i != 1)
				setSquareTypes(col - 2, row, 1);
			if (i != 2)
				setSquareTypes(col + 2, row, 1);
			if (i != 5)
				setSquareTypes(col, row + 2, 1);
			if (i == 1 || i == 2 || i == 5)
				setSquareTypes(col, row - 2, 1);
		}
	}
	
	public void setSquareTypes(int col, int row, int type) {
		Grid[col][row].setType(type);
		Grid[col+1][row].setType(type);
		Grid[col][row+1].setType(type);
		Grid[col+1][row+1].setType(type);
	}
	
	public boolean canBuild(int col, int row) {
		if (col < 0 || col > 38 || row < 0 || row > 38)
			return false;
		if (Grid[col][row].getType() > 0
				|| Grid[col+1][row].getType() > 0
				|| Grid[col][row+1].getType() > 0
				|| Grid[col+1][row+1].getType() > 0)
			return false;
		return true;
	}
	
	public Tile[][] getGrid(){
		return Grid;
	}
}