import java.util.*;
import java.io.*;
import java.util.Arrays; // Zum Sortieren, erst ab JDK 1.2
import java.applet.AppletContext;
import java.net.URL;
import java.net.MalformedURLException;

public class ReadIndex {
    private String chosenLang, chosenOpt, searchTerm;
    private Vector hits = new Vector();
    private int numberHits; // Anzahl der Treffer
    private URL indexFile;
    final private String SEPARATOR = "|";
    private String[] urls, titles;
    private URL baseDir; 
	
    /**
       <h1>Konstruktor:</h1>
       Die gewählte Sprache und das Suchwort werden dem Konstruktor übergeben.
       Die Sprachauswahl legt fest, welcher Wert der Variablen indexFile zugewiesen wird.
       Das Suchwort wird einer String-Instanz zugewiesen.
    */
    public ReadIndex(URL dir, String lang, String term) {
	baseDir = dir;
	chosenLang = lang;
	searchTerm = term;
	
	if (chosenLang.equals("German")) {
	    try {
		indexFile = new URL(baseDir,"indexDE.dat");
	    }
	    catch (MalformedURLException ex) {
		System.out.println(ex);
            }
	} else if (chosenLang.equals("French")) {
	    try {
		indexFile = new URL(baseDir,"indexFR.dat");
	    }
	    catch (MalformedURLException ex) {
		System.out.println(ex);
            }
	} else {
	    try {
		indexFile = new URL(baseDir,"indexIT.dat");
	    }
	    catch (MalformedURLException ex) {
		System.out.println(ex);
            }
	}
    } // ReadIndex()
    
    /**
       Die Indexdatei wird eingelesen und Zeilenweise abgearbeitet.
       Jede Zeile wird zunächst in die drei Variablen 'url', 'title' und 'keyword'
       aufgeteilt (Separator ist '|'). Die Zeichenkette 'keyword' wird wiederum in die
       Stringvariable 'keywords' aufgeteilt. Die einzelnen Tokens dieses Strings werden
       mit den Suchbegriffen verglichen. Bei einem Treffer, werden 'title' und 'url' 
       dem Vector 'hits' zusammen als eine Stringinstanz 'hits' angefügt. Der Vector 
       ist als Datenstruktur in Java dynamisch veränderbar. Da die Trefferanzahl nicht von 
       vornherein feststeht bietet er eine unkomplizierte Möglichkeit die Treffer zu speichern. 
       Nachdem die Indexdatei abgearbeitet ist, werden die einzelnen Objekte des Vektors 'hits'
       mit einer StringTokenizer Instanz wieder in die beiden Komponenten aufspalten und in den 
       Arrays 'titles' und 'urls' gespeichert.
    */
    public void getMatches() {
	try {
	    InputStreamReader isr = new InputStreamReader(indexFile.openStream());
	    BufferedReader inFile = new BufferedReader(isr);
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
	    System.out.println(exception);
	}
	catch (IOException exception) {
	    System.out.println(exception);
	}
	catch (SecurityException exception) {
	    System.out.println("Security problem" + exception);
	}
	
	// Hier CASTen!!!
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
    
    /**
       Gibt ein Array mit den URLs der Treffer zurück.
    */
    public String[] getURLs() {
	return urls;
    }
    
    /**
       Gibt ein Array mit den Titeln der Treffer zurück.
    */
    public String[] getTitles() {
	return titles;
    }

    /**
       Gibt Anzhal der URLs (=Anzahl der Titel) im Array
       zurück.
    */
    public int getUrlCount() {
	return urls.length;
    }
} // class ReadIndex
