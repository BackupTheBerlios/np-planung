/*
 * Gegenstand.java
 *
 * Created on 26. Mai 2005, 16:38
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
import at.htlpinkafeld.np.devel.*;

/**
 * Die Klasse Gegenstand beinhaltet alle Informationen, die zu einem 
 * Gegenstand f�r die Nachpr�fungsplanung wichtig sind und einige 
 * Funktionen, um Gegenstand-Objekte zu bearbeiten.
 *
 * @author Thomas Perl <thp@perli.net>
 * @author edited by Marc Schermann
 */
public class Gegenstand implements SQLizable {
    private int uid = 0; // UID des Gegenstands
    private String name = ""; // Die Bezeichnung des Gegenstands
    private boolean schriftlich = false; // ist dieser Gegenstand schriftlich zu pr�fen?
    private boolean muendlich = true; // ist dieser Gegenstand m�ndlich zu pr�fen?
    private int gruppe = 1; // Gruppe bei Gruppenteilung (verschiedene Lehrer erkennen)
    
    /**
     * Erstellt einen neuen Gegenstand.
     *
     * @param name Der Name des Gegenstands
     **/
    public Gegenstand( String name) {
        this.name = name;
        
        ConfigManager cm = ConfigManager.getInstance();
        
        // Schriftlich f�r diesen Gegenstand aus der Konfiguration auslesen
        schriftlich = Boolean.parseBoolean( cm.getProperty( this, "schriftlich."+name, Boolean.toString( schriftlich)));
        
        // M�ndlich f�r diesen Gegenstand aus der Konfiguration auslesen
        muendlich = Boolean.parseBoolean( cm.getProperty( this, "muendlich."+name, Boolean.toString( muendlich)));
    }
    
    /**
     * Erstellt einen neuen Gegenstand mit 
     * einer Gruppen-ID f�r geteilte Gegenst�nde.
     *
     * @param name Der Name des Gegenstands
     * @param gruppe Die Gruppe bei Gruppenteilung
     **/
    public Gegenstand( String name, int gruppe) {
        this.name = name;
        this.gruppe = gruppe;
    }
    
    /**
     * Erstellt einen neuen Gegenstand. Es k�nnen 
     * alle Daten des Gegenstands angegeben werden.
     *
     * @param uid Die UID des Gegenstands
     * @param name Der Name des Gegenstands
     * @param schriftlich true, wenn der Gegenstand schriftlich zu pr�fen ist
     * @param muendlich true, wenn der Gegenstand m�ndlich zu pr�fen ist
     * @param gruppe Die Gruppe bei Gruppenteilung, ansonsten 1
     **/
    public Gegenstand( int uid, String name, boolean schriftlich, boolean muendlich, int gruppe)
    {
        this.uid = uid;
        this.name = name;
        this.schriftlich = schriftlich;
        this.muendlich = muendlich;
        this.gruppe = gruppe;
    }
    
    /**
     * Vergleicht den Gegenstand mit einem anderen und liefert 
     * ein Ergebnis gleich der Funktion String.compareTo() 
     * zur�ck. Diese Funktion kann von Sortieralgorithmen 
     * verwendet werden (und wird von sort() benutzt).
     * Es werden auch die Gruppen ber�cksichtigt, wenn der 
     * Name des Gegenstands gleich ist.
     *
     * @param g Der Gegenstand, der verglichen werden soll
     * @return int-Wert wie bei String.compareTo()
     **/
    public int compareTo( Gegenstand g) {
        int name_result = name.compareTo( g.name);
        
        if( name_result != 0)
            return name_result;
        else
            return gruppe - g.gruppe;
    }
    
    /**
     * Vergleicht den Gegenstand mit einem anderen auf Gleichheit.
     *
     * @return true, wenn der Gegenstand der selbe ist, false anderenfalls
     **/
    public boolean equals( Gegenstand g) {
        return name.equals( g.getName()) && getGruppe() == g.getGruppe();
    }
    
    /**
     * Vergleicht den Gegenstand mit einem anderen auf Gleichheit, 
     * wobei die Gruppen-Nummer unber�cksichtigt bleibt.
     *
     * @return true, wenn der Gegenstand der selbe ist (ohne Gruppe), false anderenfalls
     **/
    public boolean equalsIgnoreGruppe( Gegenstand g) {
        return name.equals( g.getName());
    }
    
    /**
     * �berpr�ft, ob der Gegenstand g�ltig ist, um zur 
     * Datenbank hinzugef�gt zu werden.
     *
     * @return true, wenn der Gegenstand g�ltig ist, false anderenfalls
     **/
    public boolean isValid() {
        return !name.equals( "");
    }
    
    public String getName() {
        return name;
    }

