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
    private String[] urls, titles;
    
	public void init() {
	setBackground(Color.yellow);
	
	// individuelle Positionierung der Komponenten (Layout)
	setLayout(null);
	
	searchField = new TextField(20);
	searchField.setBackground(Color.white);
	searchField.setBounds(5,5,200,30);
	searchField.addActionListener(this);
	add(searchField);

	searchButton = new Button("Search!");
	searchButton.setBackground(Color.red);
	searchButton.setBounds(210,5,60,30);
	searchButton.addActionListener(this);
	add(searchButton);

	// Auswahlliste Sprachen
	language = new Choice();
	language.setBackground(Color.gray);
	language.setBounds(5,40,100,30);
	language.addItem("German");
	language.addItem("French");
	language.addItem("Italian");
	language.addItemListener(new LanguageChoice());
	add(language);
	
	// Auswahlliste Suchoptionen
	// Phrasensuche aufgrund des Indexes nicht möglich
	searchOptions = new Choice();
	searchOptions.setBackground(Color.gray);
	searchOptions.setBounds(105,40,100,30);
	searchOptions.addItem("All Words");
	searchOptions.addItem("Any Word");
	searchOptions.addItemListener(new OptionChoice());
	add(searchOptions);
    } // init()
    
    /**
       Result frame schließen und Result Instanz zerstören
    */
    public void destroy() {
        result.setVisible(false);
        result = null;
    }

    /**
       Reaktion auf Benutzer Interaktion
    */
    public void actionPerformed (ActionEvent e) {
	searchTerms = searchField.getText();
	
	ReadIndex ri = new ReadIndex(chosenLang, chosenOpt, searchTerms); 
	
	urls = ri.getURLs();
	titles = ri.getTitles();

	// Ergebnisfenster
	result = new Result(getAppletContext(),this); // this = aktuelles Applet
        result.pack(); // Fenstergröße anhand der enthaltenen Komponenten bestimmen
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
    
    public String[] getURLs() {
	return urls;
    }
    
    public String[] getTitles() {
	return titles;
    }
   
} // class SearchApplet    
