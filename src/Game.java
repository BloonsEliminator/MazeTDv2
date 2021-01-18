
/* Name: Anson He
 * Date: January 18, 2020
 * Purpose: Build a maze using towers to stop all the onslaught of enemies!
 * SURVIVAL MODE: Survive as long as possible!
 * TIME TRIAL MODE: Complete 40 levels as fast as possible!
 * LAST STAND MODE: Deal the most amount of damage after 40 levels!
 */

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class Game implements ActionListener {
	static JFrame frame = new JFrame("MazeTD");
	static JMenuBar menuBar = new JMenuBar();
	static JMenu newGameMenu = new JMenu("New Game Mode");
	static JMenuItem newSurvival = new JMenuItem("Survival");
	static JMenuItem newTimeTrial = new JMenuItem("Time Trial");
	static JMenuItem newLastStand = new JMenuItem("Last Stand");
	static JMenuItem muteMusic, showHighscores, toggleHyperSpeed, esc;
	static JMenuItem[] gameModes = { newSurvival, newTimeTrial, newLastStand };

	static int[][] scores = new int[3][5];
	static String[][] names = new String[3][5];
	static File bgm;
	static File[] bgmArr = new File[6];
	static Clip[] clips = new Clip[6];
	static int clipPos = 0;
	static int lastFrame;
	static boolean hasMusic = true;

	static DrawingPanel drawPanel;
	static OptionPanel optionPanel;
	static ScorePanel scorePanel;

	public Game() {
		for (int i = 0; i < 4; i++) {
			bgmArr[i] = new File("music\\bgm" + i + ".wav");
			try {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(bgmArr[i]));
			} catch (Exception e) {
				System.out.println("bgm" + i + ".wav file was not found!");
				hasMusic = false;
			}
		}

		for (int i = 0; i < 3; i++) {
			try {
				BufferedReader inFile = new BufferedReader(new FileReader("highscores\\highscores" + i + ".txt"));
				inFile.readLine();
				for (int j = 0; j < 5; j++) {
					String name = inFile.readLine();
					int score = Integer.parseInt(inFile.readLine());
					names[i][j] = name;
					scores[i][j] = score;
				}
				inFile.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (JMenuItem mode : gameModes) {
			mode.addActionListener(this);
			newGameMenu.add(mode);
		}
		menuBar.add(newGameMenu);

		KeyStroke m = KeyStroke.getKeyStroke(KeyEvent.VK_M, 0);
		muteMusic = new JMenuItem("Mute Music");
		muteMusic.setAccelerator(m);
		muteMusic.addActionListener(this);
		menuBar.add(muteMusic);

		KeyStroke h = KeyStroke.getKeyStroke(KeyEvent.VK_H, 0);
		showHighscores = new JMenuItem("Show Highscores");
		showHighscores.setAccelerator(h);
		showHighscores.addActionListener(this);
		menuBar.add(showHighscores);

		KeyStroke q = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0);
		toggleHyperSpeed = new JMenuItem("Game Speed: Normal");
		toggleHyperSpeed.setAccelerator(q);
		toggleHyperSpeed.addActionListener(this);
		menuBar.add(toggleHyperSpeed);

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		esc = new JMenuItem("Exit Program");
		esc.setAccelerator(escape);
		esc.addActionListener(this);
		menuBar.add(esc);
	}

	/*
	 * HIGHSCORE METHODS START highscore0.txt - Survival Mode Scores highscore1.txt
	 * - Time Trial Mode Scores highscore2.txt - Last Stand Mode Scores
	 * 
	 * Purpose: Calculates if a score at the end of the game is a highscore and
	 * saves it to the respective text file if it is. Parameters: current game mode,
	 * score and name to be saved as a highscore
	 */

	// returns true if a score is a highscore within the current game mode
	public static boolean isHighscore(int gameMode, double score) {
		for (int i = 0; i < 5; i++) {
			if (gameMode == 1 && score < scores[gameMode][4])
				return true;
			else if (gameMode != 1 && score > scores[gameMode][4])
				return true;
		}
		return false;
	}

	// returns a String representation of the position that the new highscore will
	// be placed on the leaderboard
	public static String getScorePos(int gameMode, double score) {
		int scorePos = 4;
		while (scorePos > 0 && ((gameMode == 1 && score < scores[gameMode][scorePos - 1])
				|| (gameMode != 1 && score > scores[gameMode][scorePos - 1]))) {
			scorePos--;
		}
		if (scorePos == 4)
			return "5th";
		else if (scorePos == 3)
			return "4th";
		else if (scorePos == 2)
			return "3rd";
		else if (scorePos == 1)
			return "2nd";
		else
			return "1st";
	}

	// adds the highscore to the names and scores array, then saves it to the
	// respective text file
	public static void addHighscore(int gameMode, String name, double score) { // isHighscore must be true
		int scorePos = 4;
		while (scorePos > 0 && ((gameMode == 1 && score < scores[gameMode][scorePos - 1])
				|| (gameMode != 1 && score > scores[gameMode][scorePos - 1]))) {
			names[gameMode][scorePos] = names[gameMode][scorePos - 1];
			scores[gameMode][scorePos] = scores[gameMode][scorePos - 1];
			scorePos--;
		}
		names[gameMode][scorePos] = name;
		scores[gameMode][scorePos] = (int) score;
		scorePanel.updateLabels(scores, names);

		for (int i = 0; i < 3; i++) {
			try {
				PrintWriter outFile = new PrintWriter(new FileWriter("highscores" + i + ".txt", false));
				outFile.println(gameModes[i].getText() + " Mode Highscores:");
				for (int j = 0; j < 5; j++) {
					outFile.println(names[i][j]);
					outFile.println(scores[i][j]);
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// HIGHSCORE METHODS END

	public static void setHyperSpeedText(boolean hyper) {
		if (hyper)
			toggleHyperSpeed.setText("Game Speed: HYPER SPEED!!!");
		else
			toggleHyperSpeed.setText("Game Speed: Normal");
	}

	/*
	 * Purpose: Overrides JMenuItems: newSurvival, newTimeTrial, and newLastStand
	 * start a new game in their respective game modes showHighscores and
	 * toggleHyperSpeed do exactly what you would expect esc unhighlights the place
	 * button if it is highlighted or exits the game
	 * 
	 * Parameters: ActionEvent (a click or a hotkey) Return: void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < 3; i++) {
			if (e.getSource() == gameModes[i]) {
				if (DrawingPanel.gameMode == -1) {
					drawPanel.resetGame(i, gameModes[i].getText());
					play(i + 1);
					muteMusic.setText("Mute Music");
				} else {
					int confirmNew = JOptionPane.showConfirmDialog(frame,
							"Do you really want to reset and start a new game in " + gameModes[i].getText() + " mode?",
							"STARTING NEW GAME!!!", JOptionPane.YES_NO_OPTION);
					if (confirmNew == JOptionPane.YES_OPTION) {
						drawPanel.resetGame(i, gameModes[i].getText());
						play(i + 1);
						muteMusic.setText("Mute Music");
					}
				}
			}
		}
		if (e.getSource() == muteMusic) {
			if (hasMusic) {
				if (clips[clipPos].isRunning()) {
					lastFrame = clips[clipPos].getFramePosition();
					clips[clipPos].stop();
					muteMusic.setText("Unmute Music");
				} else {
					if (lastFrame < clips[clipPos].getFrameLength()) {
						clips[clipPos].setFramePosition(lastFrame);
					} else {
						clips[clipPos].setFramePosition(0);
					}
					clips[clipPos].start();
					muteMusic.setText("Mute Music");
				}
			}
		}
		if (e.getSource() == showHighscores)

		{
			if (scorePanel.isVisible()) {
				scorePanel.setVisible(false);
				optionPanel.setVisible(true);
				showHighscores.setText("Show Highscores");
			} else {
				scorePanel.setVisible(true);
				optionPanel.setVisible(false);
				showHighscores.setText("Hide Highscores");
			}

		}
		if (e.getSource() == toggleHyperSpeed) { // hyper speed while in battle
			if (DrawingPanel.fps == 200) {
				DrawingPanel.fps = 50;
				setHyperSpeedText(true);
			} else if (DrawingPanel.fps == 50) {
				DrawingPanel.fps = 200;
				setHyperSpeedText(false);
			}
		}
		if (e.getSource() == esc) { // escape from highscores, placing towers, and the game itself
			if (scorePanel.isVisible()) {
				scorePanel.setVisible(false);
				optionPanel.setVisible(true);
				showHighscores.setText("Show Highscores");
			} else if (OptionPanel.placeButton.isHighlighted()) {
				OptionPanel.placeButton.removeHighlight();
				DrawingPanel.newTower.setVisible(false);
			} else {
				int confirmExit = JOptionPane.showConfirmDialog(frame, "Do you really want to exit the game?",
						"EXITING THE GAME!!!", JOptionPane.YES_NO_OPTION);
				if (confirmExit == JOptionPane.YES_OPTION) {
					if (hasMusic) {
						clips[clipPos].stop();
						clips[clipPos].flush();
					}
					System.exit(0);
				}
			}
		}
		drawPanel.repaint();
	}

	/*
	 * Purpose: Stops the current song and plays the one at (newPos) in the clips
	 * array Parameters: position of the new song file in the clips array Return:
	 * void
	 */
	public static void play(int newPos) {
		if (hasMusic) {
			clips[clipPos].stop();
			clips[clipPos].flush();
			clips[clipPos].setFramePosition(0);
			clipPos = newPos;
			try {
				clips[clipPos].loop(Clip.LOOP_CONTINUOUSLY);
				while (!clips[clipPos].isRunning())
					Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Purpose: Initializes all panels, creates the frame, and shows it Parameters:
	 * None Return: void
	 */
	public static void createAndShowGui() {
		scorePanel = new ScorePanel();
		scorePanel.updateLabels(scores, names);

		optionPanel = new OptionPanel();
		drawPanel = new DrawingPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setJMenuBar(menuBar);
		frame.add(drawPanel);
		frame.add(optionPanel);
		frame.add(scorePanel);
		scorePanel.setVisible(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
		if (hasMusic) {
			clips[clipPos].loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public static void main(String[] args) {
		new Game();
		SwingUtilities.invokeLater(() -> createAndShowGui());
	}
}
