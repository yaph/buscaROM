/**
   Das Applet für die Suche im Eurodelphes Index. Je Sprache (Deutsch,
   Franzöisch und Italienisch) existiert ein eigener Index. Der Nutzer 
   muss bei der Eingabe die Sprache wählen, ansonsten wird deutsch als 
   Defaultwert benutzt. Der Nutzer kann außerdem zwischen den Optionen
   'All Words' (logisches UND) und 'Any Word' (logisches ODER) zur
   Verknüpfungen der Suchbegriffe wählen.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays; // Zum Sortieren, erst ab JDK 1.2
import java.util.Hashtable;

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
	
	if (searchTerm.length() > 0) { // nur wenn etwas eingegeben wurde reagieren
	    if (searchTerm.indexOf(WS) > 0) { // mehr als ein Suchwort
		StringTokenizer words = new StringTokenizer(searchTerm, WS);
		final int TOTAL = words.countTokens();
		int[] urlCount = new int[TOTAL];
		int totalUrlCount = 0;
		Vector urlVec = new Vector();
		Vector titleVec = new Vector();
		
		/* 
		   Für jedes Suchwort die Ergebnissarrays holen und zum den
		   Ergebnisvektoren 'urlVec' und 'titleVec' zufügen.
		*/
		for (int i = 0; i < TOTAL; i++) {
		    ReadIndex ri = new ReadIndex(chosenLang, words.nextToken());
		    ri.getMatches();
		    urlCount[i] = ri.getUrlCount();// Anzahl der URLs (=Titel)
		    urlVec.add(ri.getURLs());
		    titleVec.add(ri.getTitles());
		    totalUrlCount += urlCount[i];
		}
		
		// Nur wenn Ergebnisvektor nicht leer ist
		if (urlVec.capacity() > 1) {
		    
		    // UND Suche
		    if (chosenOpt.equals("AND")) {
			/*
			  Das kürzeste Array finden und nur die Elemente
			  dieses Array mit den Elementen der anderen Vergleichen
			  (Hashtable???). Was im Gesamtergebnis ist muss auch im
			  kürzesten sein.
			*/
			int minAr = 0; // Zunächst erstes Array auf minimum gestetzt
			int min = 1000; // hoher Anfangswert
			for (int i = 0; i < urlCount.length; i++) {
			    if (urlCount[i] > 0 && urlCount[i] < min) {
				min = urlCount[i];
				minAr = i;
			    }
			}

			/*
			  Die Elemente des kürzesten Arrays in Hashtable speichern. 
			  Die URLs sind die Keys, da eindeutig.
			*/
			Hashtable cmpHash = new Hashtable();
			String[] minArray = new String[min];
			minArray = (String[]) urlVec.elementAt(minAr);
			for (int i = 0; i < min; i++) {
			    cmpHash.put(minArray[i], "1"); // Nur an den Keys interessiert
			}
			
			urls = new String[totalUrlCount]; //erstmal zu viel
			titles = new String[totalUrlCount]; //erstmal zu viel
			
			
			//System.out.println("Min: " + min + "\nMinaAr: " + minAr);
			
			/* 
			   Die URLs des kürzesten Arrays in eine Hashtable speichern.
			   Die Urls aller anderen Arrays daraufhin prüfen, ob sie bereits
			   in der Hashtabelle vorhanden sind.
			*/
			
			//Hashtable compareHash = new Hashtable;
			//if containsKey
			
		    } // UND Suche 
		    
		    else { // OR
			urls = new String[totalUrlCount];
			titles = new String[totalUrlCount];
			int counter = 0;
			for (int i = 0; i < TOTAL; i++) {
			    // Interims String Arrays
			    String[] urlAr = new String[urlCount[i]];
			    String[] titleAr = new String[urlCount[i]];
			    
			    urlAr = (String[]) urlVec.elementAt(i);
			    titleAr = (String[]) titleVec.elementAt(i);
			    
			    for (int j = 0; j < urlAr.length; j++) {
				urls[counter] = urlAr[j];
				titles[counter] = titleAr[j];
				counter++;
			    }
			}
			Arrays.sort(urls);
			Arrays.sort(titles);
		    }
		} 
		
		else { // nur ein Suchwort AND, OR egal
		    ReadIndex ri = new ReadIndex(chosenLang, searchTerm); 
		    ri.getMatches();
		    urls = ri.getURLs();
		    titles = ri.getTitles();
		}
	    } // if (urlVec.capacity() > 1) {

	    else { // Ausgabe bei 0 Treffern
		urls = new String[1];
		urls[0] = "";
		titles = new String[1];
		titles[0] = "No matches found";
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
