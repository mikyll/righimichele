package it.unibo.consolegui;

import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonBasic extends Button {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ButtonBasic(Panel p, String label, ActionListener listener){
		super(label);
		Font myFont = new Font("Arial", Font.PLAIN, 24);
		setFont(myFont);
		this.addActionListener(  listener );
		p.add(this); 
		p.validate();
		p.isVisible();
		//System.out.println("BUTTON BASIC CREATED");
	}
}