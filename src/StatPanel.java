import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StatPanel extends JPanel {

	static JLabel title = new JLabel(" Welcome to MAZE TD");
	static JLabel gameMode = new JLabel("  Select New Game Mode");
	static JLabel level = new JLabel(" Level: 0");
	static JLabel enemyType = new JLabel(" Enemy Type: Normal");
	static JLabel lives = new JLabel(" Lives: 0");
	static JLabel time = new JLabel(" Time in Battle: 0");
	static JLabel tierTitle = new JLabel(" Tier Chances(T1/T2/T3):");
	static JLabel tierChances = new JLabel(" 0% / 0% / 0%");
	static JLabel score = new JLabel(" Score: 0");
	static JLabel[] gameStatsArr = new JLabel[] { gameMode, level, enemyType, lives, time, tierTitle, tierChances,
			score };

	public StatPanel() {
		setHeight(60);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(OptionPanel.opBG);

		title.setAlignmentX(LEFT_ALIGNMENT);
		title.setFont(new Font("Courier", Font.BOLD, 20));
		add(title);
		for (JLabel label : gameStatsArr) {
			label.setAlignmentX(LEFT_ALIGNMENT);
			label.setFont(new Font("Courier", Font.BOLD, 16));
			add(label);
		}
	}

	/*
	 * Purpose: Sets the height of the StatPanel (JPanel)
	 * Parameters: int of the height
	 * Return: void
	 */
	public void setHeight(int height) {
		setMinimumSize(new Dimension(250, height));
		setPreferredSize(new Dimension(250, height));
		setMaximumSize(new Dimension(250, height));
	}

	/*
	 * Purpose: Updates the text displayed on the StatPanel to match the game variables
	 * Parameters: the required values needed to be changed
	 * Return: void
	 */
	// tier chances
	public void updateChances(int t0, int t1, int t2) {
		tierChances.setText(" " + t0 + "% / " + t1 + "% / " + t2 + "%");
	}

	// all stats
	public void updateStats(int mode, int Level, String type, int Lives, int Time, double Score) {
		setHeight(215);
		if (mode == 2 && Level == 41)
			level.setText(" Level: LAST STAND");
		else
			level.setText(" Level: " + Level);
		lives.setText(" Lives: " + Lives);
		enemyType.setText(" Enemy Type: " + type);
		time.setText(" Time in Battle: " + Time);
		updateScore(mode, Score);
	}

	// score
	public void updateScore(int mode, double Score) {
		if (mode == 0)
			score.setText(" Score (Level): " + (int) Score);
		if (mode == 1)
			score.setText(" Score (Seconds): " + (int) Score);
		if (mode == 2)
			score.setText(" Score (Damage): " + (int) Score);
	}

	// title and game mode when reset game
	public void updateMode(String modeTitle) {
		title.setText("     Game Stats");
		gameMode.setText(" Game Mode: " + modeTitle);
	}

	// title and game mode when game over
	public void setGameOver() {
		title.setText("     GAME OVER");
		gameMode.setText("  Select New Game Mode");
	}
}
