import java.util.*;
import java.io.*;
public class ReadIndex {
    private String chosenLang, chosenOpt, searchTerms;

    // speichert Suchergebnisse
    // Vielleicht Array nehmen um die Trefferliste zu begrenzen
    // Länge der Trefferliste durch Nutzer bestimmen lassen Texteingabefeld oder Choice
    private Vector hits = new Vector();
    private int numberHits; // Anzahl der Treffer
    private String indexFile;
    final private String SEPARATOR = "|";
    /**
       Die Indexdatei wird eingelesen und Zeilenweise abgearbeitet.
       Jede Zeile wird zunächst in die drei Variablen url title und keyword
       aufgeteilt (Separator ist '|'), der String keyword wird wiederum in den
       String keywords aufgeteilt. Die einzelnen Tokens dieses Strings werden
       mit den Suchbegriffen verglichen.
       Vector Objekt (dynamisch veränderbar) nutzen um Treffer zu speichern.
    */
    public ReadIndex(String lang, String opt, String terms) {
	chosenLang = lang;
	chosenOpt = opt;
	searchTerms = terms;
	
	if (chosenLang.equals("German")) {
	    indexFile = "indexDE.dat";
	} else if (chosenLang.equals("French")) {
	    indexFile = "indexFR.dat";
	} else {
	    indexFile = "indexIT.dat";
	}

	// Zeilenweises einlesen der Indexdatei und Felder pro Eintrag in Variablen speichern
	// keywords enthält alle keywords pro Datei
	try {
	    FileReader fr = new FileReader(indexFile);
	    BufferedReader inFile = new BufferedReader(fr);
	    String line = inFile.readLine();
	    while (line != null) {
		StringTokenizer fields = new StringTokenizer (line,SEPARATOR);
		String url = "", title = "", keyword = "";
		try {
		    url = fields.nextToken();
		    title = fields.nextToken();
		    keyword = fields.nextToken();
		}
		catch (NoSuchElementException exception) {
		    System.out.println("URL: "+url + "Title: " +title + "key: " +keyword + exception);
		}
		
		if (keyword != null) {
		    StringTokenizer keywords = new StringTokenizer(keyword);
		    while (keywords.hasMoreTokens()) {
			String word = keywords.nextToken();
			if (word.equalsIgnoreCase(searchTerms)) {
			    //System.out.println(url);
			    hits.add(url + SEPARATOR + title);
			}
		    }
		}
		line = inFile.readLine();
	    } // while (line != null)
	    inFile.close();
	} // try
	catch (FileNotFoundException exception) {
	    System.out.println(indexFile + exception);
	}
	catch (IOException exception) {
	    System.out.println(indexFile + exception);
	}
	//Trefferliste begrenzen (Geschwindigkeit)???
	// int countTokens() benutzen um Elementanzahl rauszufinden
	
	//String[] displayHits = {url, title};
	
    } // ReadIndex()
    
    public String getNumberHits() {
	numberHits = hits.size();
	String numHits = "Number of Hits: " + numberHits + "\n";
	return numHits;
    } 
    
    public void displayResults() {
	// FALLS KEINE ERGEBNISSE STATISCHE HTML-SEITE ÖFFNEN
	// neues Fenster mit Suchergebnissen öffnen in einem neuen Applet anzeigen lassen.
    }
}
