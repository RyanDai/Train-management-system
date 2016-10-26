package railway;

/**
 * <p>
 * An immutable class corresponding to a part of a train route that is located
 * on one section of track.
 * </p>
 * 
 * <p>
 * A segment is associated with one section of track, and it has a direction of
 * travel along that section, and a first and last location on that section.
 * 
 * The direction of travel is specified with respect to the end-points of the
 * section: one of the section end-points is designated as the departing
 * end-point and the other as the approaching end-point. The direction of travel
 * is away from the departing end-point towards the approaching end-point.
 * 
 * Let startOffset and endOffset be the offset of the segment's first location
 * and last location (respectively) with respect to the departing end-point of
 * the segment. We must have that startOffset < endOffset. For every offset x
 * satisfying startOffset <= x <= endOffset, the segment of track contains every
 * location at distance x from the segment's departing end-point.
 * 
 * The location at distance startOffset from the departing end-point is referred
 * to as the first location on the segment, the location at distance
 * startOffset+1 from the departing end-point is the second location on the
 * segment, etc. The length of the segment is endOffset - startOffset, and it
 * contains (1 + endOffset - startOffset) locations. Since startOffset <
 * endOffset, this length must be greater than or equal to one.
 * 
 * For example, consider a segment that lies on a section of length 9 with
 * end-points (j0, FACING) and (j1,FACING). If (j1,FACING) is the designated
 * departing end-point of the segment, the first location is 3 meters from (j1,
 * FACING) and the last location is 7 meters from (j1, FACING), then the segment
 * denotes a part of a route that starts at 3 meters from (j1, FACING), and
 * travels towards end-point (j0, FACING) until it reaches the location that is
 * 7 meters from (j1, FACING). It contains 4 locations, and is 3 meters long.
 * 
 * It is OK for the startOffset to be zero: in this case the first location is
 * at a junction. It is also OK for the endOffset to be equal to the section's
 * length. In this case, the last location is at a junction.
 * </p>
 */
public class Segment {

    // the section that this segment is part of
    private Section section;
    // the departing end-point of the segment
    private JunctionBranch departingEndPoint;
    // the offset of the first location with respect to departingEndPoint
    private int startOffset;
    // the offset of the last location with respect to the departingEndPoint
    private int endOffset;

    /*
     * invariant:
     * 
     * section != null
     * 
     * && section.getEndPoints().contains(departingEndPoint)
     * 
     * && 0 <= startOffset < endOffset <= section.getLength()
     */

