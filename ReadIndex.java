import java.util.*;
import java.io.*;
import java.util.Arrays; // Zum Sortieren, erst ab JDK 1.2

public class ReadIndex {
    private String chosenLang, chosenOpt, searchTerm;
    //    private String[] searchTerms;
    private Vector hits = new Vector();
    private int numberHits; // Anzahl der Treffer
    private String indexFile;
    final private String SEPARATOR = "|";
    private String[] urls, titles;
    
    /**
       <h1>Konstruktor für ein Suchwort</h1>
       Die gewählte Sprache und das Suchwort werden dem Konstruktor übergeben.
       Die Sprachauswahl legt fest, welcher Wert der Variablen indexFile zugewiesen wird.
       Das Suchwort wird einer String-Instanz zugewiesen.
    */
    public ReadIndex(String lang, String term) {
	chosenLang = lang;
	searchTerm = term;
	
	if (chosenLang.equals("German")) {
	    indexFile = "indexDE.dat";
	} else if (chosenLang.equals("French")) {
	    indexFile = "indexFR.dat";
	} else {
	    indexFile = "indexIT.dat";
	}
    } // ReadIndex()
    
    /**
       Die Indexdatei wird eingelesen und Zeilenweise abgearbeitet.
       Jede Zeile wird zunächst in die drei Variablen url title und keyword
       aufgeteilt (Separator ist '|'), der String keyword wird wiederum in den
       String keywords aufgeteilt. Die einzelnen Tokens dieses Strings werden
       mit den Suchbegriffen verglichen.
       Vector Objekt (dynamisch veränderbar) nutzen um Treffer zu speichern.
       Zeilenweises einlesen der Indexdatei und Felder pro Eintrag in Variablen speichern
       keywords enthält alle keywords pro Datei
    */
    public void getMatches() {
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
			if (word.equalsIgnoreCase(searchTerm)) {
			    hits.add(title + SEPARATOR + url); // später nach 'title' sortieren
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
	
	/**
	   Die einzelnen Objekte des Vektors 'hits' mit einer StringTokenizer Instanz
	   in die beiden Komponenten aufspalten und in die Arrays 'titles' und 'urls' 
	   damit füllen.
	*/
	if (hits != null) {
	    final int TOTAL = hits.size();
	    Object[] objArray = hits.toArray();
	    String[] strArray = new String[TOTAL];
	    titles = new String[TOTAL];
	    urls = new String[TOTAL];
	    
	    for (int i = 0; i < TOTAL; i++) {
		strArray[i] = objArray[i].toString();
	    }
	    
	    Arrays.sort(strArray);
	    
	    for (int i = 0; i < TOTAL; i++) {
		StringTokenizer urlTitle = new StringTokenizer(strArray[i], SEPARATOR);
		titles[i] = urlTitle.nextToken();
		urls[i] = urlTitle.nextToken();
	    }
	} 	
    } // getMatches()
    
    public String[] getURLs() {
	return urls;
    }
    
    public String[] getTitles() {
	return titles;
    }
} // class ReadIndex
