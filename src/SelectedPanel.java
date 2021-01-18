import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SelectedPanel extends JPanel {

	static JLabel title = new JLabel("SELECTED TOWER STATS"); // enemy stats: full hp, hpLeft, type, speed, life cost
	static JLabel color = new JLabel(" Color: 0");
	static JLabel effect = new JLabel(" Effect: N/A");
	static JLabel tier = new JLabel(" Tier: 0");
	static JLabel atk = new JLabel(" Attack: 0");
	static JLabel cd = new JLabel(" Cooldown: 0");
	static JLabel rng = new JLabel(" Range: 0");
	static JLabel kills = new JLabel(" Kills: 0");
	static JLabel[] gameStatsArr = new JLabel[] { color, effect, tier, atk, cd, rng, kills };

	public SelectedPanel() {
		setHeight(190);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(OptionPanel.opBG);

		title.setAlignmentX(LEFT_ALIGNMENT);
		title.setFont(new Font("Courier", Font.BOLD, 17));
		add(title);
		for (int i = 0; i < gameStatsArr.length; i++) {
			gameStatsArr[i].setAlignmentX(LEFT_ALIGNMENT);
			gameStatsArr[i].setFont(new Font("Courier", Font.BOLD, 16));
			add(gameStatsArr[i]);
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
	 * Purpose: Changes the panel to indicate whether the tower selected is dead (a wall) or alive (a live tower)
	 * Parameters: boolean of whether ther tower is a wall
	 * Return: void
	 */
	public void setWallStat(boolean isWall) {
		if (isWall) {
			setHeight(35);
			title.setText("  THIS IS JUST A WALL");
			for (JLabel label : gameStatsArr) {
				label.setVisible(false);
			}
		} else {
			setHeight(190);
			title.setText(" SELECTED TOWER STATS");
			for (JLabel label : gameStatsArr) {
				label.setVisible(true);
			}
		}
	}

	/*
	 * Purpose: Changes the panel to indicate the selected enemy's stats
	 * Parameters: full hp, hp left, position in array, type, speed, life cost
	 * Return: void
	 */
	public void setEnemyStats(double full, double left, int arrPos, String type, double speed, int cost) {// enemy stats: full hp, hpLeft, type, speed, life cost
		kills.setVisible(false);
		setHeight(170);
		title.setText("      ENEMY STATS");
		color.setText(" HP Left / HP Full:");
		effect.setText(" " + (Math.round(left * 100) / 100.0) + " / " + (Math.round(full * 100) / 100.0));
		tier.setText(" Position: " + (arrPos + 1));
		atk.setText(" Type: " + type);
		cd.setText(" Speed: " + (Math.round(speed * 100) / 100.0));
		rng.setText(" Life Cost: " + cost);
	}

	public void updateHp(double full, double left) {// tower and poison damage
		effect.setText(" " + left + " / " + full);
	}

	public void updateSpeed(double speed) {// slow debuff
		cd.setText(" Speed: " + (Math.round(speed * 100) / 100.0));
	}

	/*
	 * Purpose: Changes the panel to indicate a live tower's stats
	 * Parameters: color, effect, tier, attack, cooldown, range, kills
	 * Return: void
	 */
	public void updateStats(String Color, String Effect, int Tier, int Attack, int Cooldown, int Range, int Kills) {
		kills.setVisible(true);
		setHeight(190);
		title.setText(" SELECTED TOWER STATS");
		color.setText(" Color: " + Color);
		effect.setText(" Effect: " + Effect);
		tier.setText(" Tier: " + (Tier + 1));
		atk.setText(" Attack: " + Attack);
		cd.setText(" Cooldown: " + Cooldown);
		rng.setText(" Range: " + Range);
		kills.setText(" Kills: " + Kills);
	}

	public void updateCdStat(int Cooldown) { // cooldown buff
		cd.setText(" Cooldown: " + Cooldown);
	}

	public void updateKillStat(int Kills) {
		kills.setText(" Kills: " + Kills);
	}
}
