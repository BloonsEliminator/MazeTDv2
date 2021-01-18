import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {
	static final int dpWidth = 800;
	static final int dpHeight = 800;
	static final Color dpBG = new Color(215, 255, 215);
	static BufferedImage mapImage;
	static final int delay = 0;
	static int fps = 200;

	static final int gridSize = 40;
	static final Tile start = new Tile(0, 4, 1);
	static final Tile goal = new Tile(39, 33, 1);
	static final Tile[] checkpoints = { new Tile(7, 4, 1), new Tile(7, 21, 1), new Tile(31, 21, 1), new Tile(31, 4, 1),
			new Tile(19, 4, 1), new Tile(19, 33, 1) };
	static Grid grid = new Grid(gridSize, start, goal, checkpoints);
	static ArrayList<LinkedList<Tile>> segmentedPath = new ArrayList<LinkedList<Tile>>();
	static LinkedList<Tile> singlePath = new LinkedList<Tile>();

	static int gameMode = -1;
	static boolean inBattle = false;
	static NewTower newTower;
	static Tower selectedTower;
	static Enemy selectedEnemy;
	static int maxTowers = 5;
	static HashMap<Tile, Tower> liveTowerMap = new HashMap<Tile, Tower>(); // aka towers that actually attack
	static HashMap<Tile, Tower> deadTowerMap = new HashMap<Tile, Tower>(); // aka walls
	static HashMap<Tile, Tower> tempTowerMap = new HashMap<Tile, Tower>();
	static Enemy[] enemyArr = new Enemy[10];

	int repicks = 1;
	int[] tierChances = { 100, 0, 0 }; // t1, t2, t3
	boolean countRealTime = false; // time trial boolean
	long realTime = 0; // time trial
	static long repaintTime = 0; // paintComponent
	static int level, lives, time;
	static double score;
	static int timerCount = 0; // time in battle
	static boolean mouseInMap = false;

	JPanel inputScorePanel = new JPanel();
	JTextField inputScoreField = new JTextField();
	JLabel inputScoreLabel = new JLabel();

	public DrawingPanel() {
		inputScorePanel.setLayout(new BoxLayout(inputScorePanel, BoxLayout.PAGE_AXIS));
		inputScorePanel.add(inputScoreLabel);
		inputScorePanel.add(new JLabel("Please enter the username to be displayed with your score (1-10 chars):"));
		inputScoreField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (inputScoreField.getText().length() >= 10)
					e.consume();
			}
		});
		inputScorePanel.add(inputScoreField);

		setDoubleBuffered(true);
		try {
			mapImage = ImageIO.read(new File("map.png"));
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}

		grid.setCheckpoints(checkpoints);
		newTower = new NewTower();

		level = 0;
		lives = 0;
		time = 0;
		score = 0;

		Timer timer = new Timer(delay, new TimerListener());
		timer.setDelay(delay);
		timer.setRepeats(true);
		timer.start();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (newTower.isVisible()) {
					int col = newTower.getCol();
					int row = newTower.getRow();
					if (grid.canBuild(col, row)) {
						tempTowerMap.put(new Tile(col, row, 2), generateTower(col, row)); // places new tower if tiles not occupied and not blocking path
						grid.setSquareTypes(col, row, 2);
						if (!hasPath(start, goal, grid, checkpoints)) {
							grid.setSquareTypes(col, row, 0);
							tempTowerMap.remove(newTower.getTile());
							JOptionPane.showMessageDialog(null, "Blocking the path or this tile is already occupied!",
									"CANNOT BUILD THERE!!!", JOptionPane.WARNING_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Blocking the path or this tile is already occupied!",
								"CANNOT BUILD THERE!!!", JOptionPane.WARNING_MESSAGE);
					}
					if (tempTowerMap.size() == maxTowers) { // max number of temp towers placed
						if (repicks > 0) {
							OptionPanel.placeButton.getButton().setText("Repick (T)owers: 1 Repick Left");
							repicks = 0;
						} else {
							OptionPanel.placeButton.getButton().setVisible(false);
						}
						OptionPanel.placeButton.removeHighlight();
						newTower.setVisible(false);
					}
				}
				int col = (int) e.getX() / 20;
				int row = (int) e.getY() / 20;
				setSelected(new Tile(col, row)); // sets tower to tile clicked

				OptionPanel.combineButton.getButton().setVisible(false);
				OptionPanel.keepButton.getButton().setVisible(false);
				selectedEnemy = null;
				if (selectedTower == null) {
					OptionPanel.selectedPanel.setVisible(false); // selects enemy
					for (Enemy enemy : enemyArr) {
						if (enemy != null) {
							double halfW = Enemy.width / 2 + 10;
							double halfH = Enemy.height / 2 + 10;
							if (e.getX() < enemy.getX() + halfW / 2 && e.getX() > enemy.getX() - halfW
									&& e.getY() < enemy.getY() + halfH && e.getY() > enemy.getY() - halfH) {
								selectedEnemy = enemy;
								OptionPanel.selectedPanel.setWallStat(false);
								OptionPanel.selectedPanel.setEnemyStats(enemy.getHpFull(), enemy.getHpLeft(),
										enemy.getArrPos(), enemy.getTypeName(), enemy.getSpd(), enemy.getCost());
								OptionPanel.selectedPanel.setVisible(true);
								break;
							}
						}
					}
				} else {
					OptionPanel.selectedPanel.setVisible(true);
					if (selectedTower.isTower()) { // show temp/live tower stats
						OptionPanel.selectedPanel.setWallStat(false);
						OptionPanel.selectedPanel.updateStats(selectedTower.getColorName(), selectedTower.getEffect(),
								selectedTower.getTier(), selectedTower.getAtk(), selectedTower.getCd(),
								selectedTower.getRange(), selectedTower.getKills());
						if (tempTowerMap.size() == maxTowers && tempTowerMap.containsKey(selectedTower.getTile())) { // show keep tower button if temp tower selected
							OptionPanel.keepButton.getButton().setText("Keep Towe(R)");
							OptionPanel.keepButton.getButton().setVisible(true);
							if (hasDuplicate(tempTowerMap.values()) != null)
								OptionPanel.combineButton.getButton().setVisible(true);
							Game.optionPanel.repaint();
						}
					} else { // show dead tower stats (wall stats)
						OptionPanel.selectedPanel.setWallStat(true);
						OptionPanel.keepButton.getButton().setText("(R)emove Wall");
						OptionPanel.keepButton.getButton().setVisible(true);
					}
				}
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseInMap = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseInMap = false;
				repaint();
			}
		});

		// updates
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				newTower.updatePos(e.getX(), e.getY());
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				newTower.updatePos(e.getX(), e.getY());
				repaint();
			}
		});
	}

	/*
	 * Purpose: Sets the size of the DrawingPanel (JPanel)
	 * Parameters: None
	 * Return: Dimension of the DrawingPanel
	 */
	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		return new Dimension(dpWidth, dpHeight);
	}

	// Overrides paintComponent to take Graphics2D instead of just Graphics as its parameter
	@Override
	protected void paintComponent(Graphics g) {
		paintComponent((Graphics2D) g);
	}

	/*
	 * Purpose: Paints the game screen
	 * Parameters: Graphics2D
	 * Return: void
	 */
	protected void paintComponent(Graphics2D g) {
		super.paintComponent(g);
		g.drawImage(mapImage, 0, 0, null); // map image

		g.setColor(Color.GRAY);
		for (Tower tower : deadTowerMap.values()) { // dead towers (walls)
			if (tower != null)
				tower.draw(g);
		}

		for (Tower tower : liveTowerMap.values()) { // live towers
			if (tower != null)
				tower.draw(g);
		}

		g.setStroke(new BasicStroke(3));
		for (Tower tower : tempTowerMap.values()) { // temporary towers
			if (tower != null)
				tower.draw(g);
			g.setColor(Color.PINK);
			g.drawRect(tower.getTile().getX() + 2, tower.getTile().getY() + 1, tower.getWidth() - 3,
					tower.getWidth() - 2);
		}

		if (selectedTower != null) {
			Tile sTile = selectedTower.getTile(); // selected tower highlight/range
			int rng = selectedTower.getRange();
			int w = selectedTower.getWidth();
			g.setColor(Color.MAGENTA);
			g.drawRect(sTile.getX(), sTile.getY(), w, w);
			g.setColor(selectedTower.getColor());
			g.drawOval(sTile.getTowerCenterX() - rng, sTile.getTowerCenterY() - rng, rng * 2, rng * 2);
		}

		if (inBattle) {
			for (int i = enemyArr.length - 1; i >= 0; i--) { // enemies
				if (enemyArr[i] != null)
					enemyArr[i].draw(g);
			}
			for (Tower tower : liveTowerMap.values()) {
				if (tower != null && !tower.getProjectiles().isEmpty()) { // projectiles
					for (Projectile proj : tower.getProjectiles()) {
						proj.draw(g);
					}
				}
			}
		} else if (newTower.isVisible() && mouseInMap) { // projection of new towere
			g.setColor(Color.GRAY);
			newTower.draw(g);
		}
	}

	/*
	 * Purpose: Updates all game variables
	 * Parameters: None
	 * Return: void
	 */
	public void updateGame() {
		timerCount++;
		if (timerCount >= 100) { // "time in battle" stat
			time++;
			timerCount = 0;
		}
		boolean enemiesLeft = false;
		for (Enemy enemy : enemyArr) { // moves all enemies
			if (enemy != null) {
				enemy.move();
				enemiesLeft = true;
			}
		}

		for (Tower tower : liveTowerMap.values()) { // moves all projectiles
			if (!tower.getProjectiles().isEmpty()) {
				for (Projectile proj : tower.getProjectiles()) {
					proj.move();
				}
				if (tower.getProjectiles().peek().getHitStatus()) {
					Projectile hitter = tower.getProjectiles().pollFirst();
					if (hitter.willHit()) {
						if (tower.getColorPos() == 4) { // splash damage if tower is type 4
							for (Enemy enemy : enemyArr) {
								if (enemy != null && enemy.getX() > 0 && enemy.getX() < 800
										&& getDist(enemy.getX(), enemy.getY(), hitter.getX(), hitter.getY()) < 40
												* Math.pow(1.5, hitter.getCannonTier())) {
									enemy.damaged(tower.getColorPos(), tower.getTier(), tower);
								}
							}
						} else {
							hitter.getTarget().damaged(tower.getColorPos(), tower.getTier(), tower);
						}
					}
				}
			}
			tower.updateCd(10);
			if (tower.isCool()) {
				int numShots = 0;
				for (Enemy enemy : enemyArr) { // spawns new projectile for towers
					if (enemy != null) {
						if (enemy.getX() > 0 && enemy.getX() < 800 && !enemy.isDead()
								&& getDist(tower.getX(), tower.getY(), enemy.getX(), enemy.getY()) < tower.getRange()) {
							tower.addProjectile(enemy);
							numShots++;
							if (tower.getColorPos() == 5) { // multi targeting
								if (tower.getTier() == 3)
									continue;
								else if (numShots < tower.getTier() + 2) {
									continue;
								}
							}
							break;
						}
					}
				}
			}
		}

		if (!enemiesLeft) { // resets variables for next level
			inBattle = false;
			fps = 200;
			Game.setHyperSpeedText(false);
			repicks = 1;

			for (Tower tower : liveTowerMap.values()) {
				tower.getProjectiles().clear();
			}
			repaint();

			if ((gameMode == 1 && level == 40) || (gameMode == 2 && level == 41)) { // checks if game over
				gameOver(true);
				return;
			} else if (lives <= 0) {
				if (gameMode == 0 && level >= 40)
					gameOver(true);
				else
					gameOver(false);
				return;
			}

			level++;
			if (gameMode == 0) // survival, level++
				updateScore(1);

			if (level % 2 == 0) {
				if (tierChances[1] < 20 || (tierChances[2] == 5 && tierChances[1] < 35) // tier chances increase every other level
						|| (tierChances[2] == 10 && tierChances[1] < 45)
						|| (tierChances[2] == 20 && tierChances[1] < 50)
						|| (tierChances[2] == 30 && tierChances[1] < 70)) {
					tierChances[0] -= 5;
					tierChances[1] += 5;
				} else if (tierChances[0] > 0) {
					tierChances[0] -= 5;
					tierChances[2] += 5;
				} else if (tierChances[1] > 0) {
					tierChances[1] -= 5;
					tierChances[2] += 5;
				}
				OptionPanel.statPanel.updateChances(tierChances[0], tierChances[1], tierChances[2]);
			}

			OptionPanel.placeButton.getButton().setText("Place (T)owers: 1 Repick Left");
			OptionPanel.placeButton.getButton().setVisible(true);
			Game.optionPanel.repaint();
		}
		OptionPanel.statPanel.updateStats(gameMode, level, Enemy.types[(level - 1) % 5], lives, time, score);
	}

	/*
	 * Purpose: TimerListener calls the overridden actionPerformed method at a delay of 0 to animate the game smoothly
	 * Parameters: ActionEvent (TimerListener)
	 * Return: void
	 */
	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (countRealTime) {
				if (System.currentTimeMillis() - realTime >= 1000) { // score counts up every second in time trial mode
					updateScore(1);
					realTime = System.currentTimeMillis();
				}
			}
			if (inBattle) {
				updateGame();
				if (System.nanoTime() - repaintTime >= 1000000000 / fps) { // ex. 50 fps = repaint every 1/50th of a second
					repaintTime = System.nanoTime(); // 200 fps = repaint every 1/200th of a second
					repaint();
				}
			}
		}
	}

	/*
	 * Purpose: Resets all game variables and sets the new game mode
	 * gameMode = 0: SURVIVAL
	 * gameMode = 1: TIME TRIAL
	 * gameMode = 2: LAST STAND
	 * 
	 * Parameters: int of the game mode and String of its name
	 * Return: void
	 */
	public void resetGame(int mode, String modeTitle) {
		gameMode = mode;
		inBattle = false;
		level = 1;
		lives = 20;
		time = 0;
		timerCount = 0;
		updateScore(-score);
		OptionPanel.statPanel.updateMode(modeTitle);
		OptionPanel.statPanel.updateStats(mode, level, "Normal", lives, time, score);
		newTower.setVisible(false);
		grid = new Grid(gridSize, start, goal, checkpoints);
		grid.setCheckpoints(checkpoints);
		segmentedPath.clear();
		singlePath.clear();
		tempTowerMap.clear();
		liveTowerMap.clear();
		deadTowerMap.clear();
		for (int i = 0; i < enemyArr.length; i++) {
			enemyArr[i] = null;
		}
		selectedTower = null;
		selectedEnemy = null;
		repicks = 1;
		OptionPanel.placeButton.getButton().setVisible(true);
		OptionPanel.placeButton.getButton().setText("Place (T)owers: 1 Repick Left");
		OptionPanel.keepButton.getButton().setVisible(false);
		OptionPanel.combineButton.getButton().setVisible(false);
		OptionPanel.selectedPanel.setVisible(false);
		tierChances[0] = 100;
		tierChances[1] = 0;
		tierChances[2] = 0;
		OptionPanel.statPanel.updateChances(tierChances[0], tierChances[1], tierChances[2]);
		if (gameMode == 1)
			countRealTime = true;
		else
			countRealTime = false;
	}

	// combines the increase of score variable and the update of the score in the stat panel
	public static void updateScore(double Score) {
		score += Score;
		OptionPanel.statPanel.updateScore(gameMode, (int) score);
		OptionPanel.statPanel.repaint();
	}

	/*
	 * Purpose: Displays the "GAME OVER" message, prompts user for a name if a highscore was achieved
	 * Parameters: boolean of whether the game was won
	 * Return: void
	 */
	public void gameOver(boolean wonGame) {
		countRealTime = false;
		Game.play(0);
		if (!wonGame) { // game was lost
			JOptionPane.showMessageDialog(null,
					"You lost all of your lives at level " + level + " with a score of " + (int) score + " in "
							+ Game.gameModes[gameMode].getText().toUpperCase()
							+ " mode! To restart, please select a new game mode.",
					"GAME OVER!!!", JOptionPane.INFORMATION_MESSAGE);
			OptionPanel.statPanel.setGameOver();
			return;
		}

		if (Game.isHighscore(gameMode, score)) {
			inputScoreLabel.setText("You placed " + Game.getScorePos(gameMode, score) + " with a score of "
					+ (int) score + " in " + Game.gameModes[gameMode].getText().toUpperCase() + " mode!"); // game was won with a new highscore
			boolean invalid = true;
			do {
				int option = JOptionPane.showConfirmDialog(null, inputScorePanel, "NEW HIGHSCORE!!!",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (option == JOptionPane.OK_OPTION) {
					if (inputScoreField.getText().length() > 0 || inputScoreField.getText().length() <= 10) {
						Game.addHighscore(gameMode, inputScoreField.getText(), score);
						invalid = false;
					} else {
						JOptionPane.showMessageDialog(null,
								"Please ensure that the username is between 1 and 10 characters in length.",
								"ERROR: INVALID USERNAME", JOptionPane.WARNING_MESSAGE);
					}
				} else
					invalid = false;
			} while (invalid);

		} else { // game was won without a new highscore
			JOptionPane.showMessageDialog(null,
					"You beat the game with a score of " + (int) score + " in "
							+ Game.gameModes[gameMode].getText().toUpperCase()
							+ " mode!\nNo new highscore was set. To restart, please select a new game mode.",
					"YOU WIN!!!", JOptionPane.INFORMATION_MESSAGE);
		}
		OptionPanel.statPanel.setGameOver();
	}

	/*
	 * Purpose: Generates a new tower with a completely random type and a random tier with weighted chances
	 * Parameters: int of row and column of the tower to be generated
	 * Return: a new Tower object
	 */
	public Tower generateTower(int col, int row) {
		int rand = (int) (100 * Math.random());
		if (rand <= tierChances[0])
			return new Tower(col, row, (int) (Math.random() * 6), 0);
		else if (rand <= tierChances[0] + tierChances[1])
			return new Tower(col, row, (int) (Math.random() * 6), 1);
		else
			return new Tower(col, row, (int) (Math.random() * 6), 2);
	}

	/*
	 * Purpose: Sets the selectedTower variable to the tower at the tile
	 * Parameters: the Tile object clicked
	 * Return: void
	 */
	public void setSelected(Tile tile) {
		for (Tower tower : tempTowerMap.values()) {
			if (tower.inTower(tile)) {
				selectedTower = tower;
				return;
			}
		}
		for (Tower tower : liveTowerMap.values()) {
			if (tower.inTower(tile)) {
				selectedTower = tower;
				return;
			}
		}
		for (Tower tower : deadTowerMap.values()) {
			if (tower.inTower(tile)) {
				selectedTower = tower;
				return;
			}
		}
		selectedTower = null;
	}

	/*
	 * Purpose: Removes a dead tower (wall) from the grid and deadTowerMap
	 * Parameters: the Tile object clicked
	 * Return: void
	 */
	public static void removeWall() {
		grid.setSquareTypes(selectedTower.getCol(), selectedTower.getRow(), 0);
		deadTowerMap.remove(selectedTower.getTile());
		selectedTower = null;
		Game.drawPanel.repaint();
	}

	/*
	 * Purpose: Determines if a tower has a duplicate in another list of towers
	 * Parameters: a list of Towers
	 * Return: the duplicate Tile found
	 */
	public static Tile hasDuplicate(Collection<Tower> towers) {
		for (Tower tower : towers) {
			if (tower.getTile() != selectedTower.getTile() && tower.getColorPos() == selectedTower.getColorPos()
					&& tower.getTier() == selectedTower.getTier())
				return tower.getTile();
		}
		return null;
	}

	/*
	 * Purpose: Sets the next wave of enemies
	 * Parameters: the Tile object clicked
	 * Return: void
	 */
	public static void sendNextEnemies() {
		if ((level - 1) % 5 == 4)
			updatePath(true);
		else
			updatePath(false);
		for (int i = 0; i < enemyArr.length; i++) {
			if (level <= 40 || gameMode == 0) {
				enemyArr[i] = new Enemy(singlePath, i, level, (level - 1) % 5);
			} else
				enemyArr[i] = new Enemy(singlePath, i, level, 5);
		}

		inBattle = true;
	}

	/*
	 * Purpose: Starts the next level
	 * Parameters: the duplicate Tile (requirement for combining towers)
	 * Return: void
	 */
	public static void startNextLevel(Tile duplicateTile) {
		if (duplicateTile != null) { // upgrades selected tower if duplicateTile is not null
			selectedTower.upgradeTier();
			tempTowerMap.get(duplicateTile).setDead();
		}
		if (selectedTower.getColorPos() == 3) { // selected tower buffs other towers' cooldown
			for (Tower tower : liveTowerMap.values()) {
				if (getDist(selectedTower.getX(), selectedTower.getY(), tower.getX(), tower.getY()) <= selectedTower
						.getRange()) {
					if (tower.getCDTier() < selectedTower.getTier()) {
						tower.setCDTier(selectedTower.getTier());
					}
				}
			}
		}
		for (Tower tower : liveTowerMap.values()) { // other towers buff selected tower's cooldown
			if (tower.getColorPos() == 3) {
				if (getDist(selectedTower.getX(), selectedTower.getY(), tower.getX(), tower.getY()) <= tower
						.getRange()) {
					if (selectedTower.getCDTier() < tower.getTier()) {
						selectedTower.setCDTier(tower.getTier());
						OptionPanel.selectedPanel.updateCdStat(selectedTower.getCd());
					}
				}
			}
		}
		liveTowerMap.put(selectedTower.getTile(), tempTowerMap.remove(selectedTower.getTile())); // add selected to live map and rest to dead map
		for (Tower tower : tempTowerMap.values()) {
			tower.setDead();
			deadTowerMap.put(tower.getTile(), tower);
		}
		if (gameMode == 2)
			updateScore(-score);
		OptionPanel.selectedPanel.updateStats(selectedTower.getColorName(), selectedTower.getEffect(),
				selectedTower.getTier(), selectedTower.getAtk(), selectedTower.getCd(), selectedTower.getRange(),
				selectedTower.getKills());
		tempTowerMap.clear();
		sendNextEnemies();
		Game.drawPanel.repaint();
	}

	/*
	 * Purpose: Executes the breadth-first search pathfinding algorithm on a given grid with a start and ending tile
	 * Parameters: the starting and ending tiles, the grid, and boolean for whether the pathfinding should ignore tower walls
	 * Return: LinkedList of all the Tiles in the path in sequential order
	 */
	public static LinkedList<Tile> bfs(Tile start, Tile goal, Grid grid, boolean ignoreType) {
		int[][] countGrid = new int[gridSize][gridSize];
		Tile[][] bfsGrid = cloneGrid(grid.getGrid());

		Queue<Tile> queue = new LinkedList<>();
		queue.add(start);
		bfsGrid[start.getCol()][start.getRow()].setExplored(true);
		countGrid[start.getCol()][start.getRow()] = 1;

		while (!queue.isEmpty()) {
			Tile current = queue.remove();
			List<Tile> adjacentTiles = current.getAdjacentTiles(bfsGrid, ignoreType);
			if (current.equals(goal)) {
				Tile backtracker = new Tile(goal.getCol(), goal.getRow()); // if current is at goal, start backtracking
				LinkedList<Tile> path = new LinkedList<Tile>();
				path.add(backtracker);
				do {
					int col = backtracker.getCol();
					int row = backtracker.getRow();

					if (row > 0 && countGrid[col][row - 1] != 0 && countGrid[col][row - 1] < countGrid[col][row])
						backtracker = new Tile(col, row - 1);

					else if (col > 0 && countGrid[col - 1][row] != 0 && countGrid[col - 1][row] < countGrid[col][row])
						backtracker = new Tile(col - 1, row);

					else if (row < 39 && countGrid[col][row + 1] != 0 && countGrid[col][row + 1] < countGrid[col][row])
						backtracker = new Tile(col, row + 1);

					else if (col < 39 && countGrid[col + 1][row] != 0 && countGrid[col + 1][row] < countGrid[col][row])
						backtracker = new Tile(col + 1, row);

					path.addFirst(backtracker);
				} while (countGrid[backtracker.getCol()][backtracker.getRow()] > 1);
				return path;
			} else if (!adjacentTiles.isEmpty()) { // adds all adjacent tiles to the queue if they exist
				queue.addAll(adjacentTiles);
				for (int i = 0; i < adjacentTiles.size(); i++) {
					int adjCol = adjacentTiles.get(i).getCol();
					int adjRow = adjacentTiles.get(i).getRow();
					countGrid[adjCol][adjRow] = countGrid[current.getCol()][current.getRow()] + 1;
					bfsGrid[adjCol][adjRow].setExplored(true);
				}
			}
		}
		return null;
	}

	/*
	 * Purpose: Determines whether the current path exists
	 * Parameters: the starting and ending tiles, the grid, and boolean for whether the pathfinding should ignore tower walls
	 * Return: true if the path exists
	 */
	public boolean hasPath(Tile start, Tile goal, Grid grid, Tile[] checkpoint) {
		if (bfs(start, checkpoints[0], grid, false) == null)
			return false;
		for (int i = 1; i < checkpoints.length; i++) {
			if (bfs(checkpoints[i - 1], checkpoints[i], grid, false) == null)
				return false;
		}
		if (bfs(checkpoints[checkpoints.length - 1], goal, grid, false) == null)
			return false;
		return true;
	}

	/*
	 * Purpose: Executes bfs on every segment of the path and updates the two Path variables
	 * Parameters: boolean of whether type should be ignored
	 * Return: void
	 */
	public static void updatePath(boolean ignoreType) {
		segmentedPath = new ArrayList<LinkedList<Tile>>(); // segmented includes all checkpoints twice due to overlap
		singlePath = new LinkedList<Tile>(); // single is a single continuous path
		LinkedList<Tile> path0 = bfs(start, checkpoints[0], grid, ignoreType);
		segmentedPath.add(path0);
		singlePath.pollLast();
		singlePath.addAll(path0);
		for (int i = 1; i < checkpoints.length; i++) {
			LinkedList<Tile> pathI = bfs(checkpoints[i - 1], checkpoints[i], grid, ignoreType);
			segmentedPath.add(pathI);
			singlePath.pollLast();
			singlePath.addAll(pathI);
		}
		LinkedList<Tile> pathLast = bfs(checkpoints[checkpoints.length - 1], goal, grid, ignoreType);
		segmentedPath.add(pathLast);
		singlePath.pollLast();
		singlePath.addAll(pathLast);
	}

	// clones a grid and returns a perfect copy of it
	public static Tile[][] cloneGrid(Tile[][] src) {
		Tile[][] target = new Tile[src.length][src[0].length];
		for (int i = 0; i < src.length; i++) {
			for (int j = 0; j < src[0].length; j++) {
				int newType = src[i][j].getType();
				target[i][j] = new Tile(i, j, newType);
			}
		}
		return target;
	}

	// finds the distance between 2 points
	public static double getDist(double x1, double y1, double x2, double y2) {
		return (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
	}
}
