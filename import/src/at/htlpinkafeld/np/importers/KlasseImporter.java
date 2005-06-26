/*
 * KlasseImporter.java
 *
 * Created on 27. Mai 2005, 10:15
 */

/*

npImport - Einlesen-Programm f�r Nachpr�fungsplanung
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

import com.Ostermiller.util.*;
import java.io.*;
import java.util.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.util.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Die Klasse KlasseImporter wird dazu verwendet, um 
 * die Namen der Klassen von den Textdateien in ein 
 * Format zu bekommen, das vom Programm weiterverarbeitet 
 * werden kann.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class KlasseImporter implements Databaseable {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Klasse> klassen = null;
    
    private boolean hasRead = false; // Wurde die read-Funktion bereits aufgerufen?
    
    private int KLASSE = 4;
    
    /**
     * Erstellt einen neuen KlasseImporter, mit der 
     * Daten aus der GPU002 Datei importiert werden 
     * k�nnen.
     *
     * @param filename Dateiname der GPU002 Datei
     **/
    public KlasseImporter( String filename) throws FileNotFoundException, IOException {
        this.filename = filename;
        
        ConfigManager cm = ConfigManager.getInstance();
        try
        {
            KLASSE = Integer.parseInt( cm.getProperty( this, "spalte-klassenname", Integer.toString( KLASSE)));
        }
        catch( NumberFormatException nfe)
        {
            Logger.warning( this, "Fehler beim Lesen von Konfigurationseigenschaften.");
        }
        
        parser = new CSVParser( new BufferedReader( new FileReader( filename)));
        klassen = new Vector<Klasse>();
    }
    
    /**
     * Liest alle Zeilen aus der GPU002 Datei aus 
     * und speichert die relevanten Daten in einer 
     * internen Struktur f�r die sp�tere Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * �ffnen der Datei aufgerufen werden, nach 
     * dem ersten Aufruf wird ein flag gesetzt, so 
     * dass diese Funktion bei einem weiteren 
     * Aufruf sofort einen Return-Wert liefert.
     **/
    public void readKlassen() throws IOException {
        // Wenn wir bereits eingelesen haben, einfach beenden.
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese Klassen aus " + filename);
        
        String [] line = null;
        
        while( (line = parser.getLine()) != null)
        {
            Klasse k = new Klasse( line[KLASSE]);
            if( k.isValid() && !Klasse.klasseExists( klassen, k))
                klassen.add( k);
        }
        
        // Klassen alphabetisch sortieren:
        Klasse.sort( klassen, true);
        
        // Wir haben es gelesen..
        hasRead = true;
        
        Logger.progress( this, "Import von Klasse-Objekten fertig.");
    }
    
    /**
     * Sucht nach der UID einer Klasse. Der Name der 
     * Klasse wird als Parameter mitgegeben (zB 4ADV).
     * Dies ist in Wirklichkeit nur eine Durchreichefunktion 
     * f�r Klasse.findKlasse(). F�r weitere Informationen 
     * siehe dort.
     *
     * @param name Name der Klasse, nach der gesucht werden soll
     * @return siehe Klasse.findKlasse()
     **/
    public int findKlasse( String name) {
        return Klasse.findKlasse( klassen, name);
    }
    
    /**
     * Holt eine Klasse aus der Klassen-Tabelle.
     * Als �bergabeparameter wird die UID der Klasse
     * verwendet. Die UID einer Klasse kann zB mit 
     * der Funktion getKlasse() des Schueler-Objekts 
     * oder der Funktion findKlasse() herausgefunden 
     * werden.
     * 
     * @param uid Die UID der zu holenden Klasse
     * @return Klasse-Objekt mit der passenden UID (oder null, wenn nicht gefunden)
     **/
    public Klasse getKlasseByUid( int uid) {
        Klasse result = null;
        
        for( int i=0; i<klassen.size(); i++)
        {
            Klasse k = klassen.get(i);
            
            if( k.getUid() == uid)
                result = k;
        }
        
        return result;
    }
    
    /**
     * Pr�ft, ob eine Klasse mit einem bestimmten Namen existiert
     * oder eben nicht. Diese Funktion kann dazu verwendet 
     * werden, um im RelationGegenstandLehrerImporter abzupr�fen, 
     * ob eine Klasse wirklich existiert, bzw ob etwas eine 
     * Klasse ist oder nicht.
     *
     * @param name Der Name einer Klasse, die auf Existenz gepr�ft werden soll
     * @return true, wenn die Klasse existiert, ansonsten false
     **/
    public boolean klasseExists( String name) {
        for( int i=0; i<klassen.size(); i++)
        {
            if( klassen.get(i).getName().equals( name))
                return true;
        }
        
        return false;
    }
    
    /**
     * Liefert die Klassen-Tabelle.
     * 
     * @return Vektor mit allen Klassen
     **/
    public Vector<Klasse> getKlassen() 
    {
        return klassen;
    }
    
    /**
     * Gibt die Liste der Klassen auf System.out aus.
     * Diese Funktion ist f�r Debugging-Zwecke n�tzlich.
     **/
    public void printDebug() {
        for( int i=0; i<klassen.size(); i++)
            System.out.println( klassen.get( i));
    }

    /**
     * Schreibt alle Daten dieses Importers in 
     * die Datenbank.
     *
     * @return true, wenn erfolgreich, ansonsten false
     **/
    public boolean allToDatabase() {
        DatabaseTool db = DatabaseTool.getInstance();
        
        Logger.progress( this, "Schreibe alle Klassen in die Datenbank.");
        
        // Klassen-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.KLASSE));
        
        // Klassen in die Datenbank schreiben
        for( int i=0; i<klassen.size(); i++)
        {
            // Klasse aus der Tabelle holen
            Klasse k = klassen.get(i);
            
            // Klasse in die Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)k) == false)
            {
                Logger.warning( this, "Fehler bei Klasse: " + k);
                return false;
            }
        
        }
        Logger.progress( this, "Alle Klassen erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Liefert eine Beschreibung der Daten, die 
     * von diesem Importer in die Datenbank 
     * geschrieben werden.
     *
     * @return String, der die Datenbank-Daten beschreibt
     **/
    public String getDescription() {
        return "Klassen";
    }
    
}
