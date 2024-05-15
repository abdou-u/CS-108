package ch.epfl.javelo.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author Ahmed Abdelmalek (344471)
 *
 * Cette classe représente le temps d'itinéraire.
 */

public final class TimeManager {

    private final RouteBean routeBean;
    private final Consumer<String> consumer;
    private final JFrame jFrameSpeed = new JFrame("Vitesse");
    private final JFrame jFrameTime = new JFrame("Temps");
    private final IntegerProperty secs = new SimpleIntegerProperty(), speed = new SimpleIntegerProperty(), s = new SimpleIntegerProperty(), m = new SimpleIntegerProperty(), h = new SimpleIntegerProperty();
    private final StringProperty timeProperty = new SimpleStringProperty();
    private final JLabel jLabelText = new JLabel();
    private final Menu time = new Menu("Temps de route");
    private final CheckMenuItem showTime = new CheckMenuItem("Afficher le temps");

    public TimeManager (RouteBean routeBean, Consumer<String> consumer) {

        this.routeBean = routeBean;
        this.consumer = consumer;

        time.getItems().add(showTime);

        jFrameSpeed.setMinimumSize(new Dimension(160,430));
        JPanel panelSpeed = new JPanel();
        jFrameSpeed.setContentPane(panelSpeed);
        jFrameSpeed.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel jLabelSpeed = new JLabel("Choisissez votre vitesse");
        jFrameSpeed.getContentPane().add(jLabelSpeed);

        jFrameTime.setMinimumSize(new Dimension(260,160));
        JPanel panelTime = new JPanel();
        jFrameTime.setContentPane(panelTime);
        jFrameTime.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel jLabelTime = new JLabel("Temps d'itinéraire");
        jFrameTime.getContentPane().add(jLabelTime);

        jFrameTime.addWindowListener(windowListener());

        routeBean.routeProperty().addListener(observable -> changeTime());

        timeProperty.bind(Bindings.createStringBinding(() -> String.format(Locale.ROOT, "%02d:%02d:%02d", h.get(), m.get(), s.get()), h, m, s));

        windowListener();
        updateSpeed();
    }

    /**
     *
     * @return
     *        retourne le Menu.
     */

    public Menu getTime () {
        return time;
    }

    /**
     *
     * @return
     *       retourne le JFrame qui contient les vitesses.
     */

    public JFrame getJFrameSpeed() {
        return jFrameSpeed;
    }

    /**
     *
     * @return
     *       retourne le JFrame qui contient le temps de trajet.
     */

    public JFrame getJFrameTime() {
        return jFrameTime;
    }

    /**
     *
     * @return
     *       retourne l'item qui est une instance de CheckMenuItem qui est contenu dans le Menu time.
     */

    public CheckMenuItem getShowTime() {
        return showTime;
    }

    /**
     * Cette méthode est appelée dans la classe jaVelo si le checkMenuItem est appuyé.
     */

    public void start () {
        if (showTime.isSelected()) jFrameSpeed.setVisible(true);
        else {
            if (jFrameSpeed.isShowing()) jFrameSpeed.dispose();
            else {
                jFrameTime.getContentPane().remove(1);
                jFrameTime.dispose();
            }
        }
    }

    /**
     * Cette méthode représente le changement de temps chaque fois que la route change.
     */

    private  void changeTime () {
        if (jFrameTime.isShowing()) {
            double length = 0;
            if (routeBean.route()!=null) length = routeBean.route().length();
            secs.set((int) (length / (speed.get() / 3.6)));
            h.set(secs.get()/3600);
            m.set((secs.get() % 3600) / 60);
            s.set(secs.get() % 60);
            jLabelText.setText(timeProperty.get()); //Puisque chaque fois que h, m ou s changent, timeProperty change.
        }
    }

    /**
     * Cette méthode remplie les JFrame qui affiche les vitesses avec les vitesses nécessaires et si un boutton est appuyé et la route
     * n'est pas nulle, on affiche pour la première fois le JFrame contenant le temps de trajet (1), sinon un message d'erreur s'affiche (2).
     * Si le JFrame qui montre le temps du trajet s'affiche, le JFrame qui montre les vitesses se ferme.
     */

    private void updateSpeed () {
        for (int i = 10; i<=50;) {
            JButton button = new JButton(i + "km/h");
            jFrameSpeed.getContentPane().add(button);
            int finalI = i;
            button.addActionListener(event -> {
                if (routeBean.route()!=null) { // (1)
                    speed.set(finalI);
                    secs.set((int) (routeBean.route().length() / (finalI / 3.6)));
                    h.set(secs.get()/3600);
                    m.set((secs.get() % 3600) / 60);
                    s.set(secs.get() % 60);
                    jLabelText.setText(timeProperty.get());
                    jLabelText.setBorder(BorderFactory.createBevelBorder(1));
                    jLabelText.setFont(new Font("Verdana", Font.PLAIN, 35));
                    jFrameTime.getContentPane().add(jLabelText);
                    jFrameTime.setVisible(true);
                } else { // (2)
                    consumer.accept("Pas d'itinéraire !");
                    showTime.setSelected(false);
                }
                jFrameSpeed.dispose();
            });
            i+=5;
        }
    }

    /**
     *
     * @return
     *       Retourne une instance de WindowListener.
     *
     * Cette méthode est appelée dans le constructeur puisque je veux que si le JFrame se ferme sans que vous appuyiez sur le checkMenuItem,
     * (en appuyant par exemple sur le x (sortie) en haut à droite du JFrame) l'item n'est plus sélectionné.
     */

    private WindowListener windowListener () {
        return new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {showTime.setSelected(false);}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        };
    }
}
