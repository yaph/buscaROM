// Copyright 2002 - 2003, Ramiro Gómez
// Klasse, die das Ergebnishash nach Titeln sortiert als Array zurückgibt

import java.util.*;
public class SortResultHash {
    private String[] sorted; // Array zum Sortieren
    
    // Konstruktor
    public SortResultHash(int count, Hashtable hT) {
	sorted = new String[count];
	
	Enumeration e = hT.keys();
	int i = 0;
	while (e.hasMoreElements()) {
	    String temp = (String) e.nextElement();
	    sorted[i] = (String) hT.get(temp) + '|' + temp;
	    i++;
	}
	Arrays.sort(sorted); // Zum Sortieren, erst ab JDK 1.2
    } //public SortResultHash
    
    // Accessor
    public String[] getSorted() {
	return sorted;
    }
} // public class SortResultHash {
