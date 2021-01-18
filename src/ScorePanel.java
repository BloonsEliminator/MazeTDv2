import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ScorePanel extends JPanel {
	JLabel title = new JLabel("   Game Highscores");
	JLabel[] scoreTitles = new JLabel[3]; // highscore dispaly spacing: 1 / 12 / 3 / 8
	JLabel[][] scoreLabels = new JLabel[3][5]; // space, name, space, score (numSpaces = 23 - name.length - score.length)

	public ScorePanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setAlignmentX(LEFT_ALIGNMENT);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		setBackground(OptionPanel.opBG);

		title.setAlignmentX(LEFT_ALIGNMENT);
		title.setFont(new Font("Courier", Font.BOLD, 20));
		add(title);

		scoreTitles[0] = new JLabel(" Survival (Levels):");
		scoreTitles[1] = new JLabel(" Time Trial(Seconds):");
		scoreTitles[2] = new JLabel(" Last Stand (Damage):");

		for (int i = 0; i < 3; i++) {
			add(Box.createRigidArea(new Dimension(0, 70)));

			scoreTitles[i].setAlignmentX(LEFT_ALIGNMENT);
			scoreTitles[i].setFont(new Font("Courier", Font.BOLD, 18));
			add(scoreTitles[i]);
			add(Box.createRigidArea(new Dimension(0, 10)));

			for (int j = 0; j < 5; j++) {
				scoreLabels[i][j] = new JLabel();
				scoreLabels[i][j].setAlignmentX(LEFT_ALIGNMENT);
				scoreLabels[i][j].setFont(new Font("Courier", Font.BOLD, 16));
				add(scoreLabels[i][j]);
			}
		}
	}

	/*
	 * Purpose: Updates all the highscore labels to match the values in the text files
	 * Parameters: 2D arrays of the scores and names
	 * Return: void
	 */
	public void updateLabels(int[][] scores, String[][] names) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				scoreLabels[i][j].setText(" " + names[i][j]
						+ " ".repeat(23 - names[i][j].length() - (int) (Math.log10(scores[i][j]) + 1)) + scores[i][j]);
			}
		}
	}

	/*
	 * Purpose: Sets the size of the ScorePanel (JPanel)
	 * Parameters: None
	 * Return: Dimension of the ScorePanel
	 */
	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		return new Dimension(250, 800);
	}
}
