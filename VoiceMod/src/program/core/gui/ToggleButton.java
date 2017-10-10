package program.core.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public abstract class ToggleButton extends JButton {
	private static final long serialVersionUID = 1L;
	private String offText;
	private String onText;
	private Color offColor;
	private Color onColor;
	private boolean toggleState;

	public ToggleButton(String offText, String onText, Color offColor, Color onColor) {
		super(offText);
		setBackground(offColor);
		this.addActionListener(new ToggleListener());
		this.offText = offText;
		this.onText = onText;
		this.offColor = offColor;
		this.onColor = onColor;
		toggleState = false;
	}

	public String getOffText() {
		return offText;
	}

	public void setOffText(String offText) {
		this.offText = offText;
	}

	public String getOnText() {
		return onText;
	}

	public void setOnText(String onText) {
		this.onText = onText;
	}

	public boolean getToggleState() {
		return toggleState;
	}

	public void setToggleState(boolean toggleState) {
		this.toggleState = toggleState;
		if (toggleState) {
			toggleOn();
			setText(onText);
			setBackground(onColor);
		} else {
			toggleOff();
			setText(offText);
			setBackground(offColor);
		}
	}

	public Color getOffColor() {
		return offColor;
	}

	public void setOffColor(Color offColor) {
		this.offColor = offColor;
	}

	public Color getOnColor() {
		return onColor;
	}

	public void setOnColor(Color onColor) {
		this.onColor = onColor;
	}

	protected abstract void toggleOn();

	protected abstract void toggleOff();

	class ToggleListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			toggleState = !toggleState;
			if (toggleState) {
				toggleOn();
				setText(onText);
				setBackground(onColor);
			} else {
				toggleOff();
				setText(offText);
				setBackground(offColor);
			}
		}

	}
}
