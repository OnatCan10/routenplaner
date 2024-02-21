package de.uni_bremen.pi2;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Onat Can Vardareri
 *
 * Die Klasse liest die Karte aus Knoten und Kanten ein und
 * repräsentiert diese. Die Daten stammen ursprünglich aus Open
 * Street Map (OSM). Dabei werden für jede eingelesene Kante zwei
 * gerichtete Kanten in die Karte eingetragen. Die Klasse stellt
 * eine Methode zur Verfügung, um die Karte zu zeichnen, sowie
 * eine, die zu einem Punkt den dichtesten Knoten ermittelt.
 */
class Map
{
    /**
     * Map für alle knoten
     */
    private final java.util.Map<Integer, Node> nodes = new HashMap<>();
    /**
     * Array von String für 3. argument edgeType aus readme.txt
     */
    private static final String[] edgeType = {
            "primary",
            "primaryLink",
            "secondary",
            "secondaryLink",
            "tertiary",
            "unclassified",
            "residential",
            "livingStreet",
            "path",
            "cycleway",
            "rack",
            "footway",
            "track",
            "notKnownYet"
    };
    /**
     * Konstruktor. Liest die Karte ein.
     * @throws FileNotFoundException Entweder die Datei "nodes.txt" oder die
     *         Datei "edges.txt" wurden nicht gefunden.
     * @throws IOException Ein Lesefehler ist aufgetreten.
     */
    Map() throws FileNotFoundException, IOException
    {

        final java.util.Map<String, Integer> type = new HashMap<>();

        //Ordnet den verschiedenen Kantentypen unterschiedliche Geschwindigkeiten zu.
        for (int i = 0; i < edgeType.length; ++i) {
            type.put(edgeType[i], i);
        }

        //nodes.txt datei lesen solange es argumente gibt
        try (final Scanner stream = new Scanner(new FileInputStream("nodes.txt"))) {
            while (stream.hasNext()) {
                //erste argument id
                int id = stream.nextInt();
                //zweite argument x koordinate
                double x = stream.nextDouble();
                //dritte argument y koordinate
                double y = stream.nextDouble();
                //wenn alle argumente gelesen sind,erstelle Knoten und füge die in map hinzu.
                nodes.put(id, new Node(id, x , y));
            }
        }
        //edges.txt datei lesen solange es argumente gibt
        try (final Scanner stream = new Scanner(new FileInputStream("edges.txt"))) {
            while (stream.hasNext()) {
                //erste argument
                Node node1 = nodes.get(stream.nextInt());
                //zweite argument
                Node node2 = nodes.get(stream.nextInt());
                //distance zwischen die beiden
                double distance = node1.distance(node2);
                //edges hinzufügen
                node1.getEdges().add(new Edge(node2, distance));
                node2.getEdges().add(new Edge(node1, distance));
                //drite argument
                String typeName = stream.nextLine().substring(1);
            }
        }
        // Lest hier die beiden Dateien nodes.txt und edges.txt ein
        // und erzeugt daraus eine Karte aus Node- und Edge-Objekten
        // Verbindungen sollen immer in beide Richtungen gehen, d.h.
        // ihr braucht zwei Edges pro Zeile aus der edges.txt.
    }

    /** Zeichnen der Karte. */
    void draw()
    {
        //für alle knoten aus map und deren edges zeichene zwischen knote und ziel
        for (Node n : nodes.values()) {
            for (Edge e : n.getEdges()) {
                n.draw(e.getTarget(), Color.BLACK);
            }
        }
        // Zeichnet hier alle Kanten der Karte. Hierzu sollte Node.draw benutzt werden.
        // Es können die Original-Koordinaten aus den Knoten benutzt werden. Diese werden
        // automatisch geeignet skaliert.
    }

    /**
     * Findet den dichtesten Knotens zu einer gegebenen Position.
     * @param x Die x-Koordinate.
     * @param y Die y-Koordinate.
     * @return Der Knoten, der der Position am nächsten ist. null,
     *         falls es einen solchen nicht gibt.
     */
    Node getClosest(final double x, final double y)
    {
        //erstelle ein Node nach eingabe parametern.
        Node point = new Node(0,x, y);
        //setzt min auf maximum value
        double min = Double.MAX_VALUE;
        Node point2 = null;
        //für alle knoten aus nodes map
        for (Node node : nodes.values()) {
            //distance ist gleich distance von point und ein node aus map
            double distance = node.distance(point);
            //wenn distance kleiner als min ist,sind point2 gleich node und min gleich distance
            if (min > distance) {
                point2 = node;
                min = distance;
            }
        }
        return point2; // Ersetzen
    }

    /** Löschen aller Vorgängereinträge und Setzen aller Kosten auf unendlich. */
    void reset()
    {
        double unendlich = Double.POSITIVE_INFINITY;
        for (final Node node : nodes.values()) {
            node.reachedFromAtCosts(null,unendlich);
        }
        // Nutzt Node.reachedFromAtCosts für jeden Knoten.
    }
}
