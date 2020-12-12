package com.color_preference_analyzer.test;

import com.color_preference_analyzer.ColorBlock;
import com.color_preference_analyzer.ColorCollection;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertThrows;

public class ColorPreferenceAnalyzerTests {

    /**
     * Ensure color strings are retrieved properly.
     */
    @Test
    public void colorBlockRetrieveColorStringThenProperColorIsReturned() {
        ColorBlock block = new ColorBlock(new Dimension(0, 0));
        block.setBackground(Color.white);
        assert (block.getCurrentColorString().equals("#ffffff"));
        block.setBackground(Color.black);
        assert (block.getCurrentColorString().equals("#000000"));
        block.setBackground(Color.red);
        assert (block.getCurrentColorString().equals("#ff0000"));
        block.setBackground(Color.green);
        assert (block.getCurrentColorString().equals("#00ff00"));
        block.setBackground(Color.blue);
        assert (block.getCurrentColorString().equals("#0000ff"));
    }

    /**
     * Ensure the color codes read from a file are all read.
     * @throws IOException Thrown if file fails to load.
     * @throws ConfigurationException Thrown if block size is larger than color list.
     */
    @Test
    public void colorListLoadsThenSizeIsAccurate() throws IOException, ConfigurationException {
        ColorCollection collection = new ColorCollection("defaultColorsShort.txt", 4);
        assert (collection.size() == 25);
    }

    /**
     * Ensure an exception is thrown with the proper message when the block size is greater than the color list.
     */
    @Test
    public void colorListShorterThanBlocksThenExceptionThrownWithProperMessage() {
        Exception e = assertThrows(ConfigurationException.class, () -> {
            ColorCollection collection = new ColorCollection("defaultColorsShort.txt", 32);
        });

        String actualMessage = e.getMessage();
        String expectedMessage = "Color source files contains too few colors";

        assert (actualMessage.equals(expectedMessage));
    }

    /**
     * Ensures that duplicate colors are not returned in color batches.
     * @throws IOException Thrown if file fails to load.
     * @throws ConfigurationException Thrown if block size is larger than color list.
     */
    @Test
    public void batchOfColorsRequestedThenUniqueBatchReturned() throws IOException, ConfigurationException {
        ColorCollection collection = new ColorCollection("defaultColorsShort.txt", 4);
        // Request 100 batches and ensure none contain duplicates.
        for (int i = 0; i < 100; i++) {
            ArrayList<Color> colorBatch = collection.getBatch();
            Set<Color> colorSet = new HashSet<>(colorBatch);
            assert (colorBatch.size() == colorSet.size());
        }
    }
}
