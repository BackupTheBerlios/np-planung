/*
 * GruppenteilungFinder.java
 *
 * Created on 14. Juni 2005, 12:09
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
     * Sortiert die erstellten Gruppenteilung-Relationen 
     * nach der Abteilung. Dies wird f�r das abteilungsm��ige 
     * Ausdrucken der Gruppenteilungs-Formulare ben�tigt.
     **/
    private void sortRelationenByAbteilung() {
        boolean isSorted = false;
        
        while( !isSorted) {
            isSorted = true;
            
            for( int i=0; i<gruppenteilung.size()-1; i++)
            {
                RelationGruppenteilung a = gruppenteilung.get(i);
                RelationGruppenteilung b = gruppenteilung.get(i+1);
                
                if( a.getKlasse().compareToByAbteilung( b.getKlasse()) > 0)
                {
                    gruppenteilung.setElementAt( b, i);
                    gruppenteilung.setElementAt( a, i+1);
                    isSorted = false;
                }
            }
        }
    }

    /**
     * Liefert den Namen der Gruppenteilung-HTML Datei zur�ck, die 
     * erstellt wird.
     *
     * @return Dateiname der Output-Datei f�r Gruppenteilung-Formulare
     **/
    public String getGruppenHtmlFilename() {
        return ConfigManager.getInstance().getProperty( this, "html-file", "c:\\Gruppenteilung-Formulare.html");
    }
    
    /**
     * Nach erfolgreichem Einlesen gibt diese Funktion 
     * die Formulare in einer HTML Datei aus. Die Zieldatei 
     * wird in der Konfigurationsdatei bestimmt.
     **/
    public void printToHtmlFile()
    {
        String filename = getGruppenHtmlFilename();
        
        // Relationen nach Abteilung sortieren
        sortRelationenByAbteilung();
        
        Logger.progress( this, "Schreibe Gruppenteilung HTML Datei in " + filename);
        
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

        /**
         * Hier folgt unser "sch�ner" Dokumenten-Header
         **/
        out.println( "<div align=\"center\" style=\"font-size: 48pt;\"><br><br><br><br><br>Nachpr�fungsplanung<br>Formulare<br><font style=\"font-size: 16pt;\">Copyright &copy; 2005 Nachpr�fungsplanungsteam</font></div>");
        
        // Abteilungswechsel-Check-Variablen
        boolean isNewAbteilung = false;
        String oldAbteilung = "";
        
        for( int i=0; i<gruppenteilung.size(); i++)
        {
            RelationGruppenteilung rgt = gruppenteilung.get(i);
            
            Gegenstand gegenstand = rgt.getGegenstand();
            Klasse klasse = rgt.getKlasse();
            Vector<Lehrer> lehrer = rgt.getLehrer();

            isNewAbteilung = false;
            if( oldAbteilung.compareTo( klasse.getAbteilung()) != 0)
            {
                isNewAbteilung = true;
                oldAbteilung = klasse.getAbteilung();
            }
            
            if( isNewAbteilung)
            {
                out.println( "<div style=\"page-break-before: always;\">");
                out.println( "<h1>Abteilung " + oldAbteilung + "</h1>");
            }
            
            out.println( "<h2>Nachpr�fung f�r Klasse " + klasse.getName() + " (Gegenstand " + gegenstand.getName() + ")</h2>");
            out.println( "<table width=\"100%\" border=\"1\">");
            
            out.println( "<tr>");
            for( int x=0; x<lehrer.size(); x++)
            {
                Lehrer l = lehrer.get(x);
                out.println( "<th>" + l.getName() + "</th>");
            }
            out.println( "</tr>");
            
            int anzahl = 3;
            String anz_string = ConfigManager.getInstance().getProperty( this, "html-anzahl", Integer.toString( anzahl));
            
            try {
                anzahl = Integer.parseInt( anz_string);
            }
            catch( NumberFormatException e) {
                Logger.warning( this, "Fehler beim Umwandeln von Anzahl f�r \"" + anz_string + "\": " + e.toString());
            }
            
            for( int a=0; a<anzahl; a++)
            {
                out.println( "<tr>");
                
                for( int x=0; x<lehrer.size(); x++)
                    out.println( "<td>&nbsp;</td>");

                out.println( "</tr>");
            }
            out.println( "</table>");
            
            if( isNewAbteilung)
            {
                out.println( "</div>");
            }
        }
        
        out.println( "</body></html>");
        out.close();
        
        Logger.progress( this, "Gruppenteilung-Datei geschrieben.");
    }

}
