package test.fujitsu.videostore.backend.domain;

/**
 * Movie type
 * According that movie rent price should be calculated
 */
public enum MovieType {

    NEW("New release"),
    REGULAR("Regular rental"),
    OLD("Old film");

    /**
     * Textural representation in database
     */
    private final String textualRepresentation;

    MovieType(String textualRepresentation) {
        //
        //Movie type representation in database
        //
        this.textualRepresentation = textualRepresentation;
    }

    public String getTextualRepresentation() {
        return textualRepresentation;
    }

}
