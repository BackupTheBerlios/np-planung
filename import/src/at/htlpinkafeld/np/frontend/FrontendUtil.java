/*
 * FrontendUtil.java
 *
 * Created on 24. Juni 2005, 17:02
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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


package at.htlpinkafeld.np.frontend;

/**
 * Einige Funktionen fürs Frontend
 *
 * @author Thomas Perl <thp@perli.net>
 */
public class FrontendUtil {
    /**
     * Zentriert einen JDialog auf dem Bildschirm.
     *
     * @param dialog Der zu zentrierende Dialog
     **/
    public static void centerForm( java.awt.Window dialog)
    {
        java.awt.Rectangle r = dialog.getBounds();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        
        dialog.setLocation((screen.width - r.width)/2, (screen.height - r.height)/2);
    }
    
    /**
     * Setzt die minimal gewünschte Größe eines Fensters auf 
     * bestimmte Werte. Der Parameter height (bzw width) kann 
     * auch auf "0" gesetzt werden, wenn die minimale Größe
     * des Fensters egal ist.
     *
     * @param dialog Das Fenster, das auf die bestimmte Größe gesetzt werden soll
     * @param height Die Mindesthöhe, die das Fenster haben soll (0 = egal)
     * @param width Die Mindestbreite, die das Fenster haben soll (0 = egal)
     **/
    public static void setMinSize( java.awt.Window dialog, int height, int width)
    {
        java.awt.Dimension size = dialog.getSize();
        
        if( size.height < height)
            size.height = height;
        
        if( size.width < width)
            size.width = width;
        
        dialog.setSize( size);
    }
}
