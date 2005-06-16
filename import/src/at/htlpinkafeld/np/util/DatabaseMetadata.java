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
 * Änderung der Datenbank (zB Umbenennung der Tabellen) 
 * nur diese Klasse zu ändern.
 * 
 * @author Thomas Perl <thp@perli.net>
 */
public class DatabaseMetadata {
    /* ================= EINSTELLUNGEN ==================== */
    
    // Der JDBC Connect-String für die Datenbank
    public static final String DB_CONN_STRING = "jdbc:odbc:EINLESEN";
    
    /* =================== TABELLEN ======================= */
    
    // Tabelle für die Liste der Gegenstände
    public static final String T_GEGENSTAND = "tab_gegenstand";
    
    // Tabelle für die Beziehungen Gegenstand-Lehrer-Klasse
    public static final String T_GEGENSTAND_LEHRER_KLASSE = "tab_gegenstand_lehrer_klasse";
    
    // Tabelle für die Liste der Klassen
    public static final String T_KLASSE = "tab_klasse";
    
    // Tabelle für die Liste der Lehrer
    public static final String T_LEHRER = "tab_lehrer";
    
    // Tabelle für die Liste der Räume
    public static final String T_RAUM = "tab_raum";
    
    // Tabelle für die Liste der Schüler
    public static final String T_SCHUELER = "tab_schueler";
    
    // Tabelle für die Beziehungen Schüler-Gegenstand
    public static final String T_SCHUELER_GEGENSTAND = "tab_schueler_gegenstand";
}
