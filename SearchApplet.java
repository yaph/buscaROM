/**
   Das Applet für die einfache Suche im Eurodelphes Index. Je Sprache (Deutsch,
   Franzöisch und Italienisch) existiert ein eigener Index. Der Nutzer muss bei
   der Eingabe die Sprache wählen, ansonsten wird deutsch als Defaultwert benutzt.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class SearchApplet extends Applet {

    private Choice language, searchOptions;
    private TextField searchField;
    private Button searchButton;
    //    private String docBase, codeBase;
    private char languageChosen; // German, French, Italian
    private char optionChosen; // all words, any word 
    
    public void init() {
	// Positionierung der Elemente selbst bestimmen
	setLayout(null);
	
	searchField = new TextField(20);
	searchField.setBackground(Color.white);
	searchField.setBounds(5,5,200,30);
	add(searchField);
	
	searchButton = new Button("Search!");
	searchButton.setBackground(Color.red);
	searchButton.setBounds(210,5,60,30);
	add(searchButton);
	searchButton.addMouseListener(new ButtonClick());

	// Auswahlliste für die Sprachen
	language = new Choice();
	language.setBackground(Color.gray);
	language.setBounds(5,40,100,30);
	language.addItem("German");
	language.addItem("French");
	language.addItem("Italian");
	add(language);
	language.addItemListener(new LanguageChoice());
	
	// Auswahlliste für die Suchoptionen
	//Phrasensuche aufgrund des Indexes nicht möglich
	searchOptions = new Choice();
	searchOptions.setBackground(Color.gray);
	searchOptions.setBounds(105,40,100,30);
	searchOptions.addItem("All Words");
	searchOptions.addItem("Any Word");
	add(searchOptions);
	searchOptions.addItemListener(new OptionChoice());
    } // init()
    
    private class ButtonClick extends MouseAdapter {
	public void mouseClicked(MouseEvent e) {
	    System.out.println("x: "+e.getX()); // das funzt endlich
	    // start search ...
	}
    }
    
    private class LanguageChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    System.out.println(e.getItem());
	}
    }

    private class OptionChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	   System.out.println(e.getItem());
	}
    }
    

} // class SearchApplet

/*

    
   
    public void paint(Graphics g) {
	//Draw a Rectangle around the applet's display area.
	setBackground(Color.yellow);
	g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
    }

  
  // docBase = URL des Dokuments, in dem das Applet enthalten ist
  docBase = getDocumentBase().toString();
  // codeBase = URL des Applets
  codeBase = getCodeBase().toString();
  }
*/
