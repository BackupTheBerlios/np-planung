/*
 * ConfigManager.java
 *
 * Created on 20. Juni 2005, 18:12
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

import java.util.*;
import java.io.*;

import at.htlpinkafeld.np.devel.*;

/**
 * Der ConfigManager beinhaltet alle verschiedenen Konfigurationsoptionen, 
 * die man ver�ndern kann. Die Speicherung erfolgt �ber Properties-Dateien.
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class ConfigManager {
    private static ConfigManager instance = null;
    
    private Properties properties = null; /* Dieses Feld beinhaltet alle Properties. */
    
    /**
     * Erstellt einen neuen ConfigManager. Dieser Konstruktor wird
     * jeweils nur einmal pro Programmablauf aufgerufen, und dann 
     * immer nur von der Funktion getInstance().
     **/
    private ConfigManager() {
        properties = new Properties();
    }
    
    /**
     * Holt die Instanz des ConfigManagers oder erzeugt beim 
     * ersten Aufruf eine neue Instanz des ConfigManagers.
     *
     * @return Instanz des ConfigManagers
     **/
    public static ConfigManager getInstance() {
        if( instance == null)
            instance = new ConfigManager();
        
        return instance;
    }
    
    /**
     * Liest Einstellung von einer Datei aus.
     *
     * @param filename Der Dateinamer der Datei, von der die Einstellungen zu lesen sind
     **/
    public void readFromFile( String filename) throws IOException {
        Logger.progress( this, "Lade Konfigurationsdaten von \"" + filename + "\"");
        FileInputStream is = new FileInputStream( new File( filename));
        
        properties.loadFromXML( is);
    }
    
    public void saveToFile( String filename) throws IOException {
        Logger.progress( this, "Speichere Konfigurationsdaten nach \"" + filename + "\".");
        FileOutputStream os = new FileOutputStream( new File( filename));
        
        properties.storeToXML( os, "http://np-planung.berlios.de/", "UTF-8");
    }
    
    /**
     * Liefert eine Property-ID f�r eine bestimme Klasse und einen 
     * passenden Namen - damit kann ganz einfach f�r jede Klasse durch 
     * den Klassen- und Packagenamen eine einheitliche ID bestimmt werden.
     *
     * @param c Die Klasse, f�r die die Eigenschaft bestimmt ist
     * @param name Der Name der Eigenschaft
     * @return String, der die einheitliche ID f�r die Eigenschaft beinhaltet
     **/
    private String getPropertyId( Class c, String name) {
        return c.getName() + "." + name;
    }
    
    /**
     * Liefert eine Eigenschaft f�r ein bestimmtes Objekt. Dazu 
     * wird der Package- und Klassenname des Objekts genutzt (siehe 
     * Funktion getPropertyId()!).
     *
     * @param o Ein Objekt, f�r das die Eingenschaft zu holen ist
     * @param name Der Name der Eigenschaft
     * @param def "Default-Value", falls die Eigenschaft nicht gesetzt ist (oder null)
     * @return Wert der Eigenschaft, oder "Default-Value", wenn nicht gesetzt
     **/
    public String getProperty( Object o, String name, String def) {
        String key = getPropertyId( o.getClass(), name);
        return getProperty( key, def);
    }
    
    /**
     * Liefert eine Eigenschaft unter Angabe eines kompletten 
     * Key-Namens. Dies kann zB von einem Konfigurationstool 
     * verwendet werden, um gefundenen Properties direkt zu 
     * setzen, ohne vorher ein Objekt erzeugt zu haben.
     *
     * @param full_key Der komplette Name des Keys, der zu holen ist
     * @param def Der Default-Wert, falls die Eigenschaft nicht gesetzt ist (oder null)
     * @return Wert f�r diesen Key, oder Default-Wert, wenn Eigenschaft nicht gesetzt
     **/
    public String getProperty( String full_key, String def) {
        String value = properties.getProperty( full_key);
        
        /**
         * Falls wir keinen Wert haben, Default-Wert nehmen und speichern
         * (aber nur, wenn ein Default-Wert gesetzt ist..)
         **/
        if( value == null && def != null)
        {
            setProperty( full_key, def);
            value = def;
        }
        
        return value;
    }
    
    /**
     * Speichert eine Eigenschaft f�r ein bestimmtes Objekt.
     * Dazu wird der Package- oder Klassenname des Objektes genutzt 
     * (siehe Funktion getPropertyId()!). Diese Funktion speichert
     * String-Werte. Diese Funktion speichert String-Werte.
     *
     * @param o Ein Objekt, f�r das die Eigenschaft zu speichern ist
     * @param name Der Name der Eigenschaft
     * @param value Der Wert der Eigenschaft, der gespeichert werden soll
     **/
    public void setProperty( Object o, String name, String value) {
        String key = getPropertyId( o.getClass(), name);
        setProperty( key, value);
    }
    
    /**
     * Speichert eine Eigenschaft f�r ein bestimmtes Objekt.
     * Dazu wird der Package- oder Klassenname des Objektes
     * genutzt (siehe Funktion getPropertyId()!). Diese Funktion 
     * speichert int-Werte.
     *
     * @param o Ein Objekt, f�r das die Eigenschaft zu speichern ist
     * @param name Der Name der Eigenschaft
     * @param value Der Wert der Eigenschaft, der gespeichert werden soll
     **/
    public void setProperty( Object o, String name, int value) {
        String key = getPropertyId( o.getClass(), name);
        setProperty( key, value);
    }
    
    /**
     * Speichert eine Eigenschaft mit komplettem Key.
     * Diese Funktion kann String-Werte speichern.
     * 
     * @param full_key Der komplette Key zu der Eigenschaft
     * @param value Der zu speichernde Wert der Eigenschaft
     **/
    public void setProperty( String full_key, String value) {
        properties.setProperty( full_key, value);
    }
    
    /**
     * Speichert eine Eigenschaft mit komplettem Key.
     * Diese Funktion kann int-Werte speichern.
     *
     * @param full_key Der komplette Key zu der Eigenschaft
     * @param value Der zu speichernde Wert der Eigenschaft
     **/
    public void setProperty( String full_key, int value) {
        properties.setProperty( full_key, Integer.toString( value));
    }
    
    /**
     * Liefert eine Liste mit Property-Namen, die im ConfigManager
     * gesetzt sind.
     *
     * @return Vektor mit den Namen der Properties
     **/
    public Vector<String> getPropertyNames() {
        Vector<String> v = new Vector<String>();
        
        Enumeration e = properties.propertyNames();
        
        while( e.hasMoreElements())
            v.add( (String)e.nextElement());
        
        return v;
    }
}