    /**
     * Creates a new segment on the given section that starts at startOffset
     * meters from departingEndPoint and ends endOffset meters from
     * departingEndPoint. The direction of travel is away from the given
     * departingEndPoint towards the other end-point of the section.
     * 
     * @param section
     *            the section that this segment is part of
     * @param departingEndPoint
     *            an end-point of section that is designated as the departing
     *            end-point of the segment
     * @param startOffset
     *            the first location in the segment is located at startOffset
     *            meters from departingEndPoint.getJunction() along
     *            departingEndPoint.getBranch() on the given section
     * @param endOffset
     *            the last location in the segment is located at endOffset
     *            meters from departingEndPoint.getJunction() along
     *            departingEndPoint.getBranch() on the given section
     * @throws NullPointerException
     *             if either parameter section or departingEndPoint is null
     * @throws IllegalArgumentException
     *             if (i) it is not the case that 0 <= startOffSet < endOffset
     *             <= section.getLength() , or (ii) section and
     *             departingEndPoint are not null, but parameter
     *             departingEndPoint is not equivalent to an end-point of the
     *             given section.
     */
    public Segment(Section section, JunctionBranch departingEndPoint,
            int startOffset, int endOffset) throws NullPointerException,
            IllegalArgumentException {
        if (section == null || departingEndPoint == null) {
            throw new NullPointerException("Parameters cannot be null.");
        }
        if (!section.getEndPoints().contains(departingEndPoint)) {
            throw new IllegalArgumentException(
                    "Parameter departingEndPoint must be "
                            + " and end-point of the given section.");
        }
        if (!(0 <= startOffset && startOffset < endOffset && endOffset <= section
                .getLength())) {
            throw new IllegalArgumentException("Offsets " + startOffset
                    + " and " + endOffset
                    + " are not within bounds (i.e. 0 <= startOffSet "
                    + "< endOffset <= section.getLength() does not hold).");
        }

        this.section = section;
        this.departingEndPoint = departingEndPoint;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    /**
     * Returns the section that this segment lies on.
     * 
     * @return the section of this segment.
     */
    public Section getSection() {
        return section;
    }

    /**
     * Returns the departing end-point of this segment.
     * 
     * @return the departing end-point.
     */
    public JunctionBranch getDepartingEndPoint() {
        return departingEndPoint;
    }

    /**
     * Returns the approaching end-point of this segment.
     * 
     * @return the approaching end-point.
     */
    public JunctionBranch getApproachingEndPoint() {
        return section.otherEndPoint(departingEndPoint);
    }

    /**
     * Returns the branch of the departing end-point of this segment.
     * 
     * @return the branch of the departing end-point of this segment.
     */
    public Branch getDepartingBranch() {
        return departingEndPoint.getBranch();
    }

    /**
     * Returns the branch of the approaching end-point of this segment.
     * 
     * @return the branch of the approaching end-point of this segment.
     */
    public Branch getApproachingBranch() {
        return getApproachingEndPoint().getBranch();
    }

    /**
     * Returns the offset of the first location on the segment with respect to
     * the departing branch of the segment.
     * 
     * @return the offset of the first location on the segment w.r.t. the
     *         departing branch of the segment.
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * Returns the offset of the last location on the segment with respect to
     * the departing branch of the segment.
     * 
     * @return the offset of the last location on the segment w.r.t. the
     *         departing branch of the segment.
     */
    public int getEndOffset() {
        return endOffset;
    }

    /**
     * Returns the length of the segment.
     * 
     * @return the length of the segment.
     */
    public int getLength() {
        return (endOffset - startOffset);
    }

    /**
     * Returns the first location on the segment.
     * 
     * @return the first location on the segment.
     */
    public Location getFirstLocation() {
        // note that startOffset < section.length() by the class invariant
        return new Location(section, departingEndPoint, startOffset);
    }

    /**
     * Returns the last location on the segment.
     * 
     * @return the last location on the segment.
     */
    public Location getLastLocation() {
        if (endOffset == section.getLength()) {
            // we must specify the last location relative to the approaching
            // end-point
            return new Location(section, section
                    .otherEndPoint(departingEndPoint), 0);
        } else {
            // endOffset < section.getLength()
            return new Location(section, departingEndPoint, endOffset);
        }
    }

    /**
     * Returns true if this segment contains the given location, and false
     * otherwise.
     * 
     * This segment contains a location l if and only if l.onSection(section)
     * holds and there exists a distance d (in meters) such that startOffset <=
     * d <= endOffset, and l is located d meters from
     * departingEndPoint.getJunction() along the departingEndPoint.getBranch()
     * branch.
     * 
     * @param location
     *            the location to check for containment
     * @return true iff this segment contains the given location
     */
    public boolean contains(Location location) {
        if (location.atAJunction()) {
            /*
             * The location is at a junction.
             * 
             * The only locations on this segment where it could be are the
             * first and last location of the segment.
             */
            return (location.equals(this.getFirstLocation()) || location
                    .equals(this.getLastLocation()));
        } else {
            /*
             * The location is not at a junction.
             * 
             * If it lies on this segment, it must be on the same section, and
             * its offset relative to this.departingEndPoint must be within the
             * segment's bounds.
             */
            if (!section.equals(location.getSection())) {
                return false;
            }
            // the offset of the location with respect to this.departingEndPoint
            int locationOffset = location.getOffset();
            if (!departingEndPoint.equals(location.getEndPoint())) {
                locationOffset = section.getLength() - locationOffset;
            }
            // check that the offset is within bounds
            return this.startOffset <= locationOffset
                    && locationOffset <= this.endOffset;
        }
    }

    @Override
    public String toString() {
        return "[" + startOffset + ", " + endOffset + "] w.r.t. "
                + departingEndPoint + " on section " + section + "";
    }

    /**
     * Returns true if and only if the given object is an instance of the class
     * Segment with an equivalent section, departing end-point, start offset and
     * end offset to this one.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Segment)) {
            return false;
        }
        Segment other = (Segment) object;
        return this.section.equals(other.section)
                && this.departingEndPoint.equals(other.departingEndPoint)
                && this.startOffset == other.startOffset
                && this.endOffset == other.endOffset;
    }

    @Override
    public int hashCode() {
        /*
         * creates a polynomial hash-code based on the section, departing
         * end-point, and start and end offsets.
         */
        final int prime = 31; // an odd base prime
        int result = 1; // the hash code under construction
        result = prime * result + section.hashCode();
        result = prime * result + departingEndPoint.hashCode();
        result = prime * result + startOffset;
        result = prime * result + endOffset;
        return result;
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
        return section != null
                && section.getEndPoints().contains(departingEndPoint)
                && 0 <= startOffset && startOffset < endOffset
                && endOffset <= section.getLength();
    }

}
