package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import java.util.Locale;

/**
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe gère l'affichage et l'interaction avec le profil en long d'un itinéraire.
 */

public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty position;
    private final BorderPane borderPane;
    private final Path path = new Path();
    private final Polygon polygon = new Polygon();
    private final Group group = new Group();
    private final Line line = new Line();
    private final Pane pane = new Pane(path, polygon, line, group);
    private final Insets insets = new Insets(10, 10, 20, 40); //La distance entre les bords du rectangle bleu et du panneau pane.
    private final Text text = new Text();
    private final ObjectProperty<Transform> screenToWorldProperty = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreenProperty = new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Rectangle2D> rectangle2D = new SimpleObjectProperty<>();
    private final DoubleProperty mousePosition = new SimpleDoubleProperty(Double.NaN);
    private final int POSITION_FACTOR = 2, METER_KILOMETER_RATIO = 1000;

    /**
     *
     * @param elevationProfile
     *                 représente une propriété contenant le profil à afficher.
     * @param position
     *                 représente une propriété contenant la position le long du profil à mettre en évidence.
     */

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position) {

        this.elevationProfile = elevationProfile;
        this.position = (DoubleProperty) position;

        VBox vBox = new VBox(text);
        vBox.setId("profile_data");
        path.setId("grid");
        polygon.setId("profile");
        borderPane = new BorderPane(pane, null, null, vBox, null);
        borderPane.getStylesheets().add("elevation_profile.css");

        rectangle2D.bind(Bindings.createObjectBinding(this::createRectangle, pane.widthProperty(), pane.heightProperty())); // Le changement du pane entraine le changement du rectangle.
        rectangle2D.addListener(this::transformations); // Le changement du rectangle entraine le changement des deux transformations.
        worldToScreenProperty.addListener(this::updatePolygonAndLines);// Le changement des transformations entraine le changement de la grille (des lignes) et des textes.
        elevationProfile.addListener(this::transformations); // Le changement de l'élévation du profil entraine le changement des transformations.

        adjustHighlightedPositionAndLinePosition(); // Ajuste la position du cercle et de la ligne (Line).
        updateLine(); // Relie les propriétés de la ligne au rectangle.
    }

    /**
     * Cette méthode ajuste la position du cercle (highlightPosition) et de la ligne (position).
     * Si la souris est sur le profil de l'élévation, alors le cercle est visible et a la meme position que la ligne dans le profil. (1)
     * Sinon le cercle n'est pas visible. (2)
     * Si la souris quitte le profil de l'élévation, alors le cercle n'est pas visible. (3)
     */

    private void adjustHighlightedPositionAndLinePosition () {
        pane.setOnMouseMoved(event -> { // (1)
            if (rectangle2D.get().contains(event.getX(), event.getY())) {
                mousePosition.set(Math.round(screenToWorldProperty.get().transform(event.getX(), 0).getX()));
                position.unbind();
                position.set(event.getX());
            } else { // (2)
                mousePosition.set(Double.NaN);
                position.unbind();
                position.set(Double.NaN);
            }
        });
        pane.setOnMouseExited(event -> { // (3)
            mousePosition.set(Double.NaN);
            position.unbind();
            position.set(Double.NaN);
        });
    }

    /**
     *
     * @param observable
     *                Permet à cette méthode d'etre observée pour evaluation (appel dans le constructeur dès que les transformations changent).
     *
     * Cette méthode dessine le profil de l'élévation et fait appel aux méthodes qui dessinent la grille (puisque la grille change si le profil change).
     */

    private void updatePolygonAndLines(Observable observable) {
        polygon.getPoints().clear();
        group.getChildren().clear();
        path.getElements().clear();
        polygon.getPoints().addAll(rectangle2D.get().getMinX(), rectangle2D.get().getMaxY()); // Le point correspondant à la partie inférieure et minimale situé à l'extrémité gauche du profil.
        for(int i=(int) rectangle2D.get().getMinX(); i<rectangle2D.get().getMaxX(); i++) {
            double x = screenToWorldProperty.get().transform(i,0).getX();
            Point2D point = worldToScreenProperty.get().transform(x, elevationProfile.get().elevationAt(x));
            polygon.getPoints().addAll(point.getX(), point.getY());
        }
        polygon.getPoints().addAll(rectangle2D.get().getMaxX(), rectangle2D.get().getMaxY()); // Le point correspondant à la partie inférieure et minimale situé à l'extrémité droite du profil.

        verticalLinesAndHorizontalText(); horizontalLinesAndVerticalText(); text.setText(statistics());
    }

    /**
     *
     * @param observable
     *             Permet à cette méthode d'etre observée pour evaluation (appel dans le constructeur dès que le rectangle ou l'élévation changent).
     *
     * Cette méthode représente les deux transformations demandées.
     */
    private void transformations(Observable observable) {
        if (rectangle2D.get()!=Rectangle2D.EMPTY && elevationProfile.get()!=null) {
            Affine screenToWorld = new Affine();
            screenToWorld.prependTranslation(-rectangle2D.get().getMinX(), -rectangle2D.get().getMaxY());
            screenToWorld.prependScale(elevationProfile.get().length()/rectangle2D.get().getWidth() , -(elevationProfile.get().maxElevation()-elevationProfile.get().minElevation())/rectangle2D.get().getHeight());
            screenToWorld.prependTranslation(0, elevationProfile.get().minElevation());
            screenToWorldProperty.set(screenToWorld);
            try {
                worldToScreenProperty.set(screenToWorldProperty.get().createInverse());
            } catch (NonInvertibleTransformException e) {
                throw new Error();
            }
        }
    }

    /**
     * Cette méthode relie les 4 propriétés de la ligne (position mise en évidence) au rectangle.
     */

    private void updateLine () {
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreenProperty.get().transform(position.get(), 0).getX(), position));
        line.startYProperty().bind(Bindings.select(rectangle2D, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2D, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));
    }

    /**
     *
     * @return
     *       Retourne un nouveau rectangle.
     *
     * Cette méthode est appelée dans la méthode "updateRectangle" qui prend une nouvelle instance de rectangle en paramètre.
     * Elle retourne un triangle de longeur et de largeur 0, si les propriétés du pane ne sont pas valides.
     */

    private Rectangle2D createRectangle () {
        if ((pane.getWidth()>=insets.getRight()+insets.getLeft()) && (pane.getHeight()>=insets.getTop()+insets.getBottom())) {
            return new Rectangle2D(insets.getLeft(), insets.getTop(), pane.getWidth() - (insets.getRight() + insets.getLeft()), pane.getHeight() - (insets.getTop() + insets.getBottom()));
        }
        else return Rectangle2D.EMPTY;
    }

    /**
     * Cette méthode dessine les lignes verticales et le texte horizontal de la grille.
     */

    private void verticalLinesAndHorizontalText () {
        int[] POS_STEPS =
                {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};

        for (int posStep : POS_STEPS) {
            if ((worldToScreenProperty.get().deltaTransform(posStep, 0).getX() >= 50) || posStep== 100_000) {
                int y=0;
                for (double i = rectangle2D.get().getMinX(); i < rectangle2D.get().getMaxX();) {
                    PathElement moveTo = new MoveTo(i, rectangle2D.get().getMaxY()); //les coordonnées sont celles d'une extrémité de la ligne.
                    PathElement lineTo = new LineTo(i, rectangle2D.get().getMinY()); //les coordonnées sont celles de l'autre extrémité de la ligne.
                    path.getElements().add(moveTo);
                    path.getElements().add(lineTo);
                    //Mettre le texte
                    Text text = new Text();
                    text.setFont(Font.font("Avenir", 10));
                    text.setTextOrigin(VPos.TOP);
                    text.getStyleClass().add("grid_label");
                    text.getStyleClass().add("horizontal");
                    text.setText(String.valueOf((y*posStep)/METER_KILOMETER_RATIO));
                    text.setLayoutY(rectangle2D.get().getMaxY());
                    text.setLayoutX(i - (text.prefWidth(0)/POSITION_FACTOR));
                    group.getChildren().add(text);
                    i+=worldToScreenProperty.get().deltaTransform(posStep, 0).getX();
                    y++;
                }
                return; //S'il trouve les bons steps pas la peine de continuer dans la boucle.
            }
        }
    }

    /**
     * Cette méthode dessine les lignes horizontales et le texte vertical de la grille.
     */

    private void horizontalLinesAndVerticalText () {
        int[] ELE_STEPS =
                {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

        for (int eleStep : ELE_STEPS) {
            if ((-worldToScreenProperty.get().deltaTransform(0, eleStep).getY() >= 25) || eleStep== 1_000) {
                int z=-1;
                do {
                    z++;
                } while (z*eleStep<elevationProfile.get().minElevation());
                double deltaY = -worldToScreenProperty.get().deltaTransform(0, eleStep).getY();
                double startY = rectangle2D.get().getMaxY() - worldToScreenProperty.get().deltaTransform(0, elevationProfile.get().minElevation()).getY() - z*deltaY;
                int y=0;
                for (double i = startY; i > rectangle2D.get().getMinY();) {
                    PathElement moveTo = new MoveTo(rectangle2D.get().getMinX(), i); //les coordonnées sont celles d'une extrémité de la ligne.
                    PathElement lineTo = new LineTo(rectangle2D.get().getMaxX(), i); //les coordonnées sont celles de l'autre extrémité de la ligne.
                    path.getElements().add(moveTo);
                    path.getElements().add(lineTo);
                    //Mettre le text
                    Text text = new Text();
                    text.setFont(Font.font("Avenir", 10));
                    text.setTextOrigin(VPos.CENTER);
                    text.getStyleClass().add("grid_label");
                    text.getStyleClass().add("vertical");
                    text.setText(String.valueOf((z+y)*eleStep));
                    text.setLayoutY(i);
                    text.setLayoutX(rectangle2D.get().getMinX() - (text.prefWidth(0)+POSITION_FACTOR));
                    group.getChildren().add(text);
                    i-=deltaY;
                    y++;
                }
                return; //S'il trouve les bons steps pas la peine de continuer dans la boucle.
            }
        }
    }

    /**
     *
     * @return
     *        retourne les statistiques de l'itinéraire, sous la forme d'un String, présentées dans le bas du panneau.
     */

    private String statistics() {
        return String.format(Locale.ROOT,
                "Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",elevationProfile.get().length()/METER_KILOMETER_RATIO,
                                                             elevationProfile.get().totalAscent(), elevationProfile.get().totalDescent(),
                                                             elevationProfile.get().minElevation(), elevationProfile.get().maxElevation());
    }

    /**
     *
     * @return
     *        retourne le panneau contenant le dessin du profil.
     */

    public BorderPane pane() {
        return borderPane;
    }

    /**
     *
     * @return
     *        retourne une propriété en lecture seule contenant la position du pointeur de la souris le long du profil
     *        (en mètres, arrondie à l'entier le plus proche), ou NaN si le pointeur de la souris ne se trouve pas au-dessus du profil.
     */

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }
}