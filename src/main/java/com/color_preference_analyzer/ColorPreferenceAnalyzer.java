package com.color_preference_analyzer;

import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JFrame;

public final class ColorPreferenceAnalyzer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ColorPreferenceAnalyzer.class);

    // UI attributes.
    private static JFrame masterFrame;
    private static JPanel masterPanel;
    static final Dimension WINDOW_DIMENSION = new Dimension(500, 500);

    // Configuration items.
    static final String COLOR_FILE = "defaultColors.txt";
    // Num Panels should be divisible by 4
    static final int NUM_PANELS = 4;
    static final int PORT = 1234;
    static final boolean RECORD_DURATION = true;

    // Application resources.
    private static ArrayList<ColorBlock> colorBlocks;
    private static ColorCollection colors;

    // Metric items.
    private static Counter clicks;
    private static Summary hoverDuration;

    /**
     * Application entry point.
     * @param args No args expected.
     */
    public static void main(String[] args) {
        // Gather resources and spin up prometheus scrape endpoint server.
        try {
            colors = new ColorCollection(COLOR_FILE, NUM_PANELS);
            configureMetrics();

            HTTPServer server = new HTTPServer(PORT);
        } catch (Exception e) {
            LOGGER.error("Unexpected error:", e);
            System.exit(1);
        }

        // Spin up UI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create UI elements, register listeners, and generate initial colors.
     */
    static void createAndShowGUI() {
        masterFrame = new JFrame();
        masterFrame.setSize(WINDOW_DIMENSION);
        masterFrame.setMinimumSize(WINDOW_DIMENSION);
        masterFrame.setMaximumSize(WINDOW_DIMENSION);
        masterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        masterFrame.setTitle("Click on the color you like best");
        masterFrame.setBackground(Color.white);

        masterPanel = new JPanel();
        masterPanel.setLayout(new GridLayout(NUM_PANELS / 2, NUM_PANELS / 2));
        populateMasterPanel();
        masterPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateColors();
            }
        });

        updateColors();

        masterFrame.getContentPane().add(masterPanel);
        masterFrame.setVisible(true);
    }

    /**
     * Place blocks of color onto master panel.
     */
    private static void populateMasterPanel() {
        colorBlocks = new ArrayList<>();
        Dimension size = new Dimension(WINDOW_DIMENSION.width / 2, WINDOW_DIMENSION.height / 2);

        for (int i = 0; i < NUM_PANELS; i++) {
            ColorBlock block = RECORD_DURATION
                    ? new ColorBlock(size, clicks, hoverDuration) : new ColorBlock(size, clicks);
            colorBlocks.add(block);
            masterPanel.add(block);
        }
    }

    /**
     * Get a new random batch of colors and repaint the UI.
     */
    private static void updateColors() {
        ArrayList<Color> newColors = colors.getBatch();
        for (int i = 0; i < NUM_PANELS; i++) {
            colorBlocks.get(i).setBackground(newColors.get(i));
            colorBlocks.get(i).repaint();
        }
    }

    /**
     * Register metrics.
     */
    private static void configureMetrics() {
         clicks = Counter.build()
                .name("analyzer_total_color_clicks").help("Total clicks for a color.")
                .labelNames("color").register();
         hoverDuration = Summary.build().name("analyzer_hover_duration")
                 .help("How long mouse hovered on a given color.").labelNames("color").register();
    }

    /**
     * Hide default constructor.
     */
    private ColorPreferenceAnalyzer() {
        throw new UnsupportedOperationException("Do not instantiate this utility class.");
    }
}
