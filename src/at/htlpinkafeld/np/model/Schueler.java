/*
 * Schueler.java
 *
 * Created on 27. Mai 2005, 11:46
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


package at.htlpinkafeld.np.model;

import java.util.*;

import at.htlpinkafeld.np.util.*;

/**
 * Die Klasse Schueler beschreibt einen Sch�ler im
 * Datenmodell des Nachpr�fungsplanungsprogramms.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Schueler implements SQLizable {
    private int uid = 0; // UID des Sch�lers
    private int katalognr = 0; // Katalognummer
    private String name = ""; // (Vollst�ndiger) Name des Sch�lers
    private int klasse = 0; // Klassen-Nummer
    private int anzahl5 = 0; // Anzahl der 5er bzw der Nicht Beurteilt
    
    public static final int MAX_5ER = 2; // Maximale Anzahl der 5er oder Nicht Beurteilt

    /**
     * Erstellt ein neues Sch�ler-Objekt.
     *
     * @param katalognr Katalognummer des Sch�lers
     * @param name (Vollst�ndiger) Name des Sch�lers)
     * @param klasse UID der Klasse, in die der Sch�ler geht
     **/
    public Schueler( int katalognr, String name, int klasse) {
        this.katalognr = katalognr;
        this.name = name;
        this.klasse = klasse;
    }

    /**
     * Erstellt ein neues Sch�ler-Objekt. Es k�nnen 
     * alle Daten des Sch�lers angegeben werden.
     *
     * @param uid UID des Sch�lers
     * @param katalognr Katalognummer des Sch�lers
     * @param name (Vollst�ndiger) Name des Sch�lers)
     * @param klasse UID der Klasse, in die der Sch�ler geht
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
     * �berpr�ft, ob der Schueler g�ltig ist, um zur 
     * Datenbank hinzugef�gt zu werden.
     *
     * @return true, wenn der Schueler g�ltig ist, false anderenfalls
     **/
    public boolean isValid() {
        return !name.equals( "") && klasse != -1 && darfNachpruefung();
    }

    /**
     * Pr�ft, ob der genannte Schueler schon in einem 
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
     * Holt einen Sch�ler aus einem Sch�lervektor, indem 
     * man nach einem Muster sucht (das Muster ist ein 
     * Schueler-Objekt mit gesetztem Namen, Katalog-Nr 
     * und Klasse). Es wird ein Schueler-Objekt zur�ckgegeben, 
     * welches im Sch�lervektor vorhanden ist. Dieses 
     * Objekt hat normalerweise bereits den UID-Wert gesetzt.
     *
     * @param v Ein Schueler-Vektor, in dem zu suchen ist
     * @param schueler Ein Muster-Schueler-Objekt, welches die Kriterien f�r die Suche enth�lt
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
     * Gibt die stringm��ige Repr�sentation des Sch�lers 
     * zur�ck. Diese hat die die Form "UID KatalogNr Vorname Nachname (Klasse, Anzahl 5er)".
     * Kann f�r Debugging-Zwecke und f�r Fehlerausgabe, etc..
     * verwendet werden.
     *
     * @return String in der Form "UID KatalogNr Vorname Nachname (Klasse, Anzahl 5er)", der den Sch�ler beschreibt
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
     * Erh�ht die Anzahl der 5er bzw Nicht Beurteilt eines 
     * Sch�lers. Dies wird ben�tigt, um feststellen zu 
     * k�nnen, ob ein Sch�ler �berhaupt noch f�r eine 
     * Nachpr�fung zugelassen wird, oder gar nicht mehr 
     * wiederholen darf.
     **/
    public void erhoeheAnzahl5er() {
        anzahl5++;
    }
    
    /**
     * Pr�ft, ob der Sch�ler eine Nachpr�fung machen darf.
     * Es gibt Situationen, wo ein Sch�ler mehr keine 
     * Nachpr�fungen machen darf. Das ist der Fall, wenn 
     * der Sch�ler mehr als 2 5er oder Nicht Beurteilt 
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
     * kann dieser Sch�ler in die Datenbank
     * geschrieben werden.
     *
     * @return String, der einen SQL "INSERT INTO" Anweisung beinhaltet
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.SCHUELER);
        
        return "INSERT INTO " + table + " (Schuelernummer, Katalognummer, Name, Klassennummer) " +
                    "VALUES (" + uid + ", " + katalognr + ", '" + name + "', " + klasse + ")";
    }
    
}
