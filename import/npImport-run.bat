@echo off
rem ################################################
rem npImport - Nachprüfungsplanung Einlesen-Programm
rem  Copyright (c) 2005 Thomas Perl <thp@perli.net>
rem ################################################
rem
rem Diese Datei startet das Einlesen-Programm.
rem 
rem Zuerst muss "ant" aufgerufen werden, welches durch die
rem build.xml die npImport.jar aus dem Sourcecode erzeugt.
rem 
title Nachpruefungsplanung Einlesen-Programm (laeuft..)
java -classpath deps/ostermillerutils_1_04_03.jar;dist/npImport.jar npimport.Main
title Nachpruefungsplanung Einlesen-Programm (beendet.)
echo.
echo.
echo Das Nachprüfungsplanung Einlesen-Programm wurde beendet.
echo.
echo.
pause
