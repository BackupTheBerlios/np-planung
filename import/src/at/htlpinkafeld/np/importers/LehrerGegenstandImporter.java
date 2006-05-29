/*
 * GegenstandImporter.java
 *
 * Created on 10. März 2005, 18:45
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
import javax.swing.table.*;
import com.Ostermiller.util.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;
import at.htlpinkafeld.np.util.*;

/**
 * Importiert Lehrer und Gegenstände aus der GPU008-Datei.
 * Das sind die Stammdaten, die dazu verwendet werden, 
 * um dem RelationGegenstandLehrerImporter zu sagen, welche 
 * Daten in der GPU002 Datei richtig sind und als Lehrer 
 * bzw Gegenstand zu verwenden sind.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class LehrerGegenstandImporter {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Gegenstand> gegenstaende = null;
    private Vector<Lehrer> lehrer = null;
    private Vector<Lehrer> lehrer_relation = null;  //Lehrer werden 1:1 aus GPU008 importiert  
    private Vector<Gegenstand> gegenstand_relation = null;  //Gegenstände werden 1:1 aus GPU008 importiert
    private int uid = 0;
    
    private boolean hasRead = false; // Wurde die read-Funktion bereits aufgerufen?
    
    private int LEHRER = 0;
    private int GEGENSTAND = 1;
    
    /**
     * Erstellt einen neuen LehrerGegenstandImporter, mit dem 
     * Daten aus der GPU008 Datei importiert werden.
     *
     * @param filename Der Dateiname zur GPU008 Datei
     */
    public LehrerGegenstandImporter( String filename) throws FileNotFoundException, IOException {
        this.filename = filename;

        ConfigManager cm = ConfigManager.getInstance();
        try
        {
            LEHRER = Integer.parseInt( cm.getProperty( this, "spalte-lehrername", Integer.toString( LEHRER)));
            GEGENSTAND = Integer.parseInt( cm.getProperty( this, "spalte-gegenstand", Integer.toString( GEGENSTAND)));
        }
        catch( NumberFormatException nfe)
        {
            Logger.warning( this, "Fehler beim Lesen von Konfigurationseigenschaften.");
        }
        
        parser = new CSVParser( new BufferedReader( new FileReader( filename)));
        gegenstaende = new Vector<Gegenstand>();
        lehrer = new Vector<Lehrer>();
        gegenstand_relation = new Vector<Gegenstand>();
        lehrer_relation = new Vector<Lehrer>();
    }
    
    /**
     * Liest alle Zeilen aus der GPU008 Datei und 
     * speichert die relevanten Felder in eine 
     * interne Struktur zur Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * Öffnen der Datei aufgerufen werden! Danach 
     * returnt die Funktion einfach, ohne etwas 
     * zu machen.
     */
    public void readLehrerGegenstaende() throws IOException
    {
        // Wenn wir schon gelesen haben, einfach returnen
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese Lehrer+Gegenstand (nur Test-Daten) aus " + filename);
        
        String [] line = null;
        Vector store = null;
        Vector olditem = null;
        
        while( (line = parser.getLine()) != null)
        {
            Gegenstand g = new Gegenstand( line[GEGENSTAND]);
            Lehrer l = new Lehrer( line[LEHRER]);
            
            /**
             * Gegenstände und Lehrer werden 1:1 aus GPU008
             * in die Datenbank geschrieben.
             * Sie werden für Beisitzerzuteilung im Algorithmus
             * verwendet (einlesen.mdb --> tab_lehrer_gegenstand).
             **/
            if( g.isValid() && l.isValid())
            {
                gegenstand_relation.add(g);
                lehrer_relation.add(l);
            }
                
            
            /**
             * Wenn "g" ein passender Gegenstand ist, und noch 
             * nicht existiert, dann einfügen in Gegenstand-Tabelle.
             **/
            if( g.isValid() && !Gegenstand.gegenstandExists( gegenstaende, g))
                gegenstaende.add( g);
            
            /**
             * Wenn "l" ein passender Lehrer ist, und noch nicht 
             * existiert, dann einfügen in die Lehrer-Tabelle.
             **/
            if( l.isValid() && !Lehrer.lehrerExists( lehrer, l))
                lehrer.add( l);
        }
        
        // Gegenstände alphabetisch sortieren (und re-indexing):
        Gegenstand.sort( gegenstaende, true);
        
        // Lehrer alphabetisch sortieren (und re-indexing):
        Lehrer.sort( lehrer, true);
        
        hasRead = true;
        
        Logger.progress( this, "Import von Lehrer+Gegenstand (Test-Daten) fertig.");
    }

    /**
     * Liefert die Lehrertabelle zurück, die zuvor eingelesen werden muss.
     *
     * @return Vektor, der alle Lehrer enthält (gleiche Reihenfolge wie in gpu008)
     **/
    public Vector<Lehrer> getLehrer()
    {
        return lehrer_relation;
    }

    /**
     * Liefert die Gegenstandstabelle zurück, die zuvor eingelesen werden muss.
     *
     * @return Vektor, der alle Gegenstände enthält (gleiche Reihenfolge wie in gpu008)
     **/
    public Vector<Gegenstand> getGegenstand()
    {
        return gegenstand_relation;
    }
    
    /**
     * Überprüft, ob ein Lehrer-Name (4-stelliges 
     * Kürzel) gültig ist oder nicht. Diese Funktion 
     * wird vorallem vom RelationGegenstandLehrerImporter 
     * verwendet, um festzustellen, ob die Daten der 
     * GPU002 Datei korrekt sind oder nicht.
     * 
     * @param name Der Name des Lehrers (4-stelliges Kürzel)
     * @return true, wenn der Lehrer existiert, sonst false
     **/
    public boolean lehrerExists( String name) {
        for( int i=0; i<lehrer.size(); i++)
        {
            if( lehrer.get(i).getName().equals( name))
                return true;
        }
        
        return false;
    }
    
    /**
     * Überprüft, ob ein Gegenstand-Name gültig ist oder 
     * nicht. Diese Funktion wird vorallem von 
     * RelationGegenstandLehrerImporter 
     * verwendet, um festzustellen, ob die Daten der 
     * GPU002 Datei korrekt sind oder nicht.
     *
     * @param name Der Name des Gegenstands
     * @return true, wenn der Gegenstand gültig ist, sonst false
     **/
    public boolean gegenstandExists( String name) {
        for( int i=0; i<gegenstaende.size(); i++)
        {
            if( gegenstaende.get(i).getName().equals( name))
                return true;
        }
        
        return false;
    }
    
    /**
     * Gibt eine Debug-Liste auf der Logger-Ausgabe 
     * aus (eine Liste der Lehrer). Diese Funktion 
     * wird während der Entwicklung und für 
     * Debugging-Zwecke verwendet.
     **/
    public void debugListLehrer() {
        for( int i=0; i<lehrer.size(); i++)
            Logger.message( this, "Lehrer: " + lehrer.get(i));
    }

    /**
     * Gibt eine Debug-Liste auf der Logger-Ausgabe 
     * aus (eine Liste der Gegenstände). Diese Funktion 
     * wird während der Entwicklung und für Debugging-
     * Zwecke verwendet.
     **/
    public void debugListGegenstaende() {
        for( int i=0; i<gegenstaende.size(); i++)
            Logger.message( this, "Gegenstand: " + gegenstaende.get(i));
    }
    
    /**
     * Liefert eine Beschreibung (im Interface Databaseable)
     * für diese Klasse.
     *
     * @return Beschreibung der exportieren Daten (siehe Interface Databaseable)
     **/
    public String getDescription() {
        return "Gegenstände, Lehrer";
    }
}
