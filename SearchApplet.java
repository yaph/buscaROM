/**
   Das Applet für die Suche im Eurodelphes Index. Der Nutzer kann
   zwischen den Optionen 'All Words' (logisches UND) und 'Any Word'
   (logisches ODER) zur Verknüpfungen der Suchbegriffe wählen.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays; // Zum Sortieren, erst ab JDK 1.2
import java.util.Hashtable;
import java.net.URL;

public class SearchApplet extends Applet implements ActionListener {

    private Choice searchOptions;
    private TextField searchField;
    private Button searchButton;
    private String chosenOpt = "AND"; // 'AND' default Option
    private Result result;
    private String[] urls, titles;
    private URL baseDir;

    public void init() {
	baseDir = getCodeBase();

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
	searchButton.setBounds(210,5,65,30);
	searchButton.addActionListener(this);
	add(searchButton);

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
		    ReadIndex ri = new ReadIndex(baseDir, words.nextToken());
		    ri.getMatches();
		    int uCount = ri.getUrlCount();// Anzahl der URLs (=Titel)

		    // Wenn uCount '0' ist, dann ist das Array leer
		    if (uCount > 0) {
			urlCount[i] = uCount;
			urlVec.add(ri.getURLs());
			titleVec.add(ri.getTitles());
			totalUrlCount += urlCount[i];
		    }
		}

		// Nur wenn Ergebnisvektor nicht leer ist
		if (!urlVec.isEmpty()) {

		    // UND Suche
		    if (chosenOpt.equals("AND")) {
			/*
			  Das kürzeste Array finden und nur die Elemente
			  dieses Arrays mit den Elementen der anderen Vergleichen.
			  Was im Gesamtergebnis ist muss auch im kürzesten Array sein.
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
			  Die Elemente des kürzesten Arrays werden in der Hashtable
			  cmpHash gespeichert. Die 'Keys' sind die URLs, da sie eindeutig sind.
			  Jedes Element aller 'urls'-Arrays (mit Ausnahme des Kürzesten) wird
			  geprüft, ob es in cmpHash vorhanden ist. Ist es vorhanden wird der
			  aktuelle URL und der dazugehörige Titel in resultHash gespeichert.
			  Ist nach der 1. Iteration kein Eintrag in resultHash vorhanden, wird
			  die Suche beendet, da kein Dokument die ersten beiden verglichenen
			  Suchbegriffe enthält. Weitere Suchbegriffe sind dann unerheblich.
			*/
			Hashtable cmpHash = new Hashtable();
			Hashtable resultHash = new Hashtable();
			String[] minArray = new String[min];
			minArray = (String[]) urlVec.elementAt(minAr);

			// Hash mit URLs aus minArray füllen
			for (int i = 0; i < min; i++) {
			    cmpHash.put(minArray[i], "1"); // Nur an den Keys interessiert
			}

			/*
			   Flag 'result'. Zunächst wird davon ausgegangen, dass es ein
			   Ergebnis gibt. Wenn nicht wird der Vergleich beendet. 'cmpCount'
			   wird um 1 erhöht, wenn ein Array mit den Hashkeys verglichen
			   wurde. Ist 'cmpCount' größer 0 und resultHash leer, wird der
			   Vergleich beendet, da kein Dokument alle Suchbegriffe enthalten
			   kann.
			*/
			boolean result = true;
			int cmpCount = 0;

			// Vergleich der anderen Arrays mit cmpHash
			for (int i = 0; i < urlCount.length; i++) {
			    if (result) { // Vergleich ausführen
				if (i != minAr) { // kürzestes Array auslassen
				    /*
				       Wenn schon ein Vergleich dürchgeführt wurde und
				       'resultHash' nicht leer ist.
				    */
				    if (cmpCount > 0 && resultHash.size() != 0) {
					String[] urlAr = new String[urlCount[i]];
					urlAr = (String[]) urlVec.elementAt(i);

					cmpCount++;
					for (int j = 0; j < urlAr.length; j++) {
					    /*
					      Wenn 'Key' in cmpHash aber nicht in resultHash
					      vorhanden, 'key' aus 'cmpHash'. Sonst so belassen.
					    */
					    if (cmpHash.containsKey(urlAr[j]) && !resultHash.containsKey(urlAr[j])) {
						cmpHash.remove(urlAr[j]);
					    }
					} // for (int j = 0; j < urlAr.length; j++)
				    } // if (cmpCount > 0 && resultHash.size != 0)

				    /*
				      Wenn noch kein Vergleich dürchgeführt wurde über alle
				      Elemente des Arrays iterieren. Wenn ein Element in 'cmpHash'
				      vorhanden ist, dann 'url' und 'Title' in resultHash speichern.
				    */
				    else if (cmpCount == 0) {
					String[] urlAr = new String[urlCount[i]];
					String[] titleAr = new String[urlCount[i]];
					urlAr = (String[]) urlVec.elementAt(i);
					titleAr = (String[]) titleVec.elementAt(i);

					cmpCount++;
					for (int j = 0; j < urlAr.length; j++) {
					    /*
					      Wenn 'Key' in cmpHash dann 'url' und 'title' in
					      resultHash speichern.
					    */
					    if (cmpHash.containsKey(urlAr[j])) {
						resultHash.put(urlAr[j], titleAr[j]);
					    }
					}
				    } // else if (cmpCount == 0)

				    else { // sonst kein Treffer
					result = false;
				    } // else
				} // if (i != minAr)
			    } // if (result)
			} // for (int i = 0; i < urlCount.length; i++)

			/*
			  Wenn es ein Ergebnis gibt die 'keys' von resultHash
			  im Array 'urls' speichern und die 'values' im Array
			  'titles'.
			*/
			if (result) {
			    String[] urlAr = new String[min];
			    String[] titleAr = new String[min];
			    urlAr = (String[]) urlVec.elementAt(minAr);
			    titleAr = (String[]) urlVec.elementAt(minAr);

			    /*
			      Ergebnisarrays füllen. Da es keine Methode gibt, die
			      alle 'keys' zurück gibt, werden die Elemente des kürzesten
			      Arrays 'urlAr[minAr]' mit den 'keys' in resultHash vergleichen.
			      Wenn ein Element als 'key' vorhanden ist, wird der 'key' als
			      in Ergebnisarray 'urls' und der 'value' im Ergebnisarray
			      'titles' gespeichert.
			    */
			    urls = new String[resultHash.size()];
			    titles = new String[resultHash.size()];
			    int resultCount = 0; // nicht unbedingt gleich 'i' in for Schleife
			    for (int i = 0; i < urlAr.length; i++) {
				if (resultHash.containsKey(urlAr[i])) {
				    urls[resultCount] = urlAr[i];
				    titles[resultCount] = (String) resultHash.get(urlAr[i]);
				    resultCount++;
				}
			    }
			} // if (result)

			else { // bei 0 Treffern leere Arrays
			    urls = new String[0];
			    titles = new String[0];
			}

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
		    } // OR
		} // if (urlVec.capacity() > 1)

		else { // bei 0 Treffern leere Arrays
		    urls = new String[0];
		    titles = new String[0];
		}
	    }

	    else { // nur ein Suchwort AND, OR egal
		ReadIndex ri = new ReadIndex(baseDir, searchTerm);
		ri.getMatches();
		urls = ri.getURLs();
		titles = ri.getTitles();
	    }

	    // Ergebnisfenster
	    result = new Result(getAppletContext(),this); // this = aktuelles Applet
	    result.setVisible(true);
	} // if (searchTerms.length() > 0)
    } // public void actionPerformed (ActionEvent e)


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
