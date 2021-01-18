import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

// A class that constructs a box out of a rigid area and a JButton
public class Button {
	private Box box = Box.createVerticalBox();
	private JButton button = new JButton();
	private Border bevel = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
			BorderFactory.createLoweredBevelBorder());
	private Border green = BorderFactory.createLineBorder(Color.green, 3);
	private boolean isHighlighted = false;
	
	public Button(String text) {
		button = new JButton();
		box.add(Box.createRigidArea(new Dimension(0, 10)));
		button.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		button.setMinimumSize(new Dimension(250, 50));
		button.setPreferredSize(new Dimension(250, 50));
		button.setMaximumSize(new Dimension(250, 50));
		button.setText(text);
		box.add(button);
	}

	// getter methods
	public JButton getButton() {
		return button;
	}

	public Box getBox() {
		return box;
	}
	
	public boolean isHighlighted() {
		return isHighlighted;
	}

	// setter methods
	public void setHighlight() {
		button.setBorder(BorderFactory.createCompoundBorder(green, bevel));
		isHighlighted = true;
	}

	public void removeHighlight() {
		button.setBorder(bevel);
		isHighlighted = false;
	}

}
