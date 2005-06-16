/*
 * DatabaseMetadata.java
 *
 * Created on 16. Juni 2005, 07:54
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
    /* ================= EINSTELLUNGEN ==================== */
    
    // Der JDBC Connect-String f�r die Datenbank
    public static final String DB_CONN_STRING = "jdbc:odbc:EINLESEN";
    
    /* =================== TABELLEN ======================= */
    
    // Tabelle f�r die Liste der Gegenst�nde
    public static final String T_GEGENSTAND = "tab_gegenstand";
    
    // Tabelle f�r die Beziehungen Gegenstand-Lehrer-Klasse
    public static final String T_GEGENSTAND_LEHRER_KLASSE = "tab_gegenstand_lehrer_klasse";
    
    // Tabelle f�r die Liste der Klassen
    public static final String T_KLASSE = "tab_klasse";
    
    // Tabelle f�r die Liste der Lehrer
    public static final String T_LEHRER = "tab_lehrer";
    
    // Tabelle f�r die Liste der R�ume
    public static final String T_RAUM = "tab_raum";
    
    // Tabelle f�r die Liste der Sch�ler
    public static final String T_SCHUELER = "tab_schueler";
    
    // Tabelle f�r die Beziehungen Sch�ler-Gegenstand
    public static final String T_SCHUELER_GEGENSTAND = "tab_schueler_gegenstand";
}
