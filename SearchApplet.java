/**
   Das Applet für die einfache Suche im Eurodelphes Index. Je Sprache (Deutsch,
   Franzöisch und Italienisch) existiert ein eigener Index. Der Nutzer muss bei
   der Eingabe die Sprache wählen, ansonsten wird deutsch als Defaultwert benutzt.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import java.util.Vector;

public class SearchApplet extends Applet implements ActionListener {

    private Choice language, searchOptions;
    private TextField searchField;
    private Button searchButton;
    private String chosenLang = "German"; // 'German' default Sprache
    private final String AND = "All Words";
    private final String OR = "Any Word";
    private String chosenOpt = "AND"; // 'All Words' default Option
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
	searchOptions.addItem("AND");
	searchOptions.addItem("OR");
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
	String searchTerm = searchField.getText();;
	final String WS = " "; // ' ' trennt mehrere Suchbegriffe
	ReadIndex ri;
	
	if (searchTerm.length() > 0) { // nur wenn etwas eingegeben wurde reagieren
	    if (searchTerm.indexOf(WS) > 0) { // mehr als ein Suchwort
		StringTokenizer words = new StringTokenizer(searchTerm, WS);
		//final int TOTAL = words.countTokens();
		//Vector urlVec = new Vector();
		//Vector titleVec = new Vector();
		
		int wordCount = 0;
		int urlCount = 0;
		while (words.hasMoreTokens()) {
		    System.out.println(words.nextToken());
		    ri = new ReadIndex(chosenLang, words.nextToken());
		    ri.getMatches();
		    urls = ri.getURLs();
		    titles = ri.getTitles();
		    urlCount += urls.length;
		    wordCount++;
		}
		System.out.println(urlCount);
		
		if (chosenOpt.equals(AND)) {
		    //ri.andSearch();
		} 
		else {
		}
	    } 
	    else { // nur ein Suchwort AND, OR egal
		ri = new ReadIndex(chosenLang, searchTerm); 
		ri.getMatches();
		urls = ri.getURLs();
		titles = ri.getTitles();
	    }
	    
	    // Ergebnisfenster
	    result = new Result(getAppletContext(),this); // this = aktuelles Applet
	    result.setVisible(true);
	} // if (searchTerms.length() > 0)
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
