import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MapPainter {
	static Tile[] checkpoints = { new Tile(7, 4, 1), new Tile(7, 21, 1), new Tile(31, 21, 1), new Tile(31, 4, 1),
			new Tile(19, 4, 1), new Tile(19, 33, 1) };
	static Tile start = new Tile(0, 4, 1);
	static Tile goal = new Tile(39, 33, 1);

	// saves an copy of the map image to "map.png"
	// can edit and run this code to add grid lines or highlight the start and end tiles
	public static void main(String[] args) throws IOException {
		BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setPaint(DrawingPanel.dpBG);
		g.fillRect(0, 0, 800, 800);
		g.setPaint(Color.BLACK);

		// draws grid lines
//		for (int i = 20; i < 800; i += 20) {
//			g.drawLine(i, 0, i, 800);
//			g.drawLine(0, i, 800, i);
//		}

		// draws the normal light gray paths
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, checkpoints[0].getY(), checkpoints[0].getX(), 40);
		g.fillRect(checkpoints[0].getX(), checkpoints[0].getY(), 40, checkpoints[1].getY() - checkpoints[0].getY());
		g.fillRect(checkpoints[1].getX(), checkpoints[1].getY(), checkpoints[2].getX() - checkpoints[1].getX(), 40);
		g.fillRect(checkpoints[3].getX(), checkpoints[3].getY(), 40, checkpoints[2].getY() - checkpoints[3].getY());
		g.fillRect(checkpoints[4].getX(), checkpoints[4].getY(), checkpoints[3].getX() - checkpoints[4].getX(), 40);
		g.fillRect(checkpoints[4].getX(), checkpoints[4].getY(), 40, checkpoints[5].getY() - checkpoints[4].getY());
		g.fillRect(checkpoints[5].getX(), checkpoints[5].getY(), 800 - checkpoints[5].getX(), 40);

		// draws all the black checkpoints and the black unplacable parts around it
		g.setFont(new Font("SansSerif", Font.PLAIN, 20));
		for (int i = 0; i < checkpoints.length; i++) {
			g.setColor(Color.BLACK);
			int x = checkpoints[i].getX();
			int y = checkpoints[i].getY();
			g.fillRect(x, y, 40, 40);
			if (i != 1)
				g.fillRect(x - 40, y, 40, 40);
			if (i != 2)
				g.fillRect(x + 40, y, 40, 40);
			if (i != 5)
				g.fillRect(x, y + 40, 40, 40);
			if (i == 1 || i == 2 || i == 5)
				g.fillRect(x, y - 40, 40, 40);

			g.setColor(Color.WHITE);
			g.drawString("" + (i + 1), x + 15, y + 28);
		}

		// draws start and goal tiles
//		g.setColor(Color.RED);
//		g.fillRect(start.getX(), start.getY(), 20, 20);
//		g.setColor(Color.CYAN);
//		g.fillRect(goal.getX(), goal.getY(), 20, 20);

		// saves the image to "map.png"
		ImageIO.write(image, "png", new File("map.png"));
		g.dispose();
	}

}
