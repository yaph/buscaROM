/*
  Klasse zum Darstellen der Suchergebnisse, die von der Klasse
  SearchApplet aufgerufen wird. Es handelt sich um eine Klasse,
  die die Klasse Frame erweitert.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.AppletContext;
import java.net.*;

class Result extends Frame implements ActionListener {
    private SearchApplet searchApplet;
    private int chosenURL;
    private Label label;
    private List urlList;
    private Button go;
    private AppletContext appletContext;
    private String[] urls, titles;
    
    /*
      Dem Konstruktor der Klasse werden der Applet-Kontext und
      das SearchApplet-Objekt als Argumente übergeben. Dann
      werden die Ergebnisarrays der Suche mit den Dateinamen
      (urls) und Titeln (titles) über die Methoden 'getURLs()'
      und getTitles des SearchApplet-Objekts in Arrays der Klasse
      Result gespeichert. Es wird ein Label erzeugt, das die Anzahl
      der gefundenen Dokumente enthält und an der oberen Seite
      des Frames sichtbar ist. In der Mitte befindet sich die
      Liste der Dokumenttitel, die die Suchbegriffe enthalten.
      Am unteren Ende befindet sich ein Button mit der Aufschrift
      'Show document'. Nach Betätigung des Buttons wird das
      ausgewählte Dokument im aktuellen Browserfenster geöffnet
      und die Instanz des Result-Objekts zerstört. Der Frame ist
      dann nicht mehr sichtbar.
    */
    public Result(AppletContext appletContext, SearchApplet searchApplet) {
	setLayout(new BorderLayout());
	setSize(600,500);
	this.appletContext = appletContext;
	this.searchApplet = searchApplet;
	urls = searchApplet.getURLs();
	titles = searchApplet.getTitles();
	final int TOTAL = urls.length;
	
	// Layout
	label = new Label(TOTAL + " documents found.", Label.CENTER);
	label.setBackground(Color.white);
	label.setSize(600,360);
	add(label, BorderLayout.NORTH);
	
	// Liste der Ergebnisse 20 Elemente sichtbar, eines auswählbar
	urlList = new List(20, false);
	urlList.setBackground(Color.white);
	
	if (titles != null) {
	    for (int i=0; i<TOTAL;i++) {
		urlList.add(titles[i]);
	    }
	    urlList.addItemListener(new URLChoice());
	}
	else {
	    urlList.add("No matches found.");
	}
	
	add(urlList, BorderLayout.CENTER);
	
	// Button
	go = new Button("Show document");
	go.addActionListener(this);
	add(go, BorderLayout.SOUTH);
	
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    setVisible(false);
		}
	    });
    } // Result()
    
    /*
      Nach Drücken des Buttons 'Show document' wird das
      gewählte Dokument im Browserfenster geladen.
    */
    public void actionPerformed(ActionEvent e) {
	URL url = null;
      	try {
	    url = new URL(searchApplet.getCodeBase(), urls[chosenURL]);
	} catch (MalformedURLException exception) {
	    System.err.println("Malformed URL: " + chosenURL);
	}
	
	if (url != null) {
	    appletContext.showDocument(url);
	}
    } //public void actionPerformed(ActionEvent e)
    
    private class URLChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    chosenURL = urlList.getSelectedIndex();
	}
    } // private class URLChoice implements ItemListener
} // class Result
