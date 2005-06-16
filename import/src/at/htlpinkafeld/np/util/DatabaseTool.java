/*
 * DatabaseTool.java
 *
 * Created on 16. Juni 2005, 08:18
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


package at.htlpinkafeld.np.util;

import java.sql.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Diese Klasse wird dazu verwendet, um die 
 * Datenbank anzusprechen und bietet auch 
 * einige Hilfsfunktionen, die einem das 
 * Leben leichter machen.
 * 
 * @author Thomas Perl <thp@perli.net>
 */
public class DatabaseTool {
    private static DatabaseTool instance = null;
    
    private Connection connection = null;
    
    /**
     * Erstellt eine neue Instanz des DatabaseTool.
     **/
    private DatabaseTool() {
        try
        {
            // Treiber laden
            Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver"); 
            
            // Verbindung herstellen, Connect-String steht in DatabaseMetadata
            connection = DriverManager.getConnection( DatabaseMetadata.DB_CONN_STRING); 
        }
        catch( Exception e)
        {
            Logger.warning( this, "Fehler bei DB-Verbindung: " + DatabaseMetadata.DB_CONN_STRING);
            Logger.warning( this, "Ursache für Fehler: " + e.toString());
        }
    }
    
    /**
     * Holt die Instanz des DatabaseTool. Wenn das 
     * DatabaseTool noch nicht existiert, wird es
     * beim ersten Aufruf dieser Funktion erzeugt.
     *
     * @return Laufende Instanz des DatabaseTool
     **/
    public static DatabaseTool getInstance() {
        if( instance == null)
            instance = new DatabaseTool();
        
        return instance;
    }
    
    /**
     * Schreibt ein SQLizable-Objekt in die Datenbank.
     *
     * @param object Ein SQLizable-Objekt
     * @return true, wenn alles passt, ansonsten false
     **/
    public boolean insertObject( SQLizable object) {
        String sql = object.toSqlInsert();
        Logger.debug( this, "insertObject: " + sql);
        
        return executeSQL( sql);
    }
    
    /**
     * Führt ein "DELETE * FROM" auf eine bestimmte
     * Tabelle in der Datenbank aus, um diese Tabelle 
     * komplett zu leeren und um sie für unsere neuen 
     * Daten bereit zu machen.
     *
     * @param name Der Name der Tabelle, die geleert werden soll
     * @return true, wenn der Befehl erfolgreich war, ansonsten false
     **/
    public boolean emptyTable( String name) {
        String sql = "DELETE * FROM " + name;
        Logger.debug( this, "emptyTable: " + sql);
        
        return executeSQL( sql);
    }
    
    /**
     * Führt einen SQL-Befehl auf der Datenbank aus.
     *
     * @param query Der SQL-Befehl, der auszuführen ist
     * @return true, wenn der Befehl erfolgreich war, ansonsten false
     **/
    private boolean executeSQL( String query) {
        if( connection != null) {
            try
            {
                Statement statement = connection.createStatement();
                statement.execute( query);
                
                return true;
            }
            catch( Exception e)
            {
                Logger.warning( this, "Fehler bei SQL-Abfrage: " + query);
                Logger.warning( this, "Ursache für Fehler: " + e.toString());
                
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Wandelt einen boolean-Wert in den richtigen 
     * int-Wert um, der in die Access-Datenbank 
     * eingefügt wird. Das wird für ein SQL 
     * "INSERT INTO" in die Access-Datenbank benötigt
     * und von diversen Model-Klassen aufgerufen.
     *
     * @param bool Boolean-Wert, der in den Int-Wert umgewandelt wird
     * @return int-Wert, der für ein SQL "INSERT INTO" Statement für Access verwendet werden kann
     **/
    public static int toDatabaseInt( boolean bool) {
        return bool ? -1 : 0;
    }
    
}
