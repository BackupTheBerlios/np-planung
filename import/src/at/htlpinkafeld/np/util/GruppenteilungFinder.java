/*
 * GruppenteilungFinder.java
 *
 * Created on 14. Juni 2005, 12:09
 */

package at.htlpinkafeld.np.util;

import java.util.*;
import java.io.*;

import at.htlpinkafeld.np.model.*;
import at.htlpinkafeld.np.devel.*;

/**
 * Diese Klasse kann aus einem Vektor von Klassen, 
 * Gegenst�nden und auch RelationGegenstandLehrerKlasse-Objekten 
 * diejenigen Klassen (und Gegenst�nde) herausfinden, bei 
 * denen eine Gruppenteilung vorliegt. Dies wird dazu ben�tigt, 
 * um festzustellen, welcher der n Lehrer bei einer Pr�fung 
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
     * Angaben von den Importern ben�tigt. Zum einen 
     * eine Liste der Klassen. Dann eine Liste der
     * Gegenst�nde, die es gibt. Und zum Schluss noch eine 
     * Relationen-Tabelle zwischen Gegenst�nden, Klassen 
     * und Lehrern.
     *
     * @param klassen Ein Vektor mit den Klassen-Objekten
     * @param gegenstaende Ein Vektor mit den Gegenst�nden
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
                        Logger.debug( this, "F�r " + klasse + " und " + gegenstand + " gibt es mehrere Lehrer.");
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
     * @param filename Dateiname f�r die HTML Datei
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

        out.println( "<html><head><title>Nachpr�fungsplanung-Formulare</title></head><body>");
        
        for( int i=0; i<gruppenteilung.size(); i++)
        {
            RelationGruppenteilung rgt = gruppenteilung.get(i);
            
            Gegenstand gegenstand = rgt.getGegenstand();
            Klasse klasse = rgt.getKlasse();
            Vector<Lehrer> lehrer = rgt.getLehrer();
            
            out.println( "<div style=\"page-break-inside:avoid;\">");
            out.println( "<h1>Nachpr�fung f�r Klasse " + klasse.getName() + " (Gegenstand " + gegenstand.getName() + ")</h1>");
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
            out.println( "<p align=\"right\">Bitte das Formular bei Prof. Jusits abgeben (zwecks Nachpr�fungsplanung).</p>");
            out.println( "<hr/>");
            out.println( "</div>");
            
        }
        
        out.println( "</body></html>");
        out.close();
    }

}
