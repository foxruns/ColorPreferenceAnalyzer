package com.color_preference_analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class holding a list of colors along with utility methods.
 */
public class ColorCollection extends ArrayList<Color> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ColorCollection.class);

    private int numBlocks;

    /**
     * Create a new color collection from the specified file.
     * @param fileName The file containing the color hex codes to be loaded.  Note they should have no preceding #.
     * @param blocks The number of color blocks that will be shown on the UI at a time.
     * @throws IOException Thrown if there was a problem loading the colors file.
     * @throws ConfigurationException Thrown if the number of colors in the file is less than the number of blocks.
     */
    public ColorCollection(String fileName, int blocks) throws IOException, ConfigurationException {
        super();
        numBlocks = blocks;
        loadColors(fileName);
    }

    /**
     * Load colors in from a file containing a list of color hex values (file should have no preceding # on numbers).
     * @param fileName The name of the file containing the list of color hex codes.
     * @throws IOException Thrown if there was a problem loading the colors file.
     * @throws ConfigurationException Thrown if the number of colors in the file is less than the number of blocks.
     */
    private void loadColors(String fileName) throws IOException, ConfigurationException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.add(new Color(
                        Integer.valueOf(line.substring(0, 2), 16),
                        Integer.valueOf(line.substring(2, 4), 16),
                        Integer.valueOf(line.substring(4, 6), 16)));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load colors file.");
            throw e;
        }
        if (this.size() < numBlocks) {
            LOGGER.error("Color source files contains too few colors");
            throw new ConfigurationException("Color source files contains too few colors");
        }
    }

    /**
     * Get a random selection of colors of size <numBlocks>.
     * @return The list of random colors.
     */
    public ArrayList<Color> getBatch() {
        Random rand = new Random();
        ArrayList<Color> selectedColors = new ArrayList<>();

        // Keep generating random colors until we have <numBlocks> number of unique colors.
        while (selectedColors.size() < numBlocks) {
            int colorIndex = rand.nextInt(this.size());
            Color newColor = this.get(colorIndex);
            if (!selectedColors.contains(newColor)) {
                selectedColors.add(newColor);
            }
        }
        return selectedColors;
    }
}
