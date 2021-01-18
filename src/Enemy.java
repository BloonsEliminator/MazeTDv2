import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;

public class Enemy {
	private double x, y; // center of enemy
	private double dx, dy;
	static int width = 20;
	static int height = 30;
	private Color color = new Color(0, 255, 0);

	private int poisonTier = -1;
	private Tile poisonTowerTile = null;
	private int frailTier = -1;
	private int slowTier = -1;
	private double slowFactor = 1;
	private int[] counterArr = { 0, 0, 0 };
	private Color[] debuffColors = { new Color(35, 140, 20), new Color(150, 10, 10), new Color(20, 100, 180) };

	private int type = 0; // normal, speed, dodge, armour, air, change enemeyArr to enemylist linkedlist
	static String[] types = { "Normal", "Speed", "Dodge", "Armoured", "Flying", "BOSS" };
	private Color[] typeColors = { Color.WHITE, new Color(185, 255, 210), new Color(185, 240, 255),
			new Color(255, 185, 185), new Color(245, 255, 185), Color.BLACK }; // bg color of enemies based on type

	private double spd;
	private double hpFull;
	private double hpLeft;
	private double spdUp = 1;
	private double dodgeUp = 1;
	private double amrUp = 1;
	private int cost; // # of lives lost

	private int arrPos; // position in enemy list
	private LinkedList<Tile> path;
	private int pathPos = 0; // position in path LinkedList
	private Tile nextTile; // current tile that enemy is moving towards
	private Tile startTarget = new Tile(-1, 4);
	private Tile endTarget = new Tile(40, 33);
	private double displacement; // displacement from previous tile (used to predict future enemy position)
	private boolean isDead = false;

	public Enemy(LinkedList<Tile> path, int arrPos, int level, int type) {
		this.arrPos = arrPos;
		this.path = path;
		nextTile = path.get(0);
		displacement = -60 * (arrPos + 1);
		x = nextTile.getTileCenterX() - (60 * (arrPos + 1));
		y = nextTile.getTileCenterY();
		color = new Color(0, 255, 0);
		cost = 1 + level / 5;

		this.type = type;
		if (type == 1)
			spdUp = 1.5; // 20% spd boost
		else if (type == 2)
			dodgeUp = 0.8; // 10% chance to dodge
		else if (type == 3)
			amrUp = 0.8; // blocks 10% of damage

		spd = spdUp * (0.5 + 0.3 * (level / 10));

		if (type == 5) {
			hpFull = 1;
		} else if (type == 4) { // flying enemies: half speed and much lower hp
			if (level <= 40)
				hpFull = Math.pow(level, 2) * 4;
			else
				hpFull = level * 200;
			spd *= 0.5;
		} else {
			hpFull = Math.pow(level, 3);
		}
		hpLeft = hpFull;

		dx = spd;
		dy = 0;
	}

