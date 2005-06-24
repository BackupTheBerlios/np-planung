/*
 * SchuelerImporter.java
 *
 * Created on 10. M�rz 2005, 19:26
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
import at.htlpinkafeld.np.frontend.*;

/**
 * Die Klasse SchuelerImporter liest Daten aus 
 * der SchuelerMitNoten.csv Datei und erzeugt 
 * einen Vektor von Schueler-Objekten.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class SchuelerImporter implements Databaseable {
    private String filename = null;
    private KlasseImporter ki = null;
    private RelationGegenstandLehrerImporter lgi = null;
    private ExcelCSVParser parser = null;
    
    private Vector<Schueler> schueler = null;
    private Vector<RelationSchuelerGegenstand> relationen = null;
    private Vector<RelationSchuelerKlasseGegenstandMoeglichkeiten> rel_sgkm = null;

    private static final int KLASSE = 0;
    private static final int KATALOG = 1;
    private static final int VORNAME = 2;
    private static final int NACHNAME = 3;
    private static final int NOTE = 4;
    private static final int GEGENSTAND = 5;
    private static final int GEGENSTAND_TXT = 6;
    
    /**
     * Erstellt einen neuen SchuelerImporter, mit dem 
     * Daten aus der SchuelermitNoten Datei importiert werden.
     *
     * @param filename Der Dateiname zur SchuelermitNoten Datei
     * @param ki Ein KlasseImporter, der die Klassendaten zur Verf�gung stellt
     * @param lgi Ein LehrerGegenstandImporter, der die Lehrer- und Gegenstanddaten zur Verf�gung stellt
     */
    public SchuelerImporter( String filename, KlasseImporter ki, RelationGegenstandLehrerImporter lgi) throws FileNotFoundException, IOException {
        this.filename = filename;
        this.ki = ki;
        this.lgi = lgi;
        
        parser = new ExcelCSVParser( new BufferedReader( new FileReader( filename)));
        
        // Fix: Die SchuelermitNoten benutzt Strichpunkte statt Komma
        parser.changeDelimiter( ';');
        
        schueler = new Vector<Schueler>();
        relationen = new Vector<RelationSchuelerGegenstand>();
        rel_sgkm = new Vector<RelationSchuelerKlasseGegenstandMoeglichkeiten>();
    }
    
    /**
     * Liest alle Zeilen aus der SchuelermitNoten
     * Datei und speichert die relevanten Felder 
     * in eine interne Struktur zur Weiterverarbeitung.
     *
     * Diese Funktion sollte nur einmal nach dem 
     * �ffnen der Datei aufgerufen werden!
     */
    public void readSchueler() throws IOException
    {
        String [] line = null;
        int uid = 1; // Zu vergebende UID
        
        // Gegenstand-Lehrer-Klasse Relationen
        Vector<RelationGegenstandLehrerKlasse> glk_rel = lgi.getRelationen();
        
        Logger.progress( this, "Lese Sch�ler und Noten aus " + filename);
        
        while( (line = parser.getLine()) != null)
        {
            if( line[NOTE].equals( "5") || line[NOTE].equals( "NB"))
            {
                int katalognr = 0;
                String name = line[VORNAME]+ " " + line[NACHNAME];
                
                try
                {
                    katalognr = Integer.parseInt( line[KATALOG]);
                }
                catch( Exception e)
                {
                    Logger.warning( this, "Fehler beim Umwandeln von Katalog-Nummer: " + line[KATALOG]);
                    Logger.warning( this, "Ursache f�r obigen Fehler: " + e.toString());
                    
                    // Wir setzen voraus, dass eine falsche Katalog-Nummer eine falsche Zeile bedeutet
                    continue;
                }
                
                int klasse_uid = ki.findKlasse( line[KLASSE]);
                
                Schueler s = new Schueler( katalognr, name, klasse_uid);
                
                if( s.isValid())
                {
                    if( Schueler.schuelerExists( schueler, s))
                    {
                        /**
                         * "Echtes" Objekt aus dem Vektor holen, wo die 
                         * UID bereits einen Wert besitzt. Ist wichtig 
                         * f�r sp�ter, wenn wir die UID weiterverwenden.
                         **/
                        s = Schueler.getSchueler( schueler, s);
                    }
                    else
                    {
                        /**
                         * Diesen Schueler als neuen Schueler in der 
                         * Datenbank ablegen, und die n�chste UID 
                         * zuweisen.
                         **/
                        s.setUid( uid++);
                        schueler.add( s);                        
                    }
                    
                    // Einen 5er bzw Nicht Beurteilt mehr, also erh�he den Counter
                    s.erhoeheAnzahl5er();
                    
                    // Gegenstand finden (an sich.. muss noch nicht der endg�ltige sein)
                    Gegenstand g = lgi.getGegenstandByUid( lgi.findGegenstand( line[GEGENSTAND]));
                    
                    // Klasse-Objekt finden
                    Klasse k = ki.getKlasseByUid( klasse_uid);

                    Vector<RelationGegenstandLehrerKlasse> moeglichkeiten = RelationGegenstandLehrerKlasse.getRelationenByGegenstandKlasse( glk_rel, g, k);
                    
                    if( moeglichkeiten.size() == 1)
                    {
                        // Wir haben nur einen Gegenstand, keine Gruppenteilung.. fertig! :)
                    }
                    else
                    {
                        // Wir m�ssen einen Gegenstand ausw�hlen lassen, das merken wir uns vorerst mal vor
                        rel_sgkm.add( new RelationSchuelerKlasseGegenstandMoeglichkeiten( s, k, g, moeglichkeiten));
                    }
                    
                    if( g != null)
                    {
                        RelationSchuelerGegenstand rel = new RelationSchuelerGegenstand( s, g);
                        
                        // Wenn die Relation noch nicht existiert, dann einf�gen
                        if( !RelationSchuelerGegenstand.relationExists( relationen, s, g))
                        {
                            relationen.add( rel);
                            Logger.debug( this, "Erstelle Relation: Klasse = " + ki.getKlasseByUid( s.getKlasse()).toString() + ", Sch�ler = " + s.toString() + ", Gegenstand = " + g.toString());
                        }
                    }
                    else
                    {
                        // Gib' Hinweis-Text aus => siehe Text!
                        Logger.warning( this, "Fehler: Konnte keinen Gegenstand finden f�r \"" + line[GEGENSTAND] + "\" bei Sch�ler: " + s.toString());
                        Logger.message( this, "Hinweis: Vielleicht fehlt nur eine Eintragung in der Gegenstand-Liste? Siehe Funktion Gegenstand.translateName().");
                    }
                }
            }
        }
        
        Logger.progress( this, "Entferne Sch�ler, die keine Nachpr�fungen machen d�rfen... (" + schueler.size() + " verbleibende Sch�ler)");
        
        for( int i=schueler.size()-1; i>=0; i--)
        {
            Schueler s = schueler.get(i);
            
            if( !s.darfNachpruefung())
            {
                Logger.debug( this, "Sch�ler darf keine Nachpr�fung machen: " + s.toString() + ", wird aus der Liste geworfen.");
                schueler.remove( s);
            }
        }
        
        Logger.progress( this, "Erzeuge neue UIDs f�r verbleibende Sch�ler (" + schueler.size() + " verbleibende Sch�ler)");
        
        // Die UIDs auf neue Werte setzen, n�here Beschreibung bei reIndex()..
        reIndex();
        
        // �berfl�ssige Relationen entfernen (die Relationen mit entfernten Sch�lern)
        RelationSchuelerGegenstand.cleanupRelationen( relationen);
        RelationSchuelerKlasseGegenstandMoeglichkeiten.cleanupRelationen( rel_sgkm);
        
        Logger.progress( this, "Alle Sch�ler wurden erfolgreich importiert und aussortiert.");
        
        Logger.progress( this, "Beginne mit der Gruppenteilung-Abfrage f�r Sch�ler.");
        
        for( int i=0; i<rel_sgkm.size(); i++)
        {
            RelationSchuelerKlasseGegenstandMoeglichkeiten sgkm = rel_sgkm.get(i);
            
            GruppenteilungLehrerChooser chooser = new GruppenteilungLehrerChooser( MainDialog.getInstance(), sgkm);
            if( chooser.isCancelled())
            {
                int skipped = rel_sgkm.size()-i;
                Logger.warning( this, "User hat Gruppenteilung-Abfrage abgebrochen.");
                Logger.warning( this, "Anzahl der nicht bearbeiteten Gruppenteilung-Abfragen: " + skipped);
                break;
            }
            else
            {
                // Relation so �ndern, dass richtiger Gegenstand in der Relation steht
                Gegenstand neu_g = chooser.getSeletedGegenstand();
                if( neu_g != null)
                {
                    RelationSchuelerGegenstand.updateRelation( relationen, sgkm.getSchueler(), neu_g);
                }
                else
                {
                    Logger.warning( this, "Gegenstand war null - keine �nderung der Relation f�r Sch�ler" + sgkm.getSchueler().getName());
                }
            }
        }
        
        Logger.progress( this, "Gruppenteilung-Abfrage f�r Sch�ler wurde beendet.");
    }
    
    /**
     * Setzt neue Index-Werte f�r die Sch�ler-Liste. Dies ist 
     * notwendig, damit die Liste nach dem L�schen der 
     * sinnlosen Sch�ler (darfNachpruefung() == false) wieder 
     * einheitliche Indexes hat. Die RelationSchulerGegenstand 
     * wird hierbei automatisch mitge�ndert, da nicht die UID 
     * des Sch�lers gespeichert wird, sondern das Sch�ler-Objekt 
     * selbst.
     **/
    private void reIndex() {
        for( int i=0; i<schueler.size(); i++)
            schueler.get(i).setUid( i+1);
    }
    
    /**
     * Liefert die Sch�lertabelle zur�ck, die zuvor mit 
     * readSchueler() eingelesen werden muss.
     *
     * @return Vektor, der die Sch�lertabelle beinhaltet
     */
    public Vector getSchueler()
    {
        return schueler;
    }
    
    /**
     * Gibt die Liste der Sch�ler auf System.out aus.
     * Diese Funktion ist f�r Debugging-Zwecke n�tzlich.
     **/
    public void printDebug() {
        for( int i=0; i<schueler.size(); i++)
            System.out.println( schueler.get( i));
    }

    /**
     * Schreibt alle Daten, die von diesem Importer 
     * erstellt worden sind, und f�r die Nachpr�fungsplanung 
     * wichtig sind in die Datenbank.
     *
     * @return true, wenn erfolgreich, ansonsten false
     **/
    public boolean allToDatabase() {
        DatabaseTool db = DatabaseTool.getInstance();
        
        Logger.progress( this, "Schreibe alle Sch�ler in die Datenbank.");
        
        // Schueler-Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.SCHUELER));
        
        for( int i=0; i<schueler.size(); i++)
        {
            // Schueler-Objekt aus der Tabelle holen
            Schueler s = schueler.get(i);
            
            // Schueler in die Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)s) == false)
            {
                Logger.warning( this, "Fehler bei Sch�ler: " + s);
                return false;
            }
        }
        
        Logger.progress( this, "Schreibe alle Sch�ler-Gegenstand-Relationen in die Datenbank.");
        
        // Sch�ler-Gegenstand-Relationen Tabelle leeren
        db.emptyTable( DatabaseMetadata.getTableName( DatabaseMetadata.SCHUELER_GEGENSTAND));
        
        for( int i=0; i<relationen.size(); i++)
        {
            // Relation aus der Tabelle holen
            RelationSchuelerGegenstand r = relationen.get(i);
            
            // Relation in die Datenbank schreiben (siehe DatabaseTool)
            if( db.insertObject( (SQLizable)r) == false)
            {
                Logger.warning( this, "Fehler bei Relation: " + r);
                return false;
            }
        }
        
        Logger.progress( this, "Alle Sch�ler und Sch�ler-Gegenstand Relationen erfolgreich in die Datenbank geschrieben.");
        
        return true;
    }

    /**
     * Gibt eine Beschreibung der Daten zur�ck, 
     * die von diesem Databaseable Objekt in die 
     * Datenbank geschrieben werden. Wird zB von 
     * DBWriter verwendet.
     *
     * @return String, der die Daten f�r die Datenbank beschreibt
     **/
    public String getDescription() {
        return "Sch�lerdaten und Sch�ler-Gegenstand Relationen";
    }
}
