/**
   Das Applet für die einfache Suche im Eurodelphes Index. Je Sprache (Deutsch,
   Franzöisch und Italienisch) existiert ein eigener Index. Der Nutzer muss bei
   der Eingabe die Sprache wählen, ansonsten wird deutsch als Defaultwert benutzt.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class SearchApplet extends Applet implements ActionListener {

    private Choice language, searchOptions;
    private TextField searchField;
    private Button searchButton;
    private String chosenLang = "German"; // 'German' default Sprache
    private String chosenOpt = "All Words"; // 'All Words' default Option
    private String searchTerms;
    private Result result;

    public void init() {
	setBackground(Color.yellow);
	
	// Ergebnisfenster
	result = new Result(getAppletContext());
        result.pack(); // Fenstergröße anhand der enthaltenen Komponenten bestimmen

	//****************************************************** 
	// individuelle Positionierung der Komponenten (Layout)
	// und die Event Bearbeitung ermöglichen
	//******************************************************

	setLayout(null);
	
	searchField = new TextField(20);
	searchField.setBackground(Color.white);
	searchField.setBounds(5,5,200,30);
	add(searchField);
	searchField.addActionListener(this);

	searchButton = new Button("Search!");
	searchButton.setBackground(Color.red);
	searchButton.setBounds(210,5,60,30);
	add(searchButton);
	searchButton.addActionListener(this);

	// Auswahlliste Sprachen
	language = new Choice();
	language.setBackground(Color.gray);
	language.setBounds(5,40,100,30);
	language.addItem("German");
	language.addItem("French");
	language.addItem("Italian");
	add(language);
	language.addItemListener(new LanguageChoice());
	
	// Auswahlliste Suchoptionen
	//Phrasensuche aufgrund des Indexes nicht möglich
	searchOptions = new Choice();
	searchOptions.setBackground(Color.gray);
	searchOptions.setBounds(105,40,100,30);
	searchOptions.addItem("All Words");
	searchOptions.addItem("Any Word");
	add(searchOptions);
	searchOptions.addItemListener(new OptionChoice());
    } // init()
    
    public void destroy() {
        result.setVisible(false);
        result = null;
    }

    //*********************************************************
    // Reaktion auf Benutzer Interaktion
    //*********************************************************
    public void actionPerformed (ActionEvent e) {
	searchTerms = searchField.getText();
	System.out.println(chosenLang);
	System.out.println(chosenOpt);
	ReadIndex ri = new ReadIndex(chosenLang, chosenOpt, searchTerms); 
	result.setVisible(true);
    }
    
    private class LanguageChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    chosenLang = e.getItem().toString();
	}
    }

    private class OptionChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    chosenOpt = e.getItem().toString();
	}
    }
    
} // class SearchApplet

/*
  // docBase = URL des Dokuments, in dem das Applet enthalten ist
  docBase = getDocumentBase().toString();
  // codeBase = URL des Applets
  codeBase = getCodeBase().toString();
  }
*/
