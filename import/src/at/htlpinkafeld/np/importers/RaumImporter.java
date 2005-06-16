/*
 * RaumImporter.java
 *
 * Created on 10. M�rz 2005, 16:51
 */

package at.htlpinkafeld.np.importers;

import java.io.*;
import java.util.*;
import com.Ostermiller.util.*;

import at.htlpinkafeld.np.devel.*;
import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.util.*;

/**
 * Die Klasse RaumImporter importiert Raumdaten aus der 
 * GPU005 Datei. Diese R�ume werden ben�tigt, damit Pr�fungen 
 * in diesen R�umen stattfinden k�nnen.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RaumImporter implements Databaseable {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Raum> rooms = null;
    private int uid = 0;
    
    private static final int BEZEICHNUNG = 0;
    private static final int TYP = 1;
    
    private boolean hasRead = false; // Wurde die read-Funktion bereits aufgerufen?
    
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
     * �ffnen der Datei aufgerufen werden! Danach 
     * returnt diese Funktion gleich ohne etwas
     * neues einzulesen.
     */
    public void readRooms() throws IOException
    {
        String [] line = null;
        
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese R�ume aus " + filename);
        
        while( (line = parser.getLine()) != null)
        {
            Raum r = new Raum( 0, line[BEZEICHNUNG], line[TYP]);
            
            if( r.isValid())
            {
                r.setUid( ++uid);
                Logger.debug( this, "Raum hinzugef�gt: " + r);
                rooms.add( r);
            }
        }
        
        Logger.progress( this, "Import von R�umen fertig.");
        hasRead = true;
    }
    
    /**
     * Liefert einen Vektor mit allen eingelesenen
     * R�umen zur�ck.
     *
     * @return Vektor mit allen R�umen
     **/
    public Vector<Raum> getRooms() {
        return rooms;
    }

    /**
     * Schreibt alle Daten dieses Importers, die 
     * f�r die Datenbank bestimmt sind in die Datenbank.
     *
     * @return true, wenn erfolgreich, ansonsten false
     **/
    public boolean allToDatabase() {
        DatabaseTool db = DatabaseTool.getInstance();
        
        Logger.progress( this, "Schreibe alle R�ume in die Datenbank.");
        
        // R�ume-Tabelle leeren
        db.emptyTable( DatabaseMetadata.T_RAUM);
        
        // R�ume in die Datenbank schreiben
        for( int i=0; i<rooms.size(); i++)
        {
            // Raum aus der Tabelle holen
            Raum r = rooms.get(i);
            
            // Raum in die Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)r) == false)
            {
                Logger.warning( this, "Fehler bei Raum: " + r);
                return false;
            }
        }
        
        Logger.progress( this, "Alle R�ume erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Liefert eine Beschreibung der Daten, die dieses 
     * Databaseable Objekt in die Datenbank schreibt.
     *
     * @return String, der die Datenbank-Daten beschreibt
     **/
    public String getDescription() {
        return "R�ume";
    }

}
