/**
   Das Applet für die einfache Suche im Eurodelphes Index. Je Sprache (Deutsch,
   Franzöisch und Italienisch) existiert ein eigener Index. Der Nutzer muss bei
   der Eingabe die Sprache wählen, ansonsten wird deutsch als Defaultwert benutzt.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
// Wenn 'import java.util.*' gibt es Namenskonflikte
// SearchApplet.java:107: reference to List is ambiguous,
// both class java.util.List in java.util 
// and class java.awt.List in java.awt match

//import java.util.Vector;
import java.util.StringTokenizer;

public class SearchApplet extends Applet implements ActionListener {

    private Choice language, searchOptions;
    private TextField searchField;
    private Button searchButton;
    private String chosenLang = "German"; // 'German' default Sprache
    private String chosenOpt = "All Words"; // 'All Words' default Option
    private String searchTerms;
    private Result result;
    private String[] matches;

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
	matches = (ri.getHitsArray());
	
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
    
    public String[] getMatches() {
	return matches;
    }
   
} // class SearchApplet    

// Ergebnisse darstellen
class Result extends Frame implements ActionListener {
    private SearchApplet searchApplet;
    private String chosenURL;
    private Label label;
    private List urlList;
    private Button go;
    private AppletContext appletContext;
    
    /**
       Die einzelnen Objekte des Vektors 'matches' mit einer StringTokenizer Instanz
       in die beiden Komponenten 'url' und 'title' aufspalten und in die zugehörigen
       Arrays 'urls' bzw. 'titles' einfügen. Das 'titles'-Array ist dann in der Liste
       sichtbar. Bei Klick auf einen der Titel wird der zugehörige URL ausgewählt und
       in dem aktuellen Browser-Fenster geöffnet.
    */
    public Result(AppletContext appletContext, SearchApplet searchApplet) {
	setLayout(new BorderLayout());
	setSize(520,600);
        this.appletContext = appletContext;
	this.searchApplet = searchApplet;
	
	String[] matches = searchApplet.getMatches();
	final int TOTAL = matches.length;
	StringTokenizer urlTitle;
	String[] urls = new String[TOTAL];
	String[] titles = new String[TOTAL];
	
	label = new Label(TOTAL + " documents found.", Label.CENTER);
	label.setBackground(Color.white);
	add(label, BorderLayout.NORTH);
	
	urlList = new List(20, false); // 20 Elemente sichtbar, eines auswählbar
        urlList.setBackground(Color.white);
	
	if (matches != null) {
	    for (int i=0; i<TOTAL;i++) {
		urlTitle = new StringTokenizer(matches[i],"|");
		titles[i] = urlTitle.nextToken();
		urls[i] = urlTitle.nextToken();
		urlList.add(titles[i]);
	    }
	    urlList.addItemListener(new URLChoice());
	}
	else {
	    urlList.add("No matches found.");
	}
	
	add(urlList, BorderLayout.CENTER);

	go = new Button("Show document");
	go.addActionListener(this);
	add(go, BorderLayout.SOUTH);
		
        addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent event) {
		    setVisible(false);
		}
	    });
    } // Result()
    
    public void actionPerformed(ActionEvent event) {
	URL url = null;
        try {
            url = new URL(chosenURL);
        } catch (MalformedURLException exception) {
            System.err.println("Malformed URL: " + chosenURL);
        }
	if (url != null) {
	    appletContext.showDocument(url);
	}
    }
    
    private class URLChoice implements ItemListener {
	public void itemStateChanged(ItemEvent event) {
	    chosenURL = event.getItem().toString();
	    System.out.println(chosenURL);
	}
    }

} // class Result
