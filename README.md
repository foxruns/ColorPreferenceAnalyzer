# Color Preference Analyzer
A simple application for assessing color preference metrics and exposing them with a prometheus exporter.

## Description and Use
This application gathers metrics on color preferences as a user selects a preferred color from a tiling of options.
The idea is that the application could be used by various test subjects, and the data on their collective preferences
be scraped, aggregated, and analyzed.  The application can be packaged with any domain set of colors (loaded from a text file).

Some ideas for potential use cases include:
- Artists who want to create a painting with colors decided on by participant preference.
- Scientists who want to study color preference variations over varying geographical regions.
- Companies that want to test out which colors would best catch the eye of their target demographic.

In addition to retaining the number of clicks each color receives, the application tracks how long the participants
mouse hovered over each color.  Coupled with eye tracking mouse control software this expands the data gathered drastically.

Note that this application is intended for use with bounded color domain sets.

### To build and run
`mvn clean install
mvn exec:java`

Once the application is running, the color selector interface will pop up with simple directions in the window title.

To view prometheus metrics hit the following endpoint:
`http://localhost:1234/metrics`

### Lint and Test
`mvn test`

To just lint and skip tests run `mvn validate`.
