/*
   Das Applet f�r die Suche im EURODELPHES Index. Das Applet besteht 
   aus einem Texteingabefeld, einer Auswahlliste, �ber die der Nutzer
   zwischen den Optionen 'All Words' (logisches UND) und 'Any Word'
   (logisches ODER) zur Verkn�pfungen der Suchbegriffe w�hlen kann und
   einem Button zum Senden der Suchanfrage. Nach Senden der Suchanfrage
   wird zun�chst gepr�ft, ob etwas eingegeben wurde. Wenn ja wird gepr�ft,
   ob mehr als ein Suchwort eingegeben wurde, was der Fall ist, wenn der
   Suchstring ein Leerzeichen enth�lt. Der String wird in diesem Fall in
   die einzelnen W�rter zerlegt und f�r jedes Wort wird ein ReadIndex Objekt
   erzeugt, dass Methoden enth�lt, die Stringarrays der URLs und der Titel
   der Dokumente zur�ckgeben, die den Suchbegriff enthalten. Je nach 
   Verkn�pfung der Suchbegriffe (AND oder OR) wird entweder die 
   Schnittmenge (AND) der zur�ckgegebenen Arrays ermittelt oder die
   Vereinigungsmenge (OR). Wurde nur ein Wort eingegeben wird ein
   ReadIndex Objekt erzeugt, dass das gew�nschte Ergebnis ohne weitere
   Verarbeitung liefert.
   Zur Ausgabe der Ergebnisse wird ein Result Objekt
   erzeugt.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URL;

public class SearchApplet extends Applet implements ActionListener {
	private Choice searchOptions;
   private TextField searchField;
   private Button searchButton;
   private String chosenOpt = "AND"; // 'AND' default Option
   private Result result;
   private URL baseDir;
   private String[] urls, titles; // sp�tere Endergebnisse
    
   public void init() {
		baseDir = getCodeBase();
		setBackground(Color.yellow);
		/*
	  		Individuelle Positionierung der grafischen Komponenten
	  		(Layout des Applet).
		*/
		setLayout(null);
		// Texteingabefeld
		searchField = new TextField(20);
		searchField.setBackground(Color.white);
		searchField.setBounds(5,5,200,30);
		searchField.addActionListener(this);
		add(searchField);

		/*
		  Auswahlliste Suchoptionen
	  	Phrasensuche aufgrund des Indexes nicht m�glich
		*/
		searchOptions = new Choice();
		searchOptions.setBackground(Color.gray);
		searchOptions.setBounds(105,40,100,30);
		searchOptions.addItem("AND");
		searchOptions.addItem("OR");
		searchOptions.addItemListener(new OptionChoice());
		add(searchOptions);

		// Suchbutton
		searchButton = new Button("Search!");
		searchButton.setBackground(Color.red);
		searchButton.setBounds(210,5,65,30);
		searchButton.addActionListener(this);
		add(searchButton);
	} // init()

   // Resultframe schlie�en und Result Instanz zerst�ren
	public void destroy() {
   	result.setVisible(false);
   	result = null;
	}
	
   // Reaktion auf Benutzerinteraktion (Buttonklick)
   public void actionPerformed (ActionEvent e) {
		// String aus dem Texteingabefeld in searchTerm speichern
		String searchTerm = searchField.getText();
		// mehrere Suchbegriffe sind durch Leerraum ' ' getrennt.
		final String WS = " ";
	
		// Nur, wenn etwas eingegeben wurde, reagieren.
		if (searchTerm.length() > 0) {
			
			/*
	   		Wenn mehr als ein Suchwort, den Gesamtstring in einzelne
	      	W�rter zerlegen, Trennzeichen ist Leerraum ' '.
	 		*/
	   	if (searchTerm.indexOf(WS) > 0) {
				StringTokenizer words = new StringTokenizer(searchTerm, WS);
				final int TOTAL = words.countTokens(); // Anzahl der Suchw�rter
				int[] urlCount = new int[TOTAL];
				int totalUrlCount = 0;
				Vector urlVec = new Vector();
				Vector titleVec = new Vector();
			
				/*
			   	F�r jedes Suchwort die Ergebnissarrays holen und zum den
			   	Ergebnisvektoren 'urlVec' und 'titleVec' zuf�gen.
				*/
				for (int i = 0; i < TOTAL; i++) {
					// ReadIndex Objekt erzeugen.
				   ReadIndex ri = new ReadIndex(baseDir, words.nextToken());
				   ri.getMatches();
					// Anzahl der URLs (=Titel).
		    		int uCount = ri.getUrlCount();
					
					// Wenn uCount '0' ist, dann ist das Array leer.
		    		if (uCount > 0) {
						urlCount[i] = uCount;
						urlVec.add(ri.getURLs());
						titleVec.add(ri.getTitles());
						totalUrlCount += urlCount[i];
		    		}
				} // for (int i = 0; i < TOTAL; i++)
				
				// Nur, wenn Ergebnisvektor nicht leer ist.
				if (!urlVec.isEmpty()) {
					
					// UND Suche
			   	if (chosenOpt.equals("AND")) {
						/*
							Das k�rzeste Array finden und nur die Elemente
				  			dieses Arrays mit den Elementen der anderen vergleichen.
				  			Was im Gesamtergebnis ist, muss auch im k�rzesten Array sein.
						*/
						int minAr = 0; // Zun�chst erstes Array auf minimum gestetzt
						int min = 1000; // hoher Anfangswert
					
						for (int i = 0; i < urlCount.length; i++) {
				   			
				   		if (urlCount[i] > 0 && urlCount[i] < min) {
					 			min = urlCount[i];
					 			minAr = i;
				    		}
						}
						/*
			  				Die Elemente des k�rzesten Arrays werden in der Hashtable
			  				cmpHash gespeichert. Die 'Keys' sind die URLs, da sie eindeutig sind.
			  				Jedes Element aller 'urls'-Arrays (mit Ausnahme des k�rzesten) wird
			  				gepr�ft, ob es in cmpHash vorhanden ist. Ist es vorhanden wird der
			  				aktuelle URL und der dazugeh�rige Titel in resultHash gespeichert.
			  				Ist nach der 1. Iteration kein Eintrag in resultHash vorhanden, wird
			  				die Suche beendet, da kein Dokument die ersten beiden verglichenen
			  				Suchbegriffe enth�lt. Weitere Suchbegriffe sind dann unerheblich.
						*/
						Hashtable cmpHash = new Hashtable();
						Hashtable resultHash = new Hashtable();
						String[] minArray = new String[min];
						minArray = (String[]) urlVec.elementAt(minAr);
						
						// Hash mit URLs aus minArray f�llen
						for (int i = 0; i < min; i++) {
				   		cmpHash.put(minArray[i], "1"); // Nur an den Keys interessiert
						}
						/*
			   			Flag 'result'. Zun�chst wird davon ausgegangen, dass es ein
			   			Ergebnis gibt. Wenn nicht wird der Vergleich beendet. 'cmpCount'
			   			wird um 1 erh�ht, wenn ein Array mit den Hashkeys verglichen
			   			wurde. Ist 'cmpCount' gr��er 0 und resultHash leer, wird der
			   			Vergleich beendet, da kein Dokument alle Suchbegriffe enthalten
			   			kann.
						*/
						boolean result = true;
						int cmpCount = 0;
						
						// Vergleich der anderen Arrays mit cmpHash
						for (int i = 0; i < urlCount.length; i++) {
				   		
				   		if (result) { // Vergleich ausf�hren
								
					
								if (i != minAr) { // k�rzestes Array auslassen
									/*
										Wenn noch kein Vergleich d�rchgef�hrt wurde �ber alle
					      			Elemente des Arrays iterieren. Wenn ein Element in 'cmpHash'
					      			vorhanden ist, dann 'url' und 'Title' in 'resultHash' speichern.
					    			*/
					   			if (cmpCount == 0) {
										String[] urlAr = new String[urlCount[i]];
										String[] titleAr = new String[urlCount[i]];
										urlAr = (String[]) urlVec.elementAt(i);
										titleAr = (String[]) titleVec.elementAt(i);
										cmpCount++;
										for (int j = 0; j < urlAr.length; j++) {
					    					
					    					/*
					      					Wenn 'key' in 'cmpHash' dann 'url' und 'title' in
					      					'resultHash' speichern.
					    					*/
					    					if (cmpHash.containsKey(urlAr[j])) {
												resultHash.put(urlAr[j], titleAr[j]);
					    					}
										}
				    				} // if (cmpCount == 0)				
									
									/*
				       				Wenn schon ein Vergleich d�rchgef�hrt wurde und
				       				'resultHash' nicht leer ist.
				    				*/
				    				else if (cmpCount > 0 && resultHash.size() != 0) {
				   					// aktuelles Array der URLs
				   					String[] urlAr = new String[urlCount[i]];
										urlAr = (String[]) urlVec.elementAt(i);
										/*
					   	 				neues Hash mit URLs aus urlArray f�llen,
					   	 				damit die 'keys' aus resultHash mit den
					   	 				'keys' aus currentHash verglichen werden
					   	 				k�nnen.
					   				*/
				   					Hashtable currentHash = new Hashtable();
						
										for (int j = 0; j < urlAr.length; j++) {
			    							currentHash.put(urlAr[j], "1"); // Nur an den Keys interessiert
										}
										/*
						  					F�r jeden 'key' des resultHash, das den
						  					aktuellen Vorrat an Dokumenten abbildet,
						  					die alle Suchw�rter enthalten, wird gepr�ft,
						  					ob das aktuelle currentHash den 'key' enth�lt.
						  					Wenn nicht, wird er aus resultHash entfernt.
										*/
										Enumeration en = resultHash.keys();
										
										while (en.hasMoreElements()) {
											// aktuelles Element
											Object currentElement = en.nextElement();
											if ( !currentHash.containsKey(currentElement) ) {
	 											resultHash.remove(currentElement);        				 	
   	      				 			}
     									} // while (en.hasMoreElements())     					
     								} // else if (cmpCount > 0 && resultHash.size != 0)
					    		
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
							/*
			      			Ergebnisarrays mit den 'keys' und 'values'
			      			von resultHash f�llen.
			    			*/
							int resultCount = resultHash.size(); // Anzahl der Ergebnisse
							urls = new String[resultCount];
			    			titles = new String[resultCount];
			    			Enumeration eUrl = resultHash.keys();
			    			Enumeration eTitle = resultHash.elements();
							for (int i = 0; i < resultCount; i++) {
			    				urls[i] = (String) eUrl.nextElement();	
			    				titles[i] = (String) eTitle.nextElement();
			    			}
						} // if (result)
						
						else { // bei 0 Treffern leere Arrays
				   		urls = new String[0];
				   		titles = new String[0];
						}
					} // UND Suche
					
					else { // OR
						/*
							Die Ergebnisarrays, die von ReadIndex
							zur�ckgegeben wurden, werden aneinander-
							gef�gt.
						*/
						urls = new String[totalUrlCount];
						titles = new String[totalUrlCount];
						int counter = 0;
						
						for (int i = 0; i < TOTAL; i++) {
			    			// Interims Stringarrays
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
					} // OR
				} // if (!urlVec.isEmpty())
				
				else { // bei 0 Treffern leere Arrays
					urls = new String[0];
			    	titles = new String[0];
				}
			} // (searchTerm.indexOf(WS) > 0)
			
			else { // nur ein Suchwort AND, OR egal
				ReadIndex ri = new ReadIndex(baseDir, searchTerm);
				ri.getMatches();
				urls = ri.getURLs();
				titles = ri.getTitles();
			}
			
			/*
				Erzeugen einer Result Instanz zum Dartsellen des Suchergebnisses. Der
		   	aktuelle Appletkontext und das SearchApplet Objekt werden dem
	   		Konstruktor der Klasse Result �bergeben, damit die Verweise
	   		im Ergebnisfenster funktionieren.
			*/
			result = new Result(getAppletContext(),this); // this = aktuelles Applet
			result.setVisible(true);
		} // if (searchTerms.length() > 0)
	} // public void actionPerformed (ActionEvent e)

	/*
   	Innere Klasse, in die Auswahl der Suchoption durch
      den Nutzer der Variablen chosenOpt zugewiesen wird.
	*/
   private class OptionChoice implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
	   	chosenOpt = e.getItem().toString();
		}
   }

   // Methode, die das Stringarray mit den URLs zur�ckgibt
   public String[] getURLs() {
		return urls;
   }

   // Methode, die das Stringarray mit den Titeln zur�ckgibt
   public String[] getTitles() {
		return titles;
   }
} // class SearchApplet
