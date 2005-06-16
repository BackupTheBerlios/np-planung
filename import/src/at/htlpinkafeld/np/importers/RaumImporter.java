/*
 * RaumImporter.java
 *
 * Created on 10. März 2005, 16:51
 */

package at.htlpinkafeld.np.importers;

import java.io.*;
import java.util.*;
import com.Ostermiller.util.*;

import at.htlpinkafeld.np.devel.*;
import at.htlpinkafeld.np.model.*;

/**
 * Die Klasse RaumImporter importiert Raumdaten aus der 
 * GPU005 Datei. Diese Räume werden benötigt, damit Prüfungen 
 * in diesen Räumen stattfinden können.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RaumImporter {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Raum> rooms = null;
    private int uid = 0;
    
    private static final int BESCHREIBUNG = 0;
    private static final int TYP = 1;
    
    /**
     * Erstellt einen neuen RaumImporter, mit dem 
     * Daten aus der GPU005 Datei importiert werden.
     *
     * @param filename Der Dateiname zur GPU005 Datei
     */
    public RaumImporter( String filename) throws FileNotFoundException, IOException {
        this.filename = filename;
        
        parser = new CSVParser( new BufferedReader( new FileReader( filename)));
        rooms = new Vector<Raum>();
    }
    
    /**
     * Liest alle Zeilen aus der GPU005 Datei und 
     * speichert die relevanten Felder in eine 
     * interne Struktur zur Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * Öffnen der Datei aufgerufen werden!
     */
    public void readRooms() throws IOException
    {
        String [] line = null;
        Vector store = null;
        
        Logger.progress( this, "Lese Räume aus " + filename);
        
        while( (line = parser.getLine()) != null)
        {
            if( line[BESCHREIBUNG] != "")
            {
                Raum r = new Raum( ++uid, line[BESCHREIBUNG], line[TYP]);
                
                rooms.add( r);
            }
        }
        
        Logger.progress( this, "Import von Räumen fertig.");
    }

}
