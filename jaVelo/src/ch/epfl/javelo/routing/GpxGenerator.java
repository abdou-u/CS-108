package ch.epfl.javelo.routing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente un générateur d'itinéraire au format GPX.
 */

public final class GpxGenerator {

    /**
     * Ce constructeur privé permet à la classe d'etre non instanciable.
     */

    private GpxGenerator () {}

    /**
     *
     * @param route
     *          représente l'itinéraire utilisé.
     * @param profile
     *          représente le profil de l'itinéraire utilisé.
     * @return
     *          retourne le document GPX (de type Document) correspondant.
     */

    public static Document createGpx(Route route, ElevationProfile profile) {

        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        double length = 0;

        for (int i=0; i<route.points().size(); i++) {

            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.format(Locale.ROOT, "%.5f", Math.toDegrees(route.points().get(i).lat())));
            rtept.setAttribute("lon", String.format(Locale.ROOT, "%.5f", Math.toDegrees(route.points().get(i).lon())));


            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);

            if (i==0) ele.setTextContent(String.format(Locale.ROOT, "%.2f", profile.elevationAt(length)));
            else {
                    length += route.edges().get(i-1).length();
                    ele.setTextContent(String.format(Locale.ROOT, "%.2f", profile.elevationAt(length)));
            }
        }

        return doc;
    }

    /**
     *
     * @return
     *       retourne un nouveau document.
     */

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) { // Cette exception est de type checked, il faut donc l'emballer dans une autre exception de type unchecked.
            throw new Error(e); // Should never happen
        }
    }

    /**
     *
     * @param fileName
     *           représente le nom du fichier utilisé.
     * @param route
     *           représente l'itinéraire utilisé.
     * @param profile
     *           représente le profil de l'itinéraire utilisé.
     * @throws IOException
     *           lève IOException en cas d'erreur d'entrée/sortie.
     *
     * Cette classe écrit le document GPX correspondant dans le fichier.
     */

    public static void writeGpx (String fileName, Route route, ElevationProfile profile) throws IOException {

        Document doc = createGpx(route, profile);
        Writer w = Files.newBufferedWriter(Path.of(fileName));

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) { // Cette exception est de type checked, il faut donc l'emballer dans une autre exception de type unchecked.
            throw new Error(e); // Ne dois jamais se réaliser.
        }
    }
}