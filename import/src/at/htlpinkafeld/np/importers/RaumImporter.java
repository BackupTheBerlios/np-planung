/*
 * RaumImporter.java
 *
 * Created on 10. März 2005, 16:51
 */

/*

npImport - Einlesen-Programm für Nachprüfungsplanung
Copyright (c) 2005 Thomas Perl <thp@perli.net>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

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
 * GPU005 Datei. Diese Räume werden benötigt, damit Prüfungen 
 * in diesen Räumen stattfinden können.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RaumImporter implements Databaseable {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Raum> rooms = null;
    private int uid = 0;
    
    private int BEZEICHNUNG = 0;
    private int TYP = 1;
    
    private boolean hasRead = false; // Wurde die read-Funktion bereits aufgerufen?
    
    /**
     * Erstellt einen neuen RaumImporter, mit dem 
     * Daten aus der GPU005 Datei importiert werden.
     *
     * @param filename Der Dateiname zur GPU005 Datei
     */
    public RaumImporter( String filename) throws FileNotFoundException, IOException {
        this.filename = filename;

        ConfigManager cm = ConfigManager.getInstance();
        try
        {
            BEZEICHNUNG = Integer.parseInt( cm.getProperty( this, "spalte-bezeichnung", Integer.toString( BEZEICHNUNG)));
            TYP = Integer.parseInt( cm.getProperty( this, "spalte-typ", Integer.toString( TYP)));
        }
        catch( NumberFormatException nfe)
        {
            Logger.warning( this, "Fehler beim Lesen von Konfigurationseigenschaften.");
        }
        
        parser = new CSVParser( new BufferedReader( new FileReader( filename)));
        rooms = new Vector<Raum>();
    }
    
    /**
     * Liest alle Zeilen aus der GPU005 Datei und 
     * speichert die relevanten Felder in eine 
     * interne Struktur zur Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * Öffnen der Datei aufgerufen werden! Danach 
     * returnt diese Funktion gleich ohne etwas
     * neues einzulesen.
     */
    public void readRooms() throws IOException
    {
        String [] line = null;
        
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese Räume aus " + filename);
        
        while( (line = parser.getLine()) != null)
        {
            Raum r = new Raum( 0, line[BEZEICHNUNG], line[TYP]);
            
            if( r.isValid())
            {
                r.setUid( ++uid);
                Logger.debug( this, "Raum hinzugefügt: " + r);
                rooms.add( r);
            }
        }
        
        Logger.progress( this, "Import von Räumen fertig.");
        hasRead = true;
    }
    
    /**
     * Liefert einen Vektor mit allen eingelesenen
     * Räumen zurück.
     *
     * @return Vektor mit allen Räumen
     **/
    public Vector<Raum> getRooms() {
        return rooms;
    }

    /**
     * Schreibt alle Daten dieses Importers, die 
     * für die Datenbank bestimmt sind in die Datenbank.
     *
     * @return true, wenn erfolgreich, ansonsten false
     **/
    public boolean allToDatabase() {
        DatabaseTool db = DatabaseTool.getInstance();
        
        Logger.progress( this, "Schreibe alle Räume in die Datenbank.");
        
        // Räume-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.RAUM));
        
        // Räume in die Datenbank schreiben
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
        
        Logger.progress( this, "Alle Räume erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Liefert eine Beschreibung der Daten, die dieses 
     * Databaseable Objekt in die Datenbank schreibt.
     *
     * @return String, der die Datenbank-Daten beschreibt
     **/
    public String getDescription() {
        return "Räume";
    }

}
