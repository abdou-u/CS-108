package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 *
 * @author Ahmed Abdelmalek (344471)
 *
 * Cet enregistrement représente un point de passage.
 * @param pointCh : représente la position du point de passage dans le système de coordonnées suisse.
 * @param nodeId : représente l'identité du nœud JaVelo le plus proche de ce point de passage.
 */

public record Waypoint(PointCh pointCh, int nodeId) {}
