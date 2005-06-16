/*
 * GruppenteilungFinder.java
 *
 * Created on 14. Juni 2005, 12:09
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
import java.io.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Diese Klasse kann aus einem Vektor von Klassen, 
 * Gegenständen und auch RelationGegenstandLehrerKlasse-Objekten 
 * diejenigen Klassen (und Gegenstände) herausfinden, bei 
 * denen eine Gruppenteilung vorliegt. Dies wird dazu benötigt, 
 * um festzustellen, welcher der n Lehrer bei einer Prüfung 
 * anwesend sein muss (zB Programmieren-Teilung).
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class GruppenteilungFinder {
    private Vector<Klasse> klassen;
    private Vector<Gegenstand> gegenstaende;
    private Vector<RelationGegenstandLehrerKlasse> relationen;
    private Vector<RelationGruppenteilung> gruppenteilung;

    /**
     * Erstellt einen neuen GruppenteilungFinder. Um 
     * eine Gruppenteilung herauszufinden, werden einige 
     * Angaben von den Importern benötigt. Zum einen 
     * eine Liste der Klassen. Dann eine Liste der
     * Gegenstände, die es gibt. Und zum Schluss noch eine 
     * Relationen-Tabelle zwischen Gegenständen, Klassen 
     * und Lehrern.
     *
     * @param klassen Ein Vektor mit den Klassen-Objekten
     * @param gegenstaende Ein Vektor mit den Gegenständen
     * @param relationen Ein Vektor mit den Relationen zwischen Lehrer, Klasse und Gegenstand
     **/
    public GruppenteilungFinder( Vector<Klasse> klassen, Vector<Gegenstand> gegenstaende, Vector<RelationGegenstandLehrerKlasse> relationen) {
        this.klassen = klassen;
        this.gegenstaende = gegenstaende;
        this.relationen = relationen;
        
        gruppenteilung = new Vector<RelationGruppenteilung>();
    }
    
    /**
     * Berechnet die Relationen.
     **/
    public void calculateRelationen() {
        Vector<Lehrer> lehrer = null;
        
        Logger.progress( this, "Gruppenteilungen werden errechnet.");
        
        for( int klassen_i = 0; klassen_i < klassen.size(); klassen_i++)
        {
            for( int gegenstaende_i = 0; gegenstaende_i < gegenstaende.size(); gegenstaende_i++)
            {
                Klasse klasse = klassen.get( klassen_i);
                Gegenstand gegenstand = gegenstaende.get( gegenstaende_i);
                
                // Die Relationen herausholen
                lehrer = RelationGegenstandLehrerKlasse.getLehrerByGegenstandKlasse( relationen, gegenstand, klasse);
                
                if( lehrer.size() == 0)
                    continue;
                
                if( lehrer.size() > 1)
                {
                    // Verarbeitung sollte hier stattfinden.
                    if( !RelationGruppenteilung.relationExists( gruppenteilung, klasse, gegenstand))
                    {
                        RelationGruppenteilung neu = new RelationGruppenteilung( klasse, gegenstand, lehrer);
                        gruppenteilung.add( neu);
                        Logger.debug( this, "Für " + klasse + " und " + gegenstand + " gibt es mehrere Lehrer.");
                    }
                }
            }
        }
        
        Logger.progress( this, "Gruppenteilungen wurden errechnet.");
    }
    
    /**
     * Nach erfolgreichem Einlesen gibt diese Funktion 
     * die Formulare in einer HTML Datei aus.
     *
     * @param filename Dateiname für die HTML Datei
     **/
    public void printToHtmlFile( String filename)
    {
        PrintWriter out;
        try
        {
            out = new PrintWriter( new FileOutputStream( new File( filename)));
        }
        catch( Exception e)
        {
            Logger.warning( this, "Cannot open output file: " + filename);
            return;
        }

        out.println( "<html><head><title>Nachprüfungsplanung-Formulare</title></head><body>");
        
        for( int i=0; i<gruppenteilung.size(); i++)
        {
            RelationGruppenteilung rgt = gruppenteilung.get(i);
            
            Gegenstand gegenstand = rgt.getGegenstand();
            Klasse klasse = rgt.getKlasse();
            Vector<Lehrer> lehrer = rgt.getLehrer();
            
            out.println( "<div style=\"page-break-inside:avoid;\">");
            out.println( "<h1>Nachprüfung für Klasse " + klasse.getName() + " (Gegenstand " + gegenstand.getName() + ")</h1>");
            out.println( "<table width=\"100%\" border=\"1\">");
            
            out.println( "<tr>");
            for( int x=0; x<lehrer.size(); x++)
            {
                Lehrer l = lehrer.get(x);
                out.println( "<th>" + l.getName() + "</th>");
            }
            out.println( "</tr>");
            
            for( int a=0; a<6; a++)
            {
                out.println( "<tr>");
                
                for( int x=0; x<lehrer.size(); x++)
                    out.println( "<td>&nbsp;</td>");

                out.println( "</tr>");
            }
            out.println( "</table>");
            out.println( "<p align=\"right\">Bitte das Formular bei Prof. Jusits abgeben (zwecks Nachprüfungsplanung).</p>");
            out.println( "<hr/>");
            out.println( "</div>");
            
        }
        
        out.println( "</body></html>");
        out.close();
    }

}
