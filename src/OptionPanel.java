import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class OptionPanel extends JPanel implements ActionListener {

	public static final int opWidth = 250;
	public static final int opHeight = 800;
	public static final Color opBG = new Color(230, 255, 255);

	static StatPanel statPanel;
	static SelectedPanel selectedPanel;

	static Button placeButton = new Button("Place (T)owers: 1 Repick Left"); // can be swapped with "Repick Towers" button
	static Button keepButton = new Button("Keep Towe(R)"); // can be swapped with "Remove Wall" button
	static Button combineButton = new Button("(C)ombine Tower"); // can be swapped with "Remove Wall" button
	static Button[] buttonArr = new Button[] { placeButton, keepButton, combineButton };

	public OptionPanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setAlignmentX(LEFT_ALIGNMENT);
		setBackground(opBG);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		statPanel = new StatPanel();
		add(statPanel);

		for (Button button : buttonArr) {
			button.getButton().addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					button.getButton().setBackground(DrawingPanel.dpBG);
				}

				public void mouseExited(java.awt.event.MouseEvent evt) {
					button.getButton().setBackground(new JButton().getBackground());
				}
			});
			button.getBox().setAlignmentX(LEFT_ALIGNMENT);
			button.getButton().addActionListener(this);
		}
		add(placeButton.getBox());
		placeButton.getButton().setVisible(false);

		// Setting up AbstractActions for buttons
		Action placeAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				placeButtonAction();
			}
		};
		Action keepAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keepButtonAction();
			}
		};
		Action combineAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combineButtonAction();
			}
		};

		// Setting up hotkeys for buttons
		placeButton.getButton().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "place");
		placeButton.getButton().getActionMap().put("place", placeAction);

		keepButton.getButton().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "keep");
		keepButton.getButton().getActionMap().put("keep", keepAction);

		combineButton.getButton().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "combine");
		combineButton.getButton().getActionMap().put("combine", combineAction);

		Box selectedBox = Box.createVerticalBox();
		selectedBox.add(Box.createRigidArea(new Dimension(0, 10)));
		selectedPanel = new SelectedPanel();
		selectedBox.add(selectedPanel);
		add(selectedBox);
		selectedPanel.setVisible(false);

		add(keepButton.getBox());
		keepButton.getButton().setVisible(false);
		add(combineButton.getBox());
		combineButton.getButton().setVisible(false);
	}

	// END OF CONSTRUCTOR

	/*
	 * Purpose: The actions to be executed when a JButton is clicked
	 * Parameters: None
	 * Return: void
	 */
	public void placeButtonAction() {
		if (placeButton.getButton().isVisible()) {
			if (placeButton.isHighlighted()) { // set newTower to invisible
				placeButton.removeHighlight();
				DrawingPanel.newTower.setVisible(false);
			} else {
				if (DrawingPanel.tempTowerMap.size() == DrawingPanel.maxTowers) { // repick towers
					keepButton.getButton().setVisible(false);
					combineButton.getButton().setVisible(false);
					for (Tile tile : DrawingPanel.tempTowerMap.keySet()) {
						DrawingPanel.grid.setSquareTypes(tile.getCol(), tile.getRow(), 0);
					}
					DrawingPanel.tempTowerMap.clear();
					placeButton.getButton().setText("Place (T)owers: 0 Repicks Left");
					DrawingPanel.selectedTower = null;
					selectedPanel.setVisible(false);
				}
				placeButton.setHighlight();
				DrawingPanel.newTower.setVisible(true); // set newTower to visible
			}
			Game.drawPanel.repaint();
		}
	}

	public void keepButtonAction() {
		if (keepButton.getButton().isVisible()) { // starts next level
			keepButton.getButton().setVisible(false);
			combineButton.getButton().setVisible(false);
			if (DrawingPanel.selectedTower.isTower()) {
				placeButton.getButton().setVisible(false);
				DrawingPanel.startNextLevel(null);
			} else {
				DrawingPanel.removeWall();
				selectedPanel.setVisible(false); // removes dead tower(wall)
			}
			Game.drawPanel.repaint();
		}
	}

	public void combineButtonAction() { // upgrades tower then starts next level
		if (combineButton.getButton().isVisible()) {
			placeButton.getButton().setVisible(false);
			keepButton.getButton().setVisible(false);
			combineButton.getButton().setVisible(false);
			DrawingPanel.startNextLevel(DrawingPanel.hasDuplicate(DrawingPanel.tempTowerMap.values()));
			Game.drawPanel.repaint();
		}
	}

	/*
	 * Purpose: Overrides the actions for the JButtons
	 * Parameters: ActionEvent (click or hotkey)
	 * Return: void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == placeButton.getButton()) { // move this to option panel, this will occur when towers are picked
			placeButtonAction();
		}
		if (e.getSource() == keepButton.getButton()) { // keep towers, start level
			keepButtonAction();
		}
		if (e.getSource() == combineButton.getButton()) { // combine towers, start level
			combineButtonAction();
		}
	}

	/*
	 * Purpose: Sets the size of the OptionPanel (JPanel)
	 * Parameters: None
	 * Return: Dimension of the OptionPanel
	 */
	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		return new Dimension(opWidth, opHeight);
	}

}
