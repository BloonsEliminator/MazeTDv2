import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Deque;
import java.util.LinkedList;

public class Tower {
	private int width = 40;
	private Tile tile; // at top left corner of Tower
	private boolean isTower = true;

	//@formatter:off
	static final int[][][] towerStats = { {{ 10, 1000, 120 }, { 20, 1000, 100 }}, // green, orange, and blue towers have 1st column stats
										  {{ 30, 1000, 150 }, { 50, 1000, 130 }}, // cyan, red, and yellow towers have 2nd column stats
										  {{ 80, 900, 180 }, { 130, 900, 160 }},
										  {{ 180, 800, 220 }, { 300, 800, 200 }}};
	//@formatter:on

	static final String[] colorNames = { "Green", "Orange", "Blue", "Cyan", "Red", "Yellow" };
	static final Color[] colors = { Color.GREEN, Color.ORANGE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW };
	static final String[] effects = { "Poison Debuff", "Frail Debuff", "Slow Debuff", "Cooldown Buff", "Splash Cannon",
			"Multi Targeting" };
	private int colorPos, tier; // tier = 0, 1, 2, (3)
	private int atk, cd, rng;
	private int currentCd = 0;
	private double cdFactor[] = { 0.9, 0.85, 0.75, 0.6 };
	private int cdTier = -1;
	private boolean isCool = true;
	private Deque<Projectile> projectiles = new LinkedList<Projectile>();
	private int kills = 0;

	public Tower(int col, int row, int color, int tier) {
		tile = new Tile(col, row, 2);
		this.colorPos = color;
		this.tier = tier - 1;
		upgradeTier();
	}

	/*
	 * Purpose: Increases tier by 1 and updates all stats to match (used to combine towers)
	 * Parameters: None
	 * Return: void
	 */
	public void upgradeTier() {
		tier++;
		atk = towerStats[tier][(int) (colorPos / 3)][0];
		if (colorPos == 4)
			cd = towerStats[tier][(int) (colorPos / 3)][1] * 2;
		else
			cd = towerStats[tier][(int) (colorPos / 3)][1];
		rng = towerStats[tier][(int) (colorPos / 3)][2];
	}

	/*
	 * Purpose: Draws the grey base of the tower and the inner colored core based on tower type
	 * Parameters: Graphics2D object
	 * Return: void
	 */
	public void draw(Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		g.fill3DRect(tile.getCol() * 20, tile.getRow() * 20, width, width, true);
		if (isTower) {
			g.setColor(getColor());
			if (tier == 3)
				g.fill3DRect(tile.getTileCenterX() - 5, tile.getTileCenterY() - 5, 30, 30, true);
			else
				g.fillOval(tile.getTileCenterX() + 5 * (1 - tier), tile.getTileCenterY() + 5 * (1 - tier),
						10 * (tier + 1), 10 * (tier + 1));
		}
	}

	/*
	 * Purpose: Updates the cooldown of the tower by the game speed
	 * Parameters: int of game speed
	 * Return: void
	 */
	public void updateCd(int gameSpd) {
		if (!isCool) {
			currentCd += gameSpd;
			if ((cdTier >= 0 && currentCd >= (int) (cd * cdFactor[cdTier])) || (cdTier < 0 && currentCd >= cd)) {
				isCool = true;
				currentCd = 0;
			}
		}
	}

	/*
	 * Purpose: Adds a Projectile to the projectiles queue
	 * Parameters: Enemy that is the current target of the tower
	 * Return: void
	 */
	public void addProjectile(Enemy enemy) {
		if (Math.random() <= enemy.getDodgeUp()) {
			if (colorPos == 4)
				projectiles.add(
						new Projectile(tile.getTowerCenterX(), tile.getTowerCenterY(), getColor(), enemy, tier, true));
			else
				projectiles.add(
						new Projectile(tile.getTowerCenterX(), tile.getTowerCenterY(), getColor(), enemy, -1, true));
			if (enemy.getType() != 5 && enemy.getHpLeft() <= atk * enemy.getAmrUp())
				enemy.setDead(); // tower will not target "dead" enemies
		} else {
			if (colorPos == 4)
				projectiles.add(
						new Projectile(tile.getTowerCenterX(), tile.getTowerCenterY(), getColor(), enemy, tier, false));
			else
				projectiles.add(
						new Projectile(tile.getTowerCenterX(), tile.getTowerCenterY(), getColor(), enemy, -1, false));
		}
		isCool = false;
	}

	/*
	 * Purpose: Determines whether a Tile is in the 2x2 square of Tiles that a tower occupies
	 * Parameters: Tile to check
	 * Return: true if within the Tower
	 */
	public boolean inTower(Tile tile) {
		int col = tile.getCol();
		int row = tile.getRow();
		if (this.tile.equals(tile)) // bottom right
			return true;
		if (col > 0 && this.tile.equals(new Tile(col - 1, row))) // bottom left
			return true;
		if (row > 0 && this.tile.equals(new Tile(col, row - 1))) // top right
			return true;
		if (col > 0 && row > 0 && this.tile.equals(new Tile(col - 1, row - 1))) // top left
			return true;
		return false;
	}

	// getters and setters
	public void setDead() {
		isTower = false;
	}

	public int getWidth() {
		return width;
	}

	public int getCol() {
		return tile.getCol();
	}

	public int getRow() {
		return tile.getRow();
	}

	public int getX() {
		return tile.getTowerCenterX();
	}

	public int getY() {
		return tile.getTowerCenterY();
	}

	public Tile getTile() {
		return tile;
	}

	public int getColorPos() {
		return colorPos;
	}

	public String getColorName() {
		return colorNames[colorPos];
	}

	public Color getColor() {
		return colors[colorPos];
	}

	public String getEffect() {
		return effects[colorPos];
	}

	public int getTier() {
		return tier;
	}

	public int getAtk() {
		return atk;
	}

	public int getCd() {
		if (cdTier < 0)
			return cd;
		return (int) (cd * cdFactor[cdTier]);
	}

	public int getCDTier() {
		return cdTier;
	}

	public void setCDTier(int tier) {
		cdTier = tier;
	}

	public boolean isCool() {
		return isCool;
	}

	public int getRange() {
		return rng;
	}

	public Deque<Projectile> getProjectiles() {
		return projectiles;
	}

	public boolean isTower() {
		return isTower;
	}

	public void addKill() {
		kills++;
	}

	public int getKills() {
		return kills;
	}

}