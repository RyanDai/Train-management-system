package railway;

/**
 * An exception that is thrown to indicate an invalid track configuration.
 */
@SuppressWarnings("serial")
public class InvalidTrackException extends RuntimeException {

    public InvalidTrackException() {
        super();
    }

    public InvalidTrackException(String s) {
        super(s);
    }
}