	/*
	 * Purpose: Draws the enemy with:
	 * bg color based on type
	 * outline color based on hpLeft
	 * number based on initial position in the enemy array
	 * green, red, and blue horizontal stripes based on poison, frail, and slow debuffs respectively
	 * 
	 * Parameters: Graphics2D object
	 * Return: void
	 */
	public void draw(Graphics2D g) {
		g.setStroke(new BasicStroke(3));
		g.setColor(typeColors[type]);
		int drawX = (int) x - width / 2;
		int drawY = (int) y - height / 2;
		if (drawX < 800) {
			g.fill3DRect(drawX, drawY, width, height, true);
			for (int i = 0; i < counterArr.length; i++) {
				if (counterArr[i] > 0) {
					g.setColor(debuffColors[i]);
					g.fill3DRect(drawX, drawY + ((height * i) / 3), width, height / 3, true);
				}
			}
		} else if (drawX + width > 800) {
			g.fill3DRect(drawX, drawY, 800 - drawX, height, true);
			for (int i = 0; i < counterArr.length; i++) {
				if (counterArr[i] > 0) {
					g.setColor(debuffColors[i]);
					g.fill3DRect(drawX, drawY + height * i / 3, 800 - drawX, height / 3, true);
				}
			}
		}
		g.setColor(color);
		g.drawRect(drawX, drawY, width, height);

		g.setFont(new Font("SansSerif", Font.PLAIN, 16));
		g.setStroke(new BasicStroke(5));

		if (type == 5)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.BLACK);
		if (arrPos < 9) {
			g.drawString("" + (arrPos + 1), (int) x - 5, (int) y + 6);
		} else {
			g.drawString("" + (arrPos + 1), (int) x - 10, (int) y + 5);
		}

	}

	/*
	 * Purpose: Sets the next tile that the enemy will move towards, calculates any posion dmg,
	 * determines if the poison is lethal, decreases lives if it reaches end target
	 * Parameters: None
	 * Return: void
	 */
	public void setNextTile() {
		if (counterArr[0] > 0) {
			counterArr[0]--;

			if (!isDead) { // calculates poison dmg, updates hp and score
				if (type == 5) {
					hpLeft += Tower.towerStats[poisonTier][0][0] * (poisonTier + 1) * 0.05 * amrUp;
				} else {
					hpLeft -= Tower.towerStats[poisonTier][0][0] * (poisonTier + 1) * 0.05 * amrUp;
					if (hpLeft / hpFull >= 0 && hpLeft / hpFull <= 1)
						updateColor();
				}
				if (DrawingPanel.gameMode == 2) {
					DrawingPanel.updateScore(Tower.towerStats[poisonTier][0][0] * (poisonTier + 1) * 0.05 * amrUp);
				}
			}
			if (DrawingPanel.selectedEnemy != null && DrawingPanel.selectedEnemy.getArrPos() == arrPos)
				OptionPanel.selectedPanel.updateHp(hpFull, hpLeft);

			// exits method if killed by poison dmg
			if (hpLeft <= 0 && DrawingPanel.enemyArr[arrPos] != null) {
				Tower poisonTower = DrawingPanel.liveTowerMap.get(poisonTowerTile);
				poisonTower.addKill();
				if (DrawingPanel.selectedTower != null && DrawingPanel.selectedTower.getTile() == poisonTower.getTile())
					OptionPanel.selectedPanel.updateKillStat(poisonTower.getKills());
				else if (DrawingPanel.selectedEnemy != null && DrawingPanel.selectedEnemy.getArrPos() == arrPos)
					OptionPanel.selectedPanel.setVisible(false);
				setDead();
				removeSelf();
				return;
			}

			if (counterArr[0] == 0) { // resets poison tier
				poisonTier = -1;
				poisonTowerTile = null;
			}
		}
		if (counterArr[1] > 0) { // resets frail tier
			counterArr[1]--;
			if (counterArr[1] == 0) {
				frailTier = -1;
			}
		}
		if (counterArr[2] > 0) { // resets frail tier
			counterArr[2]--;
			if (counterArr[2] == 0) {
				slowTier = -1;
				slowFactor = 1;
				if (DrawingPanel.selectedEnemy != null && DrawingPanel.selectedEnemy.getArrPos() == arrPos)
					OptionPanel.selectedPanel.updateSpeed(spd * slowFactor);
			}
		}

		if (nextTile == endTarget) { // enemy reached the end
			DrawingPanel.lives -= cost;
			setDead();
			removeSelf();
			return;
		}

		pathPos++;
		if (pathPos >= path.size()) { // sets next target to off screen if on the last tile
			nextTile = endTarget;
			dx = spd;
			dy = 0;
		} else {
			nextTile = path.get(pathPos);
			if (nextTile.getTileCenterX() > x) {
				dx = spd;
				dy = 0;
			}
			if (nextTile.getTileCenterX() < x) {
				dx = -spd;
				dy = 0;
			}
			if (nextTile.getTileCenterY() > y) {
				dy = spd;
				dx = 0;
			}
			if (nextTile.getTileCenterY() < y) {
				dy = -spd;
				dx = 0;
			}
		}
		displacement = 0;
	}

	/*
	 * Purpose: Moves the enemy based on its rate of change (dx or dy) and its current position (x and y)
	 * Parameters: None
	 * Return: void
	 */
	public void move() {
		if ((dx > 0 && x + dx > nextTile.getTileCenterX()) || (dx < 0 && x + dx < nextTile.getTileCenterX())) {
			x = nextTile.getTileCenterX();
			setNextTile();
		} else if ((dy > 0 && y + dy > nextTile.getTileCenterY()) || (dy < 0 && y + dy < nextTile.getTileCenterY())) {
			y = nextTile.getTileCenterY();
			setNextTile();
		} else {
			x += dx * slowFactor;
			y += dy * slowFactor;
			displacement += dx * slowFactor;
			displacement += dy * slowFactor;
		}
	}

	/*
	 * Purpose: Calculates damage dealt to enemy and whether or not it was lethal
	 * Parameters: type of tower, tier of tower, and the Tower itself
	 * Return: void
	 */
	public void damaged(int colorPos, int tier, Tower tower) {
		if (counterArr[1] > 0) {
			if (type == 5) { // accumulates hp as a score counter if type 5 (BOSS)
				hpLeft += Tower.towerStats[tier][(int) (colorPos / 3)][0] * 0.05 * frailTier * amrUp;
			} else {
				hpLeft -= Tower.towerStats[tier][(int) (colorPos / 3)][0] * 0.05 * frailTier * amrUp;
			}
			if (DrawingPanel.gameMode == 2) {
				DrawingPanel.updateScore(Tower.towerStats[tier][(int) (colorPos / 3)][0] * 0.05 * frailTier * amrUp);
			}
		}

		if (type == 5) { // accumulates hp as a score counter if type 5 (BOSS)
			hpLeft += Tower.towerStats[tier][(int) (colorPos / 3)][0] * amrUp; // DAMAGE
		} else {
			hpLeft -= Tower.towerStats[tier][(int) (colorPos / 3)][0] * amrUp; // DAMAGE
		}

		if (DrawingPanel.gameMode == 2) {
			DrawingPanel.updateScore(Tower.towerStats[tier][(int) (colorPos / 3)][0] * amrUp);
		}

		if (DrawingPanel.selectedEnemy != null && DrawingPanel.selectedEnemy.getArrPos() == arrPos)
			OptionPanel.selectedPanel.updateHp(hpFull, hpLeft);

		// calculates poison, frail, and slow debuffs
		if (colorPos == 0) {
			if (counterArr[0] < 5 * tier + 3) {
				counterArr[0] = 5 * tier + 3;
				poisonTowerTile = tower.getTile();
			}
			if (poisonTier < tier) {
				poisonTier = tier;
			}
		}
		if (colorPos == 1) {
			if (counterArr[1] < 5 * tier + 3)
				counterArr[1] = 5 * tier + 3;
			if (frailTier < tier) {
				frailTier = tier;
			}
		}
		if (colorPos == 2) {
			if (counterArr[2] < 5 * tier + 3)
				counterArr[2] = 5 * tier + 3;
			if (slowTier < tier) {
				slowTier = tier;
				slowFactor = 1 - (0.1 * (slowTier + 1));
				if (DrawingPanel.selectedEnemy != null && DrawingPanel.selectedEnemy.getArrPos() == arrPos)
					OptionPanel.selectedPanel.updateSpeed(spd * slowFactor);
			}
		}
		if (DrawingPanel.enemyArr[arrPos] != null) {
			if (hpLeft <= 0) {
				tower.addKill();
				if (DrawingPanel.selectedTower != null && DrawingPanel.selectedTower.getTile() == tower.getTile()) // updates tower kills
					OptionPanel.selectedPanel.updateKillStat(tower.getKills());
				setDead();
				removeSelf();
				return;
			}
			if (type != 5 && hpLeft / hpFull >= 0 && hpLeft / hpFull <= 1)
				updateColor();
		}
	}

	/*
	 * Purpose: Updates the color of the enemy outline based on hpLeft
	 * Parameters: None
	 * Return: void
	 */
	public void updateColor() {
		double hpFracLeft = hpLeft / hpFull;
		if (hpFracLeft >= 0.5) {
			hpFracLeft = 1 - ((hpFracLeft - 0.5) * 2);
			color = new Color((int) (255 * hpFracLeft), 255, 0);
		} else {
			hpFracLeft = hpFracLeft * 2;
			color = new Color(255, (int) (255 * hpFracLeft), 0);
		}
	}

	// getters and setters
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public double getSpd() {
		return spd;
	}

	public double getSlowTier() {
		return slowTier;
	}

	public Tile getNextTile() {
		return nextTile;
	}

	public double getHpFull() {
		return hpFull;
	}

	public double getHpLeft() {
		return hpLeft;
	}

	public int getArrPos() {
		return arrPos;
	}

	public void setArrPos(int arrPos) {
		this.arrPos = arrPos;
	}

	public int getType() {
		return type;
	}

	public String getTypeName() {
		return types[type];
	}

	public double getDodgeUp() {
		return dodgeUp;
	}

	public double getAmrUp() {
		return amrUp;
	}

	public int getCost() {
		return cost;
	}

	public int getPathPos() {
		return pathPos;
	}

	public LinkedList<Tile> getPath() {
		return path;
	}

	public Tile getStartTarget() {
		return startTarget;
	}

	public Tile getEndTarget() {
		return endTarget;
	}

	public double getDisplacement() {
		return displacement;
	}

	public void setDead() {
		isDead = true;
	}

	public void removeSelf() {
		DrawingPanel.enemyArr[arrPos] = null;
	}

	public boolean isDead() {
		return isDead;
	}

	public static double getDist(double x1, double y1, double x2, double y2) {
		return (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
	}
}