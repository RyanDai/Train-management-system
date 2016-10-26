package railway;

import java.util.*;

/**
 * <p>
 * An immutable class corresponding to a train route.
 * </p>
 */
public class Route implements Iterable<Segment> {

    // the segments that make up the route
    private List<Segment> segments;

    /*
     * invariant:
     * 
     * segments != null &&
     * 
     * !segments.contains(null) &&
     * 
     * for each segment on the route that is not the last, the last location on
     * that segment equals the first location of the next segment on the route,
     * and that location is at a junction and the direction of travel through
     * that junction is possible.
     */

    /**
     * Creates a new route with the given segments.
     * 
     * @param segments
     *            the segments that are on this route.
     * @throws NullPointerException
     *             if segments is null or segments.contains(null)
     * @throws InvalidRouteException
     *             if this route is not valid. The route is valid when (i) for
     *             each segment on the route that is not the last, the last
     *             location on that segment equals the first location of the
     *             next segment on the route, and that location is at a junction
     *             and (ii) the direction of travel through that junction is
     *             possible.
     */
    public Route(List<Segment> segments) {
        // Check for null values.
        if (segments == null || segments.contains(null)) {
            throw new NullPointerException(
                    "segments cannot be null or contain null segments");
        }
        // Check that the route described by segments is valid.
        for (int i = 0; i < segments.size() - 1; i++) {
            // the adjacent segments at index i and i+1
            Segment previous = segments.get(i);
            Segment next = segments.get(i + 1);
            // the last location of the previous segment
            Location pivot = previous.getLastLocation();

            // check pivot connects the adjacent segments at a junction
            if (!pivot.equals(next.getFirstLocation()) 
                    || !pivot.atAJunction()) {
                throw new InvalidRouteException("The segment (" + previous
                        + ") is not connected to the next segment (" + next
                        + ") at a junction.");
            }
            // check that the direction of travel through that junction is OK
            Branch approach = previous.getApproachingBranch();
            Branch departure = next.getDepartingBranch();
            if ((approach == Branch.FACING && departure == Branch.FACING)
                || (approach != Branch.FACING && departure != Branch.FACING)) {
                throw new InvalidRouteException(
                        "The direction of travel from segment (" + previous
                                + ") to segment (" + next
                                + ") is not possible.");
            }
        }
        this.segments = new ArrayList<>(segments);
    }

    /**
     * Returns the length of the route (i.e. the sum of the length of the
     * individual segments in the route).
     * 
     * @return the length of the route.
     */
    public int getLength() {
        int result = 0;
        for (Segment segment : segments) {
            result += segment.getLength();
        }
        return result;
    }

    /**
     * Returns true if and only if all the segments of this route are on the
     * given track.
     * 
     * @param track
     *            the track that the route will be checked with respect to.
     * @throws NullPointerException
     *             if track is null
     * @return true if all the segments of this route are on the given track,
     *         and false otherwise.
     */
    public boolean onTrack(Track track) {
        if (track == null) {
            throw new NullPointerException("Parameter cannot be null");
        }
        for (Segment segment : segments) {
            if (!track.contains(segment.getSection())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the sub-route of this route that starts at startOffset meters
     * into this route and ends endOffset meters into this route.
     * 
     * @param startOffset
     *            the start offset of the sub-route to return
     * @param endOffset
     *            the end offset of the sub-route to return
     * @throws IllegalArgumentException
     *             if !(0 <= startOffset < endOffset <= this.getLength())
     * 
     * @return the sub-route of this route that starts at startOffset meters
     *         into this route and ends endOffset meters into this route.
     */
    public Route getSubroute(int startOffset, int endOffset) {
        if (!(0 <= startOffset && startOffset < endOffset && endOffset <= this
                .getLength())) {
            throw new IllegalArgumentException("Subroute is out of bounds.");
        }
        // the sub-segments that will make up the sub-route
        List<Segment> subsegments = new ArrayList<>();
        // variable offset is the distance along the route to the start of the
        // segment currently being examined
        int offset = 0;
        for (Segment segment : segments) {
            if (startOffset - offset < segment.getLength()
                    && 0 < endOffset - offset) {
                // part of the segment belongs on the sub-route
                int subsegmentStartOffset =
                        segment.getStartOffset()
                                + Math.max(startOffset - offset, 0);
                int subsegmentLength =
                        (Math.min(endOffset - offset, segment.getLength()) - 
                                Math.max(startOffset - offset, 0));
                subsegments.add(new Segment(segment.getSection(), segment
                        .getDepartingEndPoint(), subsegmentStartOffset,
                        subsegmentStartOffset + subsegmentLength));
            }
            offset += segment.getLength();
        }
        return new Route(subsegments);
    }

    /**
     * Returns true if and only if the route given intersects with this route.
     * 
     * @param other
     *            the route to check for intersection
     * @throws NullPointerException
     *             if other is null
     * @return true if there is a location in other that is also on this route,
     *         and false otherwise.
     */
    public boolean intersects(Route other) {
        if (other == null) {
            throw new NullPointerException("Parameter cannot be null.");
        }
        Route longestDisjointPrefix = longestDisjointPrefix(this, other);
        return (!this.equals(longestDisjointPrefix));
    }

    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }

    /**
     * The string representation of a route contains a line-separated
     * concatenation of the string representations of the segments that make up
     * the route.
     */
    @Override
    public String toString() {
        // the string representation to be constructed
        String result = "";
        for (Segment segment : segments) {
            if (!result.equals("")) {
                result += System.getProperty("line.separator");
            }
            result = result + segment.toString();
        }
        return result;
    }

    /**
     * Returns true if and only if the given object is an instance of the class
     * Route that has an equivalent list of segments to this route.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        return this.segments.equals(other.segments);
    }

    @Override
    public int hashCode() {
        return segments.hashCode();
    }

    /**
     * Returns the longest prefix of routeA that doesn't intersect with routeB.
     * 
     * @throws NullPointerException
     *             if routeA == null or routeB == null
     * @ensure returns the longest prefix of routeA that doesn't overlap with
     *         any of the locations in routeB.
     */
    private static Route longestDisjointPrefix(Route routeA, Route routeB) {
        if (routeA == null || routeB == null) {
            throw new NullPointerException("Parameters cannot be null");
        }

        // the segments of the longest disjoint prefix of routeA
        List<Segment> segments = new ArrayList<Segment>();

        for (Segment segmentA : routeA) {
            // calculate the longest disjoint prefix of segmentA and routeB
            Segment prefix = segmentA;
            for (Segment segmentB : routeB) {
                prefix = longestDisjointPrefix(prefix, segmentB);
                if (prefix == null) {
                    // only the empty prefix of segmentA doesn't intersect: the
                    // result calculated so far is the longest disjoint prefix
                    return new Route(segments);
                }
            }
            segments.add(prefix);
            if (!segmentA.equals(prefix)) {
                // segmentA does intersect with routeB: the
                // result calculated so far is the longest disjoint prefix
                return new Route(segments);
            }
        }
        return new Route(segments);
    }

    /**
     * Returns the longest prefix of segmentA that doesn't intersect with
     * segmentB. The value null is returned if such a segment would be empty or
     * have length 0.
     * 
     * @throws NullPointerException
     *             if segmentA == null or segmentB == null
     * @return returns the longest prefix of segmentA that doesn't overlap with
     *         any of the locations in segmentB. The value null is returned if
     *         such a segment would be empty or have length 0.
     */
    private static Segment longestDisjointPrefix(Segment segmentA,
            Segment segmentB) {

        if (segmentA == null || segmentB == null) {
            throw new NullPointerException("Parameters cannot be null");
        }

        if (!segmentA.getSection().equals(segmentB.getSection())) {
            // (i) They are not on the same section.
            // Only the first or last locations might overlap in this case.

            if (segmentB.contains(segmentA.getFirstLocation())) {
                // the longest disjoint prefix is empty
                return null;
            } else if (segmentB.contains(segmentA.getLastLocation())) {
                // the prefix has all but the last location of segmentA
                if (segmentA.getLength() == 1) {
                    return null; // the longest disjoint prefix has length 0
                } else {
                    return new Segment(segmentA.getSection(), segmentA
                            .getDepartingEndPoint(), segmentA.getStartOffset(),
                            segmentA.getEndOffset() - 1);
                }
            } else {
                // segmentA and segmentB do not intersect
                return segmentA;
            }
        } else {
            // (ii) They are on the same section.
            // We first invert segmentB's direction if necessary so that its
            // interval on the section can be readily compared to segmentA's.
            if (!segmentB.getDepartingEndPoint().equals(
                    segmentA.getDepartingEndPoint())) {
                segmentB = invertDirection(segmentB);
            }

            if (segmentA.getEndOffset() < segmentB.getStartOffset()
                    || segmentB.getEndOffset() < segmentA.getStartOffset()) {
                // the intervals do not overlap
                return segmentA;
            } else {
                // the intervals do overlap
                if (segmentB.getStartOffset() <= segmentA.getStartOffset()) {
                    // the first location in segmentA is also in segmentB.
                    return null; // the longest disjoint prefix is empty
                } else {
                    // the first location of segmentA is not in segmentB.
                    if (segmentB.getStartOffset() - 1
                            - segmentA.getStartOffset() < 1) {
                        return null; // the longest disjoint prefix has length 0
                    } else {
                        return new Segment(segmentA.getSection(), segmentA
                                .getDepartingEndPoint(), segmentA
                                .getStartOffset(),
                                segmentB.getStartOffset() - 1);
                    }
                }
            }
        }
    }

    /**
     * Returns a segment that occupies the same locations as the given segment,
     * but whose direction of travel has been reversed.
     * 
     * @throws NullPointerException
     *             if segment == null
     * @return returns the inverted segment
     */
    private static Segment invertDirection(Segment segment) {
        // the parameters of the inverted section
        Section section = segment.getSection();
        JunctionBranch endPoint =
                section.otherEndPoint(segment.getDepartingEndPoint());
        int startOffset = section.getLength() - segment.getEndOffset();
        int endOffset = section.getLength() - segment.getStartOffset();
        return new Segment(section, endPoint, startOffset, endOffset);
    }

}
