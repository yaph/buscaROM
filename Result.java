/*
  Klasse zum Darstellen der Suchergebnisse, die von der Klasse
  SearchApplet aufgerufen wird.
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

    /**
       Die einzelnen Objekte des Vektors 'matches' mit einer StringTokenizer Instanz
       in die beiden Komponenten 'url' und 'title' aufspalten und in die zugehörigen
       Arrays 'urls' bzw. 'titles' einfügen. Das 'titles'-Array ist dann in der Liste
       sichtbar. Bei Klick auf einen der Titel wird der zugehörige URL ausgewählt und
       in dem aktuellen Browser-Fenster geöffnet.
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
    }
    
    private class URLChoice implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    chosenURL = urlList.getSelectedIndex();
	}
    }
} // class Result
