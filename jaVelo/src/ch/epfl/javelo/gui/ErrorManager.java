package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Ahmed Abdelmalek (344471)
 * @author Youssef Neji (346960)
 *
 * Cette classe gère l'affichage des messages d'erreur.
 */

public final class ErrorManager {
    private final Text text = new Text();
    private final VBox vBox = new VBox(text);
    private final SequentialTransition sequentialTransition;

    /**
     * Le constructeur crée l'animation de l'apparition et de la disparition du panneau et les combine en une seule instance de SequentialTransition.
     */

    public ErrorManager() {
        vBox.setMouseTransparent(true);
        vBox.getStylesheets().add("error.css");

        FadeTransition fade1 = new FadeTransition(Duration.seconds(0.2));
        fade1.setToValue(0.8);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.5));
        fade2.setToValue(0);

        sequentialTransition = new SequentialTransition(vBox,fade1,pause,fade2);
    }

    /**
     *
     * @return
     *        retourne le panneau sur lequel apparaissent les messages d'erreur,
     */

    public Pane pane () {
        return vBox;
    }

    /**
     *
     * @param errorMessage
     *                   Une chaîne de caractères qui représente un message d'erreur.
     *
     * Cette méthode fait apparaître temporairement à l'écran le message d'erreur, accompagné d'un son indiquant l'erreur.
     */

    public void displayError(String errorMessage) {
        text.setText(errorMessage);
        sequentialTransition.stop();
        vBox.opacityProperty().set(0);
        sequentialTransition.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
