import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Projectile {
	private double x, y;
	private double dx, dy;
	private Color color;

	private int moves; // travel time for projectile
	private Enemy target;
	private double targetX, targetY;
	private boolean hitTarget = false;
	private int cannonTier; // size and speed based on this
	private boolean willHit;

	public Projectile(int x, int y, Color color, Enemy target, int cannonTier, boolean hit) {
		this.x = x;
		this.y = y;
		this.color = color;
		willHit = hit;
		this.cannonTier = cannonTier;
		if (cannonTier >= 0)
			this.moves = 50;
		else
			this.moves = 30;
		this.target = target;

		Tile beforeHitTile, afterHitTile;
		int tilesTravelled = ((int) (moves * target.getSpd() * (1 - (0.1 * target.getSlowTier())) / 20));

		if (target.getPathPos() + tilesTravelled - 1 < 0) {
			beforeHitTile = target.getStartTarget();
			afterHitTile = target.getPath().get(0);
		} else if (target.getPathPos() + tilesTravelled + 1 >= target.getPath().size()) {
			beforeHitTile = target.getPath().get(target.getPath().size() - 1);
			afterHitTile = target.getEndTarget();
		} else {
			beforeHitTile = target.getPath().get(target.getPathPos() + tilesTravelled - 1);
			afterHitTile = target.getPath().get(target.getPathPos() + tilesTravelled);
		}
		int beforeX = beforeHitTile.getTileCenterX();
		int beforeY = beforeHitTile.getTileCenterY();
		int afterX = afterHitTile.getTileCenterX();
		int afterY = afterHitTile.getTileCenterY();

		double displacement = Math.abs(target.getDisplacement());

		targetX = beforeHitTile.getTileCenterX();
		targetY = beforeHitTile.getTileCenterY();
		if (beforeX == afterX) {
			if (beforeY < afterY)
				targetY += displacement;
			else
				targetY -= displacement;
		} else {
			if (beforeX < afterX)
				targetX += displacement;
			else
				targetX -= displacement;
		}

		dx = (targetX - x) / moves;
		dy = (targetY - y) / moves;
	}

	/*
	 * Purpose: Draws the projectile based on its position, color type, and cannon tier
	 * Parameters: Graphics2D
	 * Return: void
	 */
	public void draw(Graphics2D g) {
		int x = (int) this.x;
		int y = (int) this.y;
		int length1 = (int) (4 * Math.pow(1.3, (cannonTier + 1)));

		g.setStroke(new BasicStroke((float) Math.pow(1.3, 1.0 * (cannonTier + 1)) * 2));
		g.setColor(color);
		g.drawLine(x - length1, y, x + length1, y); // --
		g.drawLine(x, y - length1, x, y + length1); // |
		g.drawLine(x - length1 * 2 / 3, y - length1 * 2 / 3, x + length1 * 2 / 3, y + length1 * 2 / 3); // \
		g.drawLine(x + length1 * 2 / 3, y - length1 * 2 / 3, x - length1 * 2 / 3, y + length1 * 2 / 3); // /

		g.setStroke(new BasicStroke((float) Math.pow(1.3, 1.0 * (cannonTier + 1))));
		g.setColor(Color.BLACK);
		g.drawLine(x - length1, y, x + length1, y); // --
		g.drawLine(x, y - length1, x, y + length1); // |
		g.drawLine(x - length1 * 2 / 3, y - length1 * 2 / 3, x + length1 * 2 / 3, y + length1 * 2 / 3); // \
		g.drawLine(x + length1 * 2 / 3, y - length1 * 2 / 3, x - length1 * 2 / 3, y + length1 * 2 / 3); // /
	}

	/*
	 * Purpose: Updates the position of the projectile
	 * Parameters: None
	 * Return: void
	 */
	public void move() {
		moves--;
		x += dx;
		y += dy;
		if (moves <= 0) {
			hitTarget = true;
		}
	}

	// getters and setters
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Enemy getTarget() {
		return target;
	}

	public int getCannonTier() {
		return cannonTier;
	}

	public boolean getHitStatus() {
		return hitTarget;
	}

	public boolean willHit() {
		return willHit;
	}
}
