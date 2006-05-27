/*
 * DBWriter.java
 *
 * Created on 16. Juni 2005, 08:57
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

import java.util.*;
import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Der DBWriter ist einfach eine Container-Klasse, 
 * die mehrere Objekte beinhalten kann, die das 
 * Interface "Databaseable" implementieren. Der 
 * DBWriter kann somit alle "Databaseable" Objekte 
 * festhalten und auf einmal in die Datenbank schreiben.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class DBWriter {
    private Vector<Databaseable> dbo = null; // Liste mit Databaseable Objekten
    
    /**
     * Erstellt einen neuen DBWriter.
     **/
    public DBWriter() {
        dbo = new Vector<Databaseable>();
    }
    
    /**
     * Fügt ein neues Databaseable-Objekt zum 
     * DBWriter hinzu, welches später beim Schreiben 
     * in die Datenbank berücksichtigt wird.
     *
     * @param object Databaseable-Objekt, das hinzugefügt werden soll
     **/
    public void addDatabaseable( Databaseable object) {
        dbo.add( object);
    }
    
    /**
     * Schreibt alle Databaseable-Objekte in die Datenbank.
     *
     * @return true, wenn erfolgreich, sonst false
     **/
    public boolean writeAll() {
        Logger.progress( this, "Das Schreiben in die Datenbank wird gestartet.");
                
        for( int i=0; i<dbo.size(); i++)
        {
            Databaseable object = dbo.get(i);
            Logger.progress( this, "Schreibe Daten: " + object.getDescription());
            
            // Daten in die Datenbank schreiben, wenn Fehler: beenden
            if( object.allToDatabase() == false)
            {
                Logger.warning( this, "Fehler beim Schreiben von Objekt: " + object.getDescription());
                return false;
            }
        }
        
        Logger.progress( this, "Das Schreiben in die Datenbank wurde erfolgreich beendet.");
        
        return true;
    }
    
}
