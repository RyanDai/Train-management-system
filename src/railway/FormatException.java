package railway;

/**
 * An exception indicating an invalid file format.
 */
@SuppressWarnings("serial")
public class FormatException extends Exception {

    public FormatException() {
        super();
    }

    public FormatException(String s) {
        super(s);
    }
}
