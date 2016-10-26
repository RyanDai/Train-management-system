package railway;

/**
 * An exception that is thrown to indicate an invalid route.
 */
@SuppressWarnings("serial")
public class InvalidRouteException extends RuntimeException {

    public InvalidRouteException() {
        super();
    }

    public InvalidRouteException(String s) {
        super(s);
    }
}
