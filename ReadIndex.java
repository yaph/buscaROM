/*
  Liest index.dat zeilenweise ein und vergleicht alle Stichwörter einer
  Zeile mit dem Suchbegriff. Bei Übereinstimmung werden URL und Titel
  des Dokuemnts einem Vektor zugefügt, aus dem dann zwei Stringarrays
  erzeugt werden, die in der Klasse SearchApplet weiter verarbeitet
  werden.
*/
import java.util.*;
import java.io.*;
import java.util.Arrays; // Zum Sortieren, erst ab JDK 1.2
import java.applet.AppletContext;
import java.net.URL;
import java.net.MalformedURLException;

public class ReadIndex {
    private String chosenOpt, searchTerm;
    private Vector hits = new Vector();
    private int numberHits; // Anzahl der Treffer
    private URL indexFile;
    final private String SEPARATOR = "|";
    private String[] urls, titles;
    private URL baseDir;

    /*
      Konstruktor der Klasse ReadIndex:
      Das Suchwort wird dem Konstruktor übergeben und einer
      Stringinstanz zugewiesen. baseDir ist das Verzeichnis,
      in dem sich die Klassen des Suchapplets befinden.
    */
    public ReadIndex(URL dir, String term) {
	baseDir = dir;
	searchTerm = term;
	
	try {
	    indexFile = new URL(baseDir,"index.dat");
	}
	catch (MalformedURLException ex) {
	    System.out.println(ex);
	}
	
    } // ReadIndex()

    /*
      Die Indexdatei wird eingelesen und Zeilenweise abgearbeitet.
      Jede Zeile wird zunächst in die drei Variablen 'url', 'title' und 'keyword'
      aufgeteilt (Separator ist '|'). Die Zeichenkette 'keyword' wird wiederum in die
      Stringvariable 'keywords' aufgeteilt. Die einzelnen Tokens dieses Strings werden
      mit den Suchbegriffen verglichen. Bei einem Treffer, werden 'title' und 'url'
      dem Vector 'hits' zusammen als eine Stringinstanz 'hits' angefügt. Der Vector
      ist als Datenstruktur in Java dynamisch veränderbar. Da die Trefferanzahl nicht von
      vornherein feststeht, bietet er eine unkomplizierte Möglichkeit die Treffer zu speichern.
      Nachdem die Indexdatei abgearbeitet ist, werden die einzelnen Objekte des Vektors 'hits'
      mit einer StringTokenizer Instanz wieder in die beiden Komponenten aufgespalten und in den
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
    
    // Methode gibt ein Array mit den URLs der Treffer zurück.
    public String[] getURLs() {
	return urls;
    }
    
    // Methode gibt ein Array mit den Titeln der Treffer zurück.
    public String[] getTitles() {
	return titles;
    }

    // Methode gibt Anzhal der URLs (=Anzahl der Titel) im Array zurück.
    public int getUrlCount() {
	return urls.length;
    }
} // class ReadIndex
