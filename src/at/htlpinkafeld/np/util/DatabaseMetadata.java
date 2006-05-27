/*
 * DatabaseMetadata.java
 *
 * Created on 16. Juni 2005, 07:54
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


package at.htlpinkafeld.np.util;

/**
 * Die Klasse DatabaseMetadata beinhaltet Funktionen, 
 * die Informationen zur Datenbank (wie zB Namen von 
 * Tabellen, etc.. bereitstellen. Damit ist bei einer 
 * �nderung der Datenbank (zB Umbenennung der Tabellen) 
 * nur diese Klasse zu �ndern.
 * 
 * @author Thomas Perl <thp@perli.net>
 */
public class DatabaseMetadata {
    private static DatabaseMetadata instance = null;
        
    // Tabelle f�r die Liste der Gegenst�nde
    public static final int GEGENSTAND = 1;
    
    // Tabelle f�r die Beziehungen Gegenstand-Lehrer-Klasse
    public static final int GEGENSTAND_LEHRER_KLASSE = 2;
    
    // Tabelle f�r die Liste der Klassen
    public static final int KLASSE = 3;
    
    // Tabelle f�r die Liste der Lehrer
    public static final int LEHRER = 4;
    
    // Tabelle f�r die Liste der R�ume
    public static final int RAUM = 5;
    
    // Tabelle f�r die Liste der Sch�ler
    public static final int SCHUELER = 6;
    
    // Tabelle f�r die Beziehungen Sch�ler-Gegenstand
    public static final int SCHUELER_GEGENSTAND = 7;
    
    // Tabelle f�r die Beziehungen Lehrer-Gegenstand
    public static final int LEHRER_GEGENSTAND = 8;
    
    /**
     * Privater Konstruktor f�r Pseudo-Element (instance).
     **/
    private DatabaseMetadata() { }
    
    private static String getProperty( String name, String def) {
        ConfigManager cm = ConfigManager.getInstance();
        
        return cm.getProperty( getInstance(), name, def);
    }
    
    /**
     * Liefert die Instanz von DatabaseMetadata zur�ck, oder 
     * erstellt eine neuen und liefert sie dann zur�ck.
     *
     * @return Die Instanz von DatabaseMetadata
     **/
    private static DatabaseMetadata getInstance() {
        if( instance == null)
            instance = new DatabaseMetadata();
        
        return instance;
    }
    
    /**
     * Liefert den Namen einer Tabelle im Datenmodell zur�ck. 
     * Der Name der Tabelle kann in der Konfigurationsdatei 
     * angepasst werden - von dort werden auch die Einstellungen 
     * hier gelesen.
     *
     * @param which Von welcher Tabelle soll der Name geholt werden? (zB GEGENSTAND)
     * @return String, der den Namen der Tabelle beinhaltet oder null, wenn es die Tabelle nicht gibt
     **/
    public static String getTableName( int which) {
        if( which == GEGENSTAND)
            return getProperty( "tabellen.gegenstand", "tab_gegenstand");
        
        if( which == GEGENSTAND_LEHRER_KLASSE)
            return getProperty( "tabellen.gegenstand-lehrer-klasse", "tab_gegenstand_lehrer_klasse");
        
        if( which == KLASSE)
            return getProperty( "tabellen.klasse", "tab_klasse");
        
        if( which == LEHRER)
            return getProperty( "tabellen.lehrer", "tab_lehrer");
        
        if( which == RAUM)
            return getProperty( "tabellen.raum", "tab_raum");
        
        if( which == SCHUELER)
            return getProperty( "tabellen.schueler", "tab_schueler");
        
        if( which == SCHUELER_GEGENSTAND)
            return getProperty( "tabellen.schueler-gegenstand", "tab_schueler_gegenstand");
        
        if( which == LEHRER_GEGENSTAND)
            return getProperty( "tabellen.lehrer-gegenstand",  "tab_lehrer_gegenstand");
        
        return null;
    }
    
    /**
     * Liefert den Connect-String f�r JDBC, mit dem die Verbindung 
     * zur Datenbank hergestellt wird. Standardm��ig ist dies eine 
     * Verbindung zu einer ODBC-Datenbank mit dem Bezeichner 
     * "EINLESEN". Dies kann in der Konfiguration ge�ndert werden.
     *
     * @return JDBC-Connect-String f�r die Datenbankverbindung
     **/
    public static String getConnectString() {
        return getProperty( "jdbc-connect-string", "jdbc:odbc:EINLESEN");
    }
    
    /**
     * Liefert den Klassennamen des Datenbank-Treibers zur�ck, der 
     * dann vom DatabaseTool geladen wird. Dies ist im Normalfall 
     * der ODBC-Treiber von Sun. Dies kann in der Konfiguration 
     * ge�ndert werden.
     *
     * @return Klassenname (+Package) des Datenbanktreibers
     **/
    public static String getDriverName() {
        return getProperty( "database-driver", "sun.jdbc.odbc.JdbcOdbcDriver");
    }
}
