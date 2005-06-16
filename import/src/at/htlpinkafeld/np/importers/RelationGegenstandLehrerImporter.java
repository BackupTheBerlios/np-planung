/*
 * GegenstandImporter.java
 *
 * Created on 10. März 2005, 18:45
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
 * Importiert Lehrer und Gegenstände aus der GPU002-Datei.
 * Diese Daten werden dazu benötigt, um passende Relationen 
 * zwischen Gegenstand, Lehrer und Klasse zu erstellen.
 * Die Daten der Lehrer und Gegenstände werden zusätzlich 
 * noch vom LehrerGegenstandImporter aus der GPU008 Datei 
 * importiert, um sicher zu gehen, dass Lehrer und Gegenstände 
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
    
    private static final int KLASSE = 4;
    private static final int LEHRER = 5;
    private static final int GEGENSTAND = 6;
    
    /**
     * Erstellt einen neuen RelationGegenstandLehrerImporter,
     * mit dem Daten aus der GPU002 Datei importiert werden.
     *
     * @param filename Der Dateiname zur GPU002 Datei
     * @param ki Ein KlasseImporter, der die Klassen-Objekte zur Verfügung stellt
     * @param lgi Ein LehrerGegenstandImporter, der die Stammdaten zu Lehrer und Gegenstand bereithält
     */
    public RelationGegenstandLehrerImporter( String filename, KlasseImporter ki, LehrerGegenstandImporter lgi) throws FileNotFoundException, IOException {
        this.filename = filename;
        this.ki = ki;
        this.lgi = lgi;
        
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
     * Öffnen der Datei aufgerufen werden! Danach 
     * returnt die Funktion einfach, ohne etwas 
     * zu machen.
     */
    public void readLehrerGegenstaende() throws IOException
    {
        // temporäre uids..
        int gegenstand_uid = 1;
        int lehrer_uid = 1;
        // ..temporäre uids
        
        // Wenn wir schon gelesen haben, einfach returnen
        if( hasRead)
            return;
        
        Logger.progress( this, "Lese Lehrer und Gegenständen (+Relationen) aus " + filename);
        
        String [] line = null;
        
        while( (line = parser.getLine()) != null)
        {
            // Überprüfen, ob Lehrer Okay-ish ist..
            if( !lgi.lehrerExists( line[LEHRER]))
                continue;
            
            // Überprüfen, ob Gegenstand Okay-ish ist..
            if( !lgi.gegenstandExists( Gegenstand.translateName( line[GEGENSTAND])))
                continue;
            
            // Überprüfen, ob Klasse Okay-ish ist..
            if( !ki.klasseExists( line[KLASSE]))
                continue;
                
            // Klasse aus dem KlasseImporter heraussuchen und holen
            int klasse_uid = ki.findKlasse( line[KLASSE]);
            Klasse k = ki.getKlasseByUid( klasse_uid);

            // Prüfen, ob die Klasse gefunden wurde..
            if( klasse_uid == -1)
            {
                Logger.warning( this, "Konnte für " + line[KLASSE] + " kein Klasse-Objekt finden.");

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
             * Nun schauen wir, dass wir den Gegenstand anlegen können, 
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
             * Alles, was wir jetzt tun müssen, ist eine Relation 
             * zu erzeugen.
             **/
            if( !RelationGegenstandLehrerKlasse.relationExists( relationen, g, l, k))
            {
                Logger.debug( this, "Erstelle Relation (G-L-K): " + g + ", " + l + ", " + k + ".");
                RelationGegenstandLehrerKlasse relation_neu = new RelationGegenstandLehrerKlasse( g, l, k);
                relationen.add( relation_neu);
            }
        }
        
        // Gegenstände alphabetisch sortieren (und re-indexing):
        Gegenstand.sort( gegenstaende, true);
        
        // Lehrer alphabetisch sortieren (und re-indexing):
        Lehrer.sort( lehrer, true);
        
        hasRead = true;
        
        Logger.progress( this, "Import von Lehrer und Gegenständen (+Relationen) fertig.");
    }
    
    /**
     * Liefert die Gegenstandstabelle zurück, die zuvor mit 
     * readGegenstaende() eingelesen werden muss.
     *
     * @return Vektor, der die Gegenstandstabelle (als einen Vektor von Gegenständen) beinhaltet
     */
    public Vector<Gegenstand> getGegenstaende()
    {
        return gegenstaende;
    }
    
    /**
     * Liefert die Relationentabelle zurück, die zuvor mit 
     * eingelesen werden muss.
     *
     * @return Vektor, der alle Relationen enthält
     **/
    public Vector<RelationGegenstandLehrerKlasse> getRelationen()
    {
        return relationen;
    }
    
    /**
     * Liefert ein Lehrer-Objekt, welches man über 
     * die UID herausfinden kann. Solch ein Lehrer-
     * Objekt wird zum Beispiel dafür benötigt, um 
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
     * Sucht nach der UID eines Gegenstands nach dem Namen (zB AM).
     * Dies ist in Wirklichkeit nur eine Durchreichefunktion 
     * für Gegenstand.findGegenstand(). Für weitere Informationen 
     * siehe dort.
     *
     * @param name Name des Gegenstands, nach dem gesucht wird
     * @return siehe Gegenstand.findGegenstand()
     **/
    public int findGegenstand( String name) {
        return Gegenstand.findGegenstand( gegenstaende, Gegenstand.translateName( name));
    }
    
    /**
     * Sucht nach der UID eines Lehrers per Kürzel.
     * Dies ist in Wirklichkeit nur eine Durchreichefunktion 
     * für Lehrer.findLehrer(). Für weitere Informationen 
     * siehe dort.
     *
     * @param name Der Name des Lehrers, nach dem gesucht wird
     * @return siehe Lehrer.findLehrer()
     **/
    public int findLehrer( String name) {
        return Lehrer.findLehrer( lehrer, name);
    }
    
    /**
     * Holt einen Gegenstand aus der Gegenstände-Tabelle. 
     * Als Übergabeparameter wird die UID des Gegenstands 
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
     * eine line (beim Einlesen) nicht berücksichtigt 
     * werden konnte und somit keine Daten angelegt wurden.
     *
     * @param line Ein String-Array, wie es von der Einlesen-Routine bekommen wird
     **/
    private void warnByLine( String [] line)
    {
        Logger.warning( this, "Die Relation K:" + line[KLASSE] + ", G:" + line[GEGENSTAND] +
                              ", L:" + line[LEHRER] + " wird nicht berücksichtigt!");
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
        
        Logger.progress( this, "Schreibe alle Gegenstände in die Datenbank.");
        
        // Gegenstands-Tabelle leeren
        db.emptyTable( DatabaseMetadata.T_GEGENSTAND);
        
        // Gegenstände in die Datenbank schreiben
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
        db.emptyTable( DatabaseMetadata.T_LEHRER);
        
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
        db.emptyTable( DatabaseMetadata.T_GEGENSTAND_LEHRER_KLASSE);
        
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
        
        Logger.progress( this, "Alle Gegenstände, Lehrer und G-L-K Relationen erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Liefert eine Beschreibung (im Interface Databaseable)
     * für diese Klasse.
     *
     * @return Beschreibung der exportieren Daten (siehe Interface Databaseable)
     **/
    public String getDescription() {
        return "Gegenstände, Lehrer und G-L-K Relationen";
    }

}
