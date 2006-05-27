/*
 * Logger.java
 *
 * Created on 01. Juni 2005, 17:29
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


package at.htlpinkafeld.np.devel;

import java.io.*;

import at.htlpinkafeld.np.frontend.*;

/**
 * Die Klasse Logger wird vom Nachpr�fungseinlesenprogramm 
 * dazu verwendet, um verschiedenste Meldungen aus dem 
 * Code in einem Platz zu vereinen. Von hier aus kann auch 
 * die automatische Verteilung an verschiedenste andere 
 * Teile des Programms erfolgen, wenn ben�tigt.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class Logger {
    // Logging aktivieren?
    private static final boolean isLogging = true;
    
    // Logging aktivieren f�r Debugging?
    private static boolean isDebugging = false;
    
    // Logging aktivieren f�r Progressanzeigen?
    private static final boolean isProgress = true;
    
    /**
     * Loggt eine Debug-Ausgabe mit (und gibt sie 
     * ggf. aus).
     *
     * @param o Das Objekt, das die Debug-Ausgabe aussendet
     * @param msg Die Debug-Nachricht
     **/
    public static void debug( Object o, String msg) {
        if( isDebugging)
            log( "debug(" + getModuleName( o) + "): " + msg, false);
    }
    
    /**
     * Loggt eine Ausgabe-Nachricht mit (und gibt sie ggf. aus).
     *
     * @param o Das Objekt, das die Nachricht aussendet
     * @param msg Die Ausgabe-nachricht
     **/
    public static void message( Object o, String msg) {
        log( "message(" + getModuleName( o) + "): " + msg, false);
    }
    
    /**
     * Loggt eine Warnung mit. Dies wird immer angezeigt, 
     * da es hilfreiche Hinweise auf Fehler in den Daten 
     * geben kann.
     *
     * @param o Das Objekt, das die Debug-Ausgabe aussendet
     * @param msg Die Warnungs-Nachricht
     **/
    public static void warning( Object o, String msg) {
        log( "warning(" + getModuleName( o) + "): " + msg, true);
    }
    
    /**
     * Loggt eine Progressanzeige mit. Dies wird dazu 
     * ben�tigt, um anzuzeigen, was das Programm gerade 
     * macht.
     *
     * @param o Das Objekt, das die Progressanzeige aussendet
     * @param msg Die Progressanzeige
     **/
    public static void progress( Object o, String msg) {
        log( "progress(" + getModuleName( o) + "): " + msg, false);
    }
    
    /**
     * Schreibt die Nachricht in das Logging-Ziel - 
     * diese Funktion wird von anderen Funktionen 
     * aufgerufen.
     *
     * @param msg Die Nachricht, die ausgegeben wird
     **/
    private static void log( String msg, boolean isWarning) {
        if( isLogging)
        {
            PrintStream out;
            
            if( isWarning)
                out = System.err;
            else
                out = System.out;
                
            out.println( msg);
        }
        
        MainDialog dialog = MainDialog.getInstance();
        
        if( dialog != null)
        {
            dialog.logMessage( msg);
        }
    }
    
    /**
     * Liefert den "h�bschen" Modulnamen eines Objektes.
     * Diese Funktion wird dazu verwendet, um den Namen 
     * eines Objektes (welches Nachrichten sendet) in 
     * einer sch�nen Form darzustellen. Dies ist meistens 
     * der Klassenname (ohne Package-Pfad). F�r Ausnahmen 
     * siehe bitte im Sourcecode.
     *
     * @param o Ein Objekt, dessen Modulname geholt werden soll
     * @return Ein String, der den "h�bschen" Namen des Moduls darstellt
     **/
    private static String getModuleName( Object o) {
        String name = o.getClass().getName();
        
        return name.substring( name.lastIndexOf( ".")+1);
    }
    
    /**
     * Setzt, ob Debugging eingeschaltet werden soll oder nicht.
     *
     * @param debugging true, wenn Debugging aktiviert werden soll, sonst false
     **/
    public static void setDebugging( boolean debugging) {
        isDebugging = debugging;
        
        if( isDebugging)
            log( "Debug-Ausgabe aktiviert.", false);
        else
            log( "Debug-Ausgabe deaktiviert.", false);
    }
}
