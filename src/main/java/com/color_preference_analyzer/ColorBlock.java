package com.color_preference_analyzer;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A JPanel intended to be monitored for when it is clicked and the duration of a mouse hover.
 */
public class ColorBlock extends JPanel {
    private Counter clicksCounter;
    private Summary hoverDuration;
    private Summary.Timer hoverTimer;

    /**
     * Constructor with no prometheus metrics.
     * @param size The size of this color block panel.
     */
    public ColorBlock(Dimension size) {
        super();
        this.setSize(size);
    }

    /**
     * Constructor not including duration calculations.
     * @param size The size of this color block panel.
     * @param clicks Prometheus counter for the number of clicks on a color.  Uses color hex as label.
     */
    public ColorBlock(Dimension size, Counter clicks) {
        super();
        this.setSize(size);

        clicksCounter = clicks;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                clicksCounter.labels(getCurrentColorString()).inc();

                darken();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Send event to parent object to signal color update.
                getParent().dispatchEvent(e);
            }
        });
    }

    /**
     * Constructor including duration calculations.
     * @param size The size of this color block panel.
     * @param clicks Prometheus counter for the number of clicks on a color.  Uses color hex as label.
     * @param duration Prometheus summary for hover duration.  Uses color hex as label.
     */
    public ColorBlock(Dimension size, Counter clicks, Summary duration) {
        super();
        this.setSize(size);

        clicksCounter = clicks;
        hoverDuration = duration;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                clicksCounter.labels(getCurrentColorString()).inc();

                // Darken with click for UX.
                darken();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                recordHoverDuration();

                // Send event to parent object to signal color update.
                getParent().dispatchEvent(e);

                // Color should be updated now, start new timer for duration.
                startHoverTimer();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                startHoverTimer();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                recordHoverDuration();
            }
        });
    }

    /**
     * Darken this box for effect.
     */
    private void darken() {
        setBackground(getBackground().darker());
        repaint();
    }

    private void startHoverTimer() {
        hoverTimer = hoverDuration.labels(getCurrentColorString()).startTimer();
    }

    private void recordHoverDuration() {
        hoverDuration.labels(getCurrentColorString()).observe(hoverTimer.observeDuration());
    }

    /**
     * Get the hex string for the current color of this color block.
     * @return The hex string for the current background color.
     */
    public String getCurrentColorString() {
        Color color = getBackground();
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }
}
