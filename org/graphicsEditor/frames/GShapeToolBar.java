package org.graphicsEditor.frames;

import org.graphicsEditor.global.GConstants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GShapeToolBar extends JToolBar {
	private GConstants.EShapeType eShapeType;
	public GConstants.EShapeType getShapeType() {
		return eShapeType;
	}

	public GShapeToolBar() {
		ActionHandler actionHandler = new ActionHandler();
		ButtonGroup group = new ButtonGroup();

		for (GConstants.EShapeType type : GConstants.EShapeType.values()) {
			JRadioButton radioButton = new JRadioButton(type.getName());
			this.add(radioButton);
			group.add(radioButton);
			radioButton.addActionListener(actionHandler);
			radioButton.setActionCommand(type.toString());
		}
		((JRadioButton)(this.getComponentAtIndex(GConstants.EShapeType.eSelect.ordinal()))).doClick();
	}

	private class ActionHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// "eSelec" ==> eSelect
			eShapeType = GConstants.EShapeType.valueOf(e.getActionCommand());
		}
	}

}
