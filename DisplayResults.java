/**
   In diesem Applet werden die Suchergebnisse dargestellt.
 */
import java.awt.*;
import java.applet.Applet;

public class DisplayResults extends Applet {
    TextArea displayField;
    public void init() {
	setSize(getWidth(),getHeight());
	displayField = new TextArea(20,60);
	displayField.setEditable(false);
	setLayout(new GridLayout(1, 0));
	add(displayField);
    }
}
