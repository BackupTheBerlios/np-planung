/*
 * Schueler.java
 *
 * Created on 27. Mai 2005, 11:46
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


package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.util.*;

/**
 * Die Klasse Schueler beschreibt einen Schüler im
 * Datenmodell des Nachprüfungsplanungsprogramms.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Schueler implements SQLizable {
    private int uid = 0; // UID des Schülers
    private int katalognr = 0; // Katalognummer
    private String name = ""; // (Vollständiger) Name des Schülers
    private int klasse = 0; // Klassen-Nummer
    private int anzahl5 = 0; // Anzahl der 5er bzw der Nicht Beurteilt
    
    public static final int MAX_5ER = 2; // Maximale Anzahl der 5er oder Nicht Beurteilt

    /**
     * Erstellt ein neues Schüler-Objekt.
     *
     * @param katalognr Katalognummer des Schülers
     * @param name (Vollständiger) Name des Schülers)
     * @param klasse UID der Klasse, in die der Schüler geht
     **/
    public Schueler( int katalognr, String name, int klasse) {
        this.katalognr = katalognr;
        this.name = name;
        this.klasse = klasse;
    }

    /**
     * Erstellt ein neues Schüler-Objekt. Es können 
     * alle Daten des Schülers angegeben werden.
     *
     * @param uid UID des Schülers
     * @param katalognr Katalognummer des Schülers
     * @param name (Vollständiger) Name des Schülers)
     * @param klasse UID der Klasse, in die der Schüler geht
     **/
    public Schueler( int uid, int katalognr, String name, int klasse) {
        this.uid = uid;
        this.katalognr = katalognr;
        this.name = name;
        this.klasse = klasse;
    }

    /**
     * Vergleicht den Schueler mit einem anderen auf Gleichheit.
     *
     * @return true, wenn der Schueler der selbe ist, false anderenfalls
     **/
    public boolean equals( Schueler s) {
        return name.equals( s.name) && 
               katalognr == s.katalognr && 
               klasse == s.klasse;
    }

    /**
     * Überprüft, ob der Schueler gültig ist, um zur 
     * Datenbank hinzugefügt zu werden.
     *
     * @return true, wenn der Schueler gültig ist, false anderenfalls
     **/
    public boolean isValid() {
        return !name.equals( "") && klasse != -1 && darfNachpruefung();
    }

    /**
     * Prüft, ob der genannte Schueler schon in einem 
     * Schuelervektor vorhanden ist, oder nicht.
     *
     * @param v Der Schuelervektor, in dem zu suchen ist
     * @param schueler Der Schueler, nach dem zu suchen ist
     * @return true, wenn der Schueler vorhanden ist, ansonsten false
     */
    public static boolean schuelerExists( Vector<Schueler> v, Schueler schueler)
    {
        for( int i=0; i<v.size(); i++)
        {
            if( v.get( i).equals( schueler))
                return true;
        }
        return false;
    }
    
    /**
     * Holt einen Schüler aus einem Schülervektor, indem 
     * man nach einem Muster sucht (das Muster ist ein 
     * Schueler-Objekt mit gesetztem Namen, Katalog-Nr 
     * und Klasse). Es wird ein Schueler-Objekt zurückgegeben, 
     * welches im Schülervektor vorhanden ist. Dieses 
     * Objekt hat normalerweise bereits den UID-Wert gesetzt.
     *
     * @param v Ein Schueler-Vektor, in dem zu suchen ist
     * @param schueler Ein Muster-Schueler-Objekt, welches die Kriterien für die Suche enthält
     * @return Ein Schueler-Objekt aus dem Vektor, oder null wenn nicht gefunden
     **/
    public static Schueler getSchueler( Vector<Schueler> v, Schueler schueler)
    {
        for( int i=0; i<v.size(); i++)
        {
            Schueler f = v.get(i);
            
            if( f.equals( schueler))
                return f;
        }
        return null;
    }
    
    /**
     * Gibt die stringmäßige Repräsentation des Schülers 
     * zurück. Diese hat die die Form "UID KatalogNr Vorname Nachname (Klasse, Anzahl 5er)".
     * Kann für Debugging-Zwecke und für Fehlerausgabe, etc..
     * verwendet werden.
     *
     * @return String in der Form "UID KatalogNr Vorname Nachname (Klasse, Anzahl 5er)", der den Schüler beschreibt
     **/
    public String toString() {
        return uid + " " + katalognr + " " + name + " (" + klasse + ", " + anzahl5 + " 5er)";
    }
    
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getKatalognr() {
        return katalognr;
    }

    public void setKatalognr(int katalognr) {
        this.katalognr = katalognr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public int getAnzahl5() {
        return anzahl5;
    }

    public void setAnzahl5(int anzahl5) {
        this.anzahl5 = anzahl5;
    }
    
    /**
     * Erhöht die Anzahl der 5er bzw Nicht Beurteilt eines 
     * Schülers. Dies wird benötigt, um feststellen zu 
     * können, ob ein Schüler überhaupt noch für eine 
     * Nachprüfung zugelassen wird, oder gar nicht mehr 
     * wiederholen darf.
     **/
    public void erhoeheAnzahl5er() {
        anzahl5++;
    }
    
    /**
     * Prüft, ob der Schüler eine Nachprüfung machen darf.
     * Es gibt Situationen, wo ein Schüler mehr keine 
     * Nachprüfungen machen darf. Das ist der Fall, wenn 
     * der Schüler mehr als 2 5er oder Nicht Beurteilt 
     * hat.
     *
     * @return True, wenn die Anzahl der 5er bzw Nicht Beurteilt kleiner ist als MAX_5ER, sonst False
     **/
    public boolean darfNachpruefung() {
        return anzahl5 <= MAX_5ER;
    }

    /**
     * Wandelt dieses Schueler-Objekt in eine 
     * SQL "INSERT INTO" Anweisung um. Damit 
     * kann dieser Schüler in die Datenbank
     * geschrieben werden.
     *
     * @return String, der einen SQL "INSERT INTO" Anweisung beinhaltet
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.T_SCHUELER;
        
        return "INSERT INTO " + table + " (Schuelernummer, Katalognummer, Name, Klassennummer) " +
                    "VALUES (" + uid + ", " + katalognr + ", '" + name + "', " + klasse + ")";
    }
    
}
