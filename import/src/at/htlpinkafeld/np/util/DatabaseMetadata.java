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
