/*
 * GegenstandImporter.java
 *
 * Created on 10. M�rz 2005, 18:45
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

import java.io.*;
import java.util.*;
import javax.swing.table.*;
import com.Ostermiller.util.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;
import at.htlpinkafeld.np.util.*;

/**
 * Importiert Lehrer und Gegenst�nde aus der GPU002-Datei.
 * Diese Daten werden dazu ben�tigt, um passende Relationen 
 * zwischen Gegenstand, Lehrer und Klasse zu erstellen.
 * Die Daten der Lehrer und Gegenst�nde werden zus�tzlich 
 * noch vom LehrerGegenstandImporter aus der GPU008 Datei 
 * importiert, um sicher zu gehen, dass Lehrer und Gegenst�nde 
 * sinnvolle Daten beinhalten.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class RelationGegenstandLehrerImporter implements Databaseable {
    private String filename = null;
    private CSVParser parser = null;
    private Vector<Gegenstand> gegenstaende = null;
    private Vector<Lehrer> lehrer = null;
    private int uid = 0;
    private KlasseImporter ki = null;
    private LehrerGegenstandImporter lgi = null;
    private Vector<RelationGegenstandLehrerKlasse> relationen = null;
    
    private boolean hasRead = false; // Wurde die read-Funktion bereits aufgerufen?
    
    private int KLASSE = 4;
    private int LEHRER = 5;
    private int GEGENSTAND = 6;
    
    /**
     * Erstellt einen neuen RelationGegenstandLehrerImporter,
     * mit dem Daten aus der GPU002 Datei importiert werden.
     *
     * @param filename Der Dateiname zur GPU002 Datei
     * @param ki Ein KlasseImporter, der die Klassen-Objekte zur Verf�gung stellt
     * @param lgi Ein LehrerGegenstandImporter, der die Stammdaten zu Lehrer und Gegenstand bereith�lt
     */
    public RelationGegenstandLehrerImporter( String filename, KlasseImporter ki, LehrerGegenstandImporter lgi) throws FileNotFoundException, IOException {
        this.filename = filename;
        this.ki = ki;
        this.lgi = lgi;

        ConfigManager cm = ConfigManager.getInstance();
        try
        {
            KLASSE = Integer.parseInt( cm.getProperty( this, "spalte-klassename", Integer.toString( KLASSE)));
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
        relationen = new Vector<RelationGegenstandLehrerKlasse>();
    }
    
    /**
     * Liest alle Zeilen aus der GPU002 Datei und 
     * speichert die relevanten Felder in eine 
     * interne Struktur zur Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * �ffnen der Datei aufgerufen werden! Danach 
     * returnt die Funktion einfach, ohne etwas 
     * zu machen.
     */
    public void readLehrerGegenstaende() throws IOException
    {
        // tempor�re uids..
        int gegenstand_uid = 1;
        int lehrer_uid = 1;
        // ..tempor�re uids
        
        // Wenn wir schon gelesen haben, einfach returnen
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese Lehrer und Gegenst�nden (+Relationen) aus " + filename);
        
        String [] line = null;
        
        while( (line = parser.getLine()) != null)
        {
            // �berpr�fen, ob Lehrer Okay-ish ist..
            if( !lgi.lehrerExists( line[LEHRER]))
                continue;
            
            // �berpr�fen, ob Gegenstand Okay-ish ist..
            if( !lgi.gegenstandExists( Gegenstand.translateName( line[GEGENSTAND])))
                continue;
            
            // �berpr�fen, ob Klasse Okay-ish ist..
            if( !ki.klasseExists( line[KLASSE]))
                continue;
                
            // Klasse aus dem KlasseImporter heraussuchen und holen
            int klasse_uid = ki.findKlasse( line[KLASSE]);
            Klasse k = ki.getKlasseByUid( klasse_uid);

            // Pr�fen, ob die Klasse gefunden wurde..
            if( klasse_uid == -1)
            {
                Logger.warning( this, "Konnte f�r " + line[KLASSE] + " kein Klasse-Objekt finden.");

                // Warnmeldung ausgeben
                warnByLine( line);

                // Diesen Datensatz nicht weiter bearbeiten
                continue;
            }
            
            Lehrer l = new Lehrer( line[LEHRER]);

            // Wenn "l" ein passender Lehrer ist, wird er als solcher behandelt.
            if( l.isValid())
            {
                if( !Lehrer.lehrerExists( lehrer, l))
                {
                    l.setUid( lehrer_uid++);
                    lehrer.add( l);
                }
                else
                {
                    l = getLehrerByUid( Lehrer.findLehrer( lehrer, line[LEHRER]));
                }
            }
            else
            {
                Logger.warning( this, "Konnte den Lehrer nicht erzeugen (isValid() liefert false): " + line[LEHRER]);
                
                // Warnmeldung ausgeben
                warnByLine( line);
                
                // Diesen Datensatz nicht weiter bearbeiten
                continue;
            }

            Gegenstand g = new Gegenstand( Gegenstand.translateName( line[GEGENSTAND]));
            
            int gegenstand_gruppe = RelationGegenstandLehrerKlasse.findNextGruppeByKlasse( relationen, g, k);
            g.setGruppe( gegenstand_gruppe);
            
            /**
             * Nun schauen wir, dass wir den Gegenstand anlegen k�nnen, 
             * wenn er noch ned existiert.
             **/
            if( g.isValid())
            {
                if( !Gegenstand.gegenstandExists( gegenstaende, g))
                {
                    g.setUid( gegenstand_uid++);
                    gegenstaende.add( g);
                }
                else
                {
                    // Hole den richtigen Gegenstand
                    int gruppe = g.getGruppe();
                    g = getGegenstandByUid( Gegenstand.findGegenstand( gegenstaende, Gegenstand.translateName( line[GEGENSTAND]), gruppe));
                }
            }
            else
            {
                Logger.warning( this, "Gegenstand konnte nicht validiert werden: " + line[GEGENSTAND]);
                
                // Warnmeldung ausgeben
                warnByLine( line);
                
                // Diesen Datensatz nicht weiter bearbeiten
                continue;
            }
            
            /**
             * So weit so gut.. Wir haben die richtige Klasse, den 
             * richtigen Lehrer und auch den richtigen Gegenstand 
             * mit der passenden Gruppen-ID (falls zutreffend). 
             * Alles, was wir jetzt tun m�ssen, ist eine Relation 
             * zu erzeugen.
             **/
            if( !RelationGegenstandLehrerKlasse.relationExists( relationen, g, l, k))
            {
                Logger.debug( this, "Erstelle Relation (G-L-K): " + g + ", " + l + ", " + k + ".");
                RelationGegenstandLehrerKlasse relation_neu = new RelationGegenstandLehrerKlasse( g, l, k);
                relationen.add( relation_neu);
            }
        }
        
        // Gegenst�nde alphabetisch sortieren (und re-indexing):
        Gegenstand.sort( gegenstaende, true);
        
        // Lehrer alphabetisch sortieren (und re-indexing):
        Lehrer.sort( lehrer, true);
        
        hasRead = true;
        
        Logger.progress( this, "Import von Lehrer und Gegenst�nden (+Relationen) fertig.");
    }
    
    /**
     * Liefert die Gegenstandstabelle zur�ck, die zuvor mit 
     * readGegenstaende() eingelesen werden muss.
     *
     * @return Vektor, der die Gegenstandstabelle (als einen Vektor von Gegenst�nden) beinhaltet
     */
    public Vector<Gegenstand> getGegenstaende()
    {
        return gegenstaende;
    }
    
    /**
     * Liefert die Relationentabelle zur�ck, die zuvor mit 
     * eingelesen werden muss.
     *
     * @return Vektor, der alle Relationen enth�lt
     **/
    public Vector<RelationGegenstandLehrerKlasse> getRelationen()
    {
        return relationen;
    }
    
    /**
     * Liefert die Lehrertabelle zur�ck, die zuvor eingelesen werden muss.
     *
     * @return Vektor, der alle Lehrer enth�lt
     **/
    public Vector<Lehrer> getLehrer()
    {
        return lehrer;
    }
    
    /**
     * Liefert ein Lehrer-Objekt, welches man �ber 
     * die UID herausfinden kann. Solch ein Lehrer-
     * Objekt wird zum Beispiel daf�r ben�tigt, um 
     * ein bereits existierendes Lehrer-Objekt aus 
     * dem Lehrer-Vektor herauszulesen.
     *
     * @param uid UID des zu suchenden Lehrers
     * @return Lehrer-Objekt passend zur UID oder null, wenn nicht gefunden
     **/
    public Lehrer getLehrerByUid( int uid) {
        for( int i=0; i<lehrer.size(); i++)
        {
            Lehrer s = lehrer.get(i);
            
            if( s.getUid() == uid)
                return s;
        }
        
        return null;
    }
    
    /**
     * Gibt eine Debug-Liste auf der Logger-Ausgabe 
     * aus (eine Liste der Lehrer). Diese Funktion 
     * wird w�hrend der Entwicklung und f�r 
     * Debugging-Zwecke verwendet.
     **/
    public void debugListLehrer() {
        for( int i=0; i<lehrer.size(); i++)
            Logger.message( this, "Lehrer: " + lehrer.get(i));
    }

    /**
     * Gibt eine Debug-Liste auf der Logger-Ausgabe 
     * aus (eine Liste der Gegenst�nde). Diese Funktion 
     * wird w�hrend der Entwicklung und f�r Debugging-
     * Zwecke verwendet.
     **/
    public void debugListGegenstaende() {
        for( int i=0; i<gegenstaende.size(); i++)
            Logger.message( this, "Gegenstand: " + gegenstaende.get(i));
    }
    
    /**
     * Sucht nach der UID eines Gegenstands nach dem Namen (zB AM).
     * Dies ist in Wirklichkeit nur eine Durchreichefunktion 
     * f�r Gegenstand.findGegenstand(). F�r weitere Informationen 
     * siehe dort.
     *
     * @param name Name des Gegenstands, nach dem gesucht wird
     * @return siehe Gegenstand.findGegenstand()
     **/
    public int findGegenstand( String name) {
        return Gegenstand.findGegenstand( gegenstaende, Gegenstand.translateName( name));
    }
    
    /**
     * Sucht nach der UID eines Lehrers per K�rzel.
     * Dies ist in Wirklichkeit nur eine Durchreichefunktion 
     * f�r Lehrer.findLehrer(). F�r weitere Informationen 
     * siehe dort.
     *
     * @param name Der Name des Lehrers, nach dem gesucht wird
     * @return siehe Lehrer.findLehrer()
     **/
    public int findLehrer( String name) {
        return Lehrer.findLehrer( lehrer, name);
    }
    
    /**
     * Holt einen Gegenstand aus der Gegenst�nde-Tabelle. 
     * Als �bergabeparameter wird die UID des Gegenstands 
     * verwendet. Die UID eines Gegenstands kann zB mit 
     * der Funktion findGegenstand() herausgefunden werden.
     * 
     * @param uid Die UID des zu holenden Gegenstands
     * @return Gegenstand-Objekt mit der passenden UID (oder null, wenn nicht gefunden)
     **/
    public Gegenstand getGegenstandByUid( int uid) {
        Gegenstand result = null;
        
        for( int i=0; i<gegenstaende.size(); i++)
        {
            Gegenstand g = gegenstaende.get(i);
            if( g.getUid() == uid)
                result = g;
        }
        
        return result;
    }
    
    /**
     * Schreibt eine Logger-Warnung aus, die besagt, dass 
     * eine line (beim Einlesen) nicht ber�cksichtigt 
     * werden konnte und somit keine Daten angelegt wurden.
     *
     * @param line Ein String-Array, wie es von der Einlesen-Routine bekommen wird
     **/
    private void warnByLine( String [] line)
    {
        Logger.warning( this, "Die Relation K:" + line[KLASSE] + ", G:" + line[GEGENSTAND] +
                              ", L:" + line[LEHRER] + " wird nicht ber�cksichtigt!");
    }
    
    /**
     * Schreibt alle Daten, die dieser Importer 
     * beinhaltet und ausgelesen hat in die 
     * Datenbank.
     *
     * @return true, wenn alles geklappt hat, ansonsten false
     **/
    public boolean allToDatabase() {
        DatabaseTool db = DatabaseTool.getInstance();
        
        Logger.progress( this, "Schreibe alle Gegenst�nde in die Datenbank.");
        
        // Gegenstands-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.GEGENSTAND));
        
        // Gegenst�nde in die Datenbank schreiben
        for( int i=0; i<gegenstaende.size(); i++)
        {
            // Gegenstand aus der Tabelle holen
            Gegenstand g = gegenstaende.get(i);
            
            // Gegenstand in Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)g) == false)
            {
                Logger.warning( this, "Fehler bei Gegenstand: " + g);
                return false;
            }
        }
        
        Logger.progress( this, "Schreibe alle Lehrer in die Datenbank.");
        
        // Lehrer-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.LEHRER));
        
        // Lehrer in die Datenbank schreiben
        for( int i=0; i<lehrer.size(); i++)
        {
            // Lehrer aus der Tabelle holen
            Lehrer l = lehrer.get(i);
            
            // Lehrer in die Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)l) == false)
            {
                Logger.warning( this, "Fehler bei Lehrer: " + l);
                return false;
            }
        }
        
        Logger.progress( this, "Schreibe alle Gegenstand-Lehrer-Klasse Relationen in die Datenbank.");
        
        // G-L-K-Relationen-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.GEGENSTAND_LEHRER_KLASSE));
        
        // G-L-K Relationen in die Datenbank schreiben
        for( int i=0; i<relationen.size(); i++)
        {
            // Relation aus der Tabelle holen
            RelationGegenstandLehrerKlasse r = relationen.get(i);
            
            if( db.insertObject( r) == false)
            {
                Logger.warning( this, "Fehler bei Gegenstand-Lehrer-Klasse-Relation: " + r);
                return false;
            }
        }
        
        Logger.progress( this, "Alle Gegenst�nde, Lehrer und G-L-K Relationen erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Liefert eine Beschreibung (im Interface Databaseable)
     * f�r diese Klasse.
     *
     * @return Beschreibung der exportieren Daten (siehe Interface Databaseable)
     **/
    public String getDescription() {
        return "Gegenst�nde, Lehrer und G-L-K Relationen";
    }

}