    public void setName( String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isSchriftlich() {
        return schriftlich;
    }

    public void setSchriftlich(boolean schriftlich) {
        this.schriftlich = schriftlich;
    }

    public boolean isMuendlich() {
        return muendlich;
    }

    public void setMuendlich(boolean muendlich) {
        this.muendlich = muendlich;
    }
    
    /**
     * Sortiert einen Vektor von Gegenst�nden alphabetisch nach Namen.
     *
     * @param v Der Vektor, der zu sortieren ist
     * @param doIndex Wenn true, werden die UIDs nach dem Sortiervorgang neu gesetzt
     **/
    public static void sort( Vector<Gegenstand> v, boolean doIndex) {
        boolean isSorted = false;
        
        while( !isSorted)
        {
            isSorted = true;
            for( int i=0; i<v.size()-1; i++)
            {
                Gegenstand a = v.get(i);
                Gegenstand b = v.get(i+1);
                if( a.compareTo( b) > 0)
                {
                    v.setElementAt( b, i);
                    v.setElementAt( a, i+1);
                    isSorted = false;
                }
            }
        }
        
        if( doIndex)
        {
            int uid = 1;
            
            for( int i=0; i<v.size(); i++)
                v.get(i).setUid( uid++);
        }
    }
    
    /**
     * Pr�ft, ob der genannte Gegenstand schon im
     * internen Vektor vorhanden ist, oder nicht.
     *
     * @param gegenstaende Der Vektor, in dem zu suchen ist
     * @param gegenstand Der zu suchende Gegenstand
     * @return true, wenn der Gegenstand vorhanden ist, ansonsten false
     */
    public static boolean gegenstandExists( Vector<Gegenstand> gegenstaende, Gegenstand gegenstand)
    {
        for( int i=0; i<gegenstaende.size(); i++)
        {
            if( gegenstaende.get( i).equals( gegenstand))
                return true;
        }
        return false;
    }
    
    /**
     * Sucht in einem Vektor von Gegenst�nden nach 
     * einem Gegenstand. Liefert die UID des
     * Gegenstands zur�ck, der gefunden wurde.
     *
     * @param gegenstaende Vektor, in dem der Gegenstand zu suchen ist
     * @param name Name des Gegenstands, die zu finden ist
     * @return UID des Gegenstands, wenn gefunden; wenn nicht gefunden -1
     **/
    public static int findGegenstand( Vector<Gegenstand> gegenstaende, String name) {
        for( int i=0; i<gegenstaende.size(); i++)
        {
            Gegenstand g = gegenstaende.get(i);
            if( name.equals( g.getName()))
                return g.getUid();
        }
        
        return -1;
    }

    /**
     * Sucht in einem Vektor von Gegenst�nden nach 
     * einem Gegenstand. Liefert die UID des
     * Gegenstands zur�ck, der gefunden wurde. Diese 
     * Funktion ber�cksichtigt auch eine Gruppen-ID 
     * bei einer Gruppenteilung bei verschiedenen 
     * Gegenst�nden.
     *
     * @param gegenstaende Vektor, in dem der Gegenstand zu suchen ist
     * @param name Name des Gegenstands, die zu finden ist
     * @param gruppe Die Gruppe des Gegenstands bei Gruppenteilung
     * @return UID des Gegenstands, wenn gefunden; wenn nicht gefunden -1
     **/
    public static int findGegenstand( Vector<Gegenstand> gegenstaende, String name, int gruppe) {
        for( int i=0; i<gegenstaende.size(); i++)
        {
            Gegenstand g = gegenstaende.get(i);
            if( name.equals( g.getName()) && gruppe == g.getGruppe())
                return g.getUid();
        }
        
        return -1;
    }

    /**
     * �bersetzt einen Gegenstandsnamen in den "richtigen" 
     * Namen des Gegenstands. Diese Funktion wird ben�tigt, 
     * weil manche Gegenst�nde in verschiedenen Klassen oder 
     * Abteilungen unterschiedliche Namen besitzen. Mit 
     * dieser Funktion werden diese unterschiedliche Namen 
     * auf einen einheitlichen gebracht. F�r genauere 
     * Informationen, welche Gegenst�nde das sind bitte im 
     * Sourcecode nachschlagen.
     *
     * Jedes Vorkommen eines Gegenstandnamens sollte durch 
     * diese �bersetzungsfunktion geschickt werden, um 
     * eine Einheitlichkeit von Gegenstandsnamen zu 
     * gew�hrleisten (also nicht nur in der Klasse Gegenstand 
     * sondern zB auch in der Klasse Schueler, etc..).
     *
     * @param name Der Name eines Gegenstands
     * @return Der "richtige" Name des Gegenstands (wenn vorhanden), ansonsten einfach der selbe Name
     **/
    public static String translateName( String name)
    {
        // Folgende �bersetzungen seit Schuljahr 2003/04 oder fr�her (�bernahme von RedSwitch)
        if( name.equals( "RWC"))
            return "RWUC";
        
        if( name.equals( "PR"))
            return "PRUB";
        
        if( name.equals( "GPH"))
            return "GSK";
       
        /**
         * Wird anscheinend nicht mehr ben�tigt - ist falsch?!
         *
        if( name.equals( "KONP"))
            return "AM";
         *
         **/
        
        if( name.equals( "NEPT"))
            return "D";
        
        if( name.equals( "BET"))
            return "D";
        
        // Folgende �bersetzungen seit Schuljahr 2004/05 (thp, <thp@perli.net>)
        if( name.equals( "GWKB"))
            return "GWK";
        
        if( name.equals( "RKB"))
            return "RK";
        
        return name;
    }
    
    /**
     * Gibt einen String zur�ck, der den Gegenstand 
     * repr�sentiert. Dieser String hat die Form 
     * "UID Name Gruppe m�ndlich und schriftlich", und kann f�r Debugging Zwecke 
     * und andere Ausgaben verwendet werden.
     *
     * @return String der Form "UID Name Gruppe m�ndlich und schriftlich", der den Gegenstand beschreibt
     **/
    public String toString() {
        String s = uid + " " + name + " " + gruppe;
        
        /* M�ndlich und schriftlich sch�n ausgeben */
        s += (muendlich)                ? " m�ndlich"    : "";
        s += (muendlich && schriftlich) ? " und"         : "";
        s += (schriftlich)              ? " schriftlich" : "";
        
        return s;
    }
    
    /**
     * Liefert die Dauer einer m�ndlichen Pr�fung f�r eine Klasse.
     *
     * @param k Die Klasse, f�r die die Dauer zu berechnen ist
     * @return Dauer einer m�ndlichen Pr�fung in Minuten
     **/
    public int getDauerMuendlich( Klasse k)
    {
        ConfigManager cm = ConfigManager.getInstance();
        
        // Standardm��ig wird von einer Viertelstunde ausgegangen
        int dauer = 15;
        
        // Jahrgang der Klasse
        String jahrgang = Integer.toString( k.getJahrgang());
        
        try
        {
            dauer = Integer.parseInt( cm.getProperty( this, "dauer.muendlich." + name + "-jahrgang-" + jahrgang, Integer.toString( dauer)));
        }
        catch( NumberFormatException nfe)
        {
            Logger.warning( this, "Konnte Daten nicht umwandeln in m�ndliche Dauer f�r Klasse " + k);
        }
        
        return dauer;
    }
    
    /**
     * Liefert die Dauer einer schriftlichen Pr�fung f�r eine Klasse.
     *
     * @param k Die Klasse, f�r die die Dauer zu berechnen ist
     * @return Dauer einer schriftlichen Pr�fung in Minuten
     **/
    public int getDauerSchriftlich( Klasse k)
    {
        ConfigManager cm = ConfigManager.getInstance();
        
        // Standardm��ig wird von einer Stunde (50 Minuten)
        int dauer = 50;
        
        // Jahrgang der Klasse
        String jahrgang = Integer.toString( k.getJahrgang());
        
        try
        {
            dauer = Integer.parseInt( cm.getProperty( this, "dauer.schriftlich." + name + "-jahrgang-" + jahrgang, Integer.toString( dauer)));
        }
        catch( NumberFormatException nfe)
        {
            Logger.warning( this, "Konnte Daten nicht umwandeln in schriftliche Dauer f�r Klasse " + k);
        }
        
        return dauer;
    }

    public int getGruppe() {
        return gruppe;
    }

    public void setGruppe(int gruppe) {
        this.gruppe = gruppe;
    }

    /**
     * Konvertiert dieses Gegenstand-Objekt in 
     * ein SQL "INSERT INTO" Statement.
     *
     * @return SQL "INSERT INTO" Statement f�r diesen Gegenstand
     **/
    public String toSqlInsert() {
        String table = DatabaseMetadata.getTableName( DatabaseMetadata.GEGENSTAND);
        
        /**
         * F�r MS Access scheint zu gelten, dass ein "true"
         * Wert bei einem "INSERT INTO" ein "-1" darstellt, 
         * und ein "false" Wert ein "0" darstellt.
         *
         * Bei der Verwendung einer moderneren Datenbank 
         * (MySQL, PostgreSQL) sollte dies dann je nach 
         * verwendeter Datenbank angepasst werden.
         **/
        int schriftl_int = schriftlich ? -1 : 0;
        int muendl_int = muendlich ? -1 : 0;
        
        return "INSERT INTO " + table + " (Gegenstandsnummer, Bezeichnung, schriftlich, m�ndlich) " +
                    "VALUES (" + uid + ", '" + name + "', " + schriftl_int + ", " + muendl_int + ")";
    }
}
