package railway;

/**
 * <p>
 * An immutable class representing a junction on a railway track.
 * </p>
 * 
 * <p>
 * In the context of a particular instance of a railway track, each junction has
 * between one and three branches that connect it to sections of the track. It
 * can have at most one branch of each type. (I.e. a junction may have not have
 * two branches of type Branch.FACING.)
 * </p>
 */
public class Junction {

    // the identifier of this junction
    private String junctionIdentifier;

    /*
     * invariant: junctionIdentifier != null
     */

    /**
     * Creates a new junction with the given identifier.
     * 
     * @param junctionIdentifier
     *            the identifier of the junction
     * @throws NullPointerException
     *             if junctionIdentifier is null
     */
    public Junction(String junctionIdentifier) throws NullPointerException {
        if (junctionIdentifier == null) {
            throw new NullPointerException(
                    "The parameter junctionIdentifier cannot be null.");
        }
        this.junctionIdentifier = junctionIdentifier;
    }

    /**
     * Returns the identifier of the junction.
     * 
     * @return the junction identifier
     */
    public String getJunctionId() {
        return junctionIdentifier;
    }

    @Override
    public String toString() {
        return junctionIdentifier;
    }

    /**
     * Returns true if and only if the given object is an instance of the class
     * Junction, with an equivalent identifier string to this.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Junction)) {
            return false;
        }
        Junction other = (Junction) object; // the junction to compare
        return (this.junctionIdentifier.equals(other.junctionIdentifier));
    }

    @Override
    public int hashCode() {
        return junctionIdentifier.hashCode();
    }

    /**
     * Determines whether this class is internally consistent (i.e. it satisfies
     * its class invariant).
     * 
     * This method is only intended for testing purposes.
     * 
     * @return true if this class is internally consistent, and false otherwise.
     */
    public boolean checkInvariant() {
        return (junctionIdentifier != null);
    }

}
