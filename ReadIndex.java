// Copyright 2002 - 2003, Ramiro G�mez
/*
  Liest index.dat zeilenweise ein und vergleicht alle Stichw�rter einer
  Zeile mit dem Suchbegriff. Bei �bereinstimmung werden URL und Titel
  des Dokuments einem Vektor zugef�gt, aus dem dann zwei Stringarrays
  erzeugt werden, die in der Klasse SearchApplet weiter verarbeitet
  werden.
*/
import java.util.*;
import java.io.*;
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
      Das Suchwort wird dem Konstruktor �bergeben und einer
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
      Jede Zeile wird zun�chst in die drei Variablen 'url', 'title' und 'keyword'
      aufgeteilt (Separator ist '|'). Die Zeichenkette 'keyword' wird wiederum in die
      Stringvariable 'keywords' aufgeteilt. Die einzelnen Tokens dieses Strings werden
      mit den Suchbegriffen verglichen. Bei einem Treffer, werden 'title' und 'url'
      dem Vector 'hits' zusammen als eine Stringinstanz 'hits' angef�gt. Der Vector
      ist als Datenstruktur in Java dynamisch ver�nderbar. Da die Trefferanzahl nicht von
      vornherein feststeht, bietet er eine unkomplizierte M�glichkeit die Treffer zu speichern.
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
			    hits.add(title + SEPARATOR + url); // sp�ter nach 'title' sortieren
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
	    
	    titles = new String[TOTAL];
	    urls = new String[TOTAL];
	    
	    /*
	      Die Objekte des Vektors hits in die Komponenten url und title
	      aufspalten und den zugeh�rigen Arrays zuf�gen.
	    */
	    int i = 0;
	    for (Enumeration e = hits.elements() ; e.hasMoreElements() ;) {
		StringTokenizer urlTitle = new StringTokenizer((String) e.nextElement(), SEPARATOR);
		titles[i] = urlTitle.nextToken();
		urls[i] = urlTitle.nextToken();
		i++;
	    }
	}
    } // getMatches()
    
    // Methode gibt ein Array mit den URLs der Treffer zur�ck.
    public String[] getURLs() {
	return urls;
    }
    
    // Methode gibt ein Array mit den Titeln der Treffer zur�ck.
    public String[] getTitles() {
	return titles;
    }

    // Methode gibt Anzhal der URLs (=Anzahl der Titel) im Array zur�ck.
    public int getUrlCount() {
	return urls.length;
    }
} // class ReadIndex
