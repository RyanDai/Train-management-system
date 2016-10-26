package railway;

import java.util.*;

/**
 * <p>
 * A mutable class representing the layout of a railway track.
 * </p>
 *
 * <p>
 * A railway track is made up of a number of sections. A junction is on the
 * track if and only if the junction is at one of the end-points of a section in
 * the track.
 * 
 * Each junction on the track has between one and three branches that connect it
 * to sections of the track, and it can have at most one branch of each type.
 * (I.e. a junction may have not have two or more branches of type
 * Branch.FACING.)
 * </p>
 *
 */
public class Track implements Iterable<Section> {

    // Correct line separator for executing machine (used in toString method)
    private final static String LINE_SEPARATOR = System
            .getProperty("line.separator");

    // the sections of the track
    private Set<Section> sections;
    // the end-points of the sections in the track
    private Set<JunctionBranch> endPoints;

    /*
     * invariant:
     * 
     * sections!= null && endPoints!=null && !sections.contains(null) &&
     * 
     * endPoints contains an end-point if and only if it is the end-point of a
     * section in sections &&
     * 
     * for each endPoint in endPoints, there is only one section in sections
     * that has that end-point
     */

    /**
     * Creates a new track with no sections.
     */
    public Track() {
        sections = new HashSet<>();
        endPoints = new HashSet<>();
    }

    /**
     * <p>
     * Adds the given section to the track, unless the addition of the section
     * would result in the track becoming invalid.
     * </p>
     * 
     * <p>
     * If the section is null, then a NullPointerException is thrown and the
     * track is not modified. <br>
     * <br>
     * 
     * If the track already contains an equivalent section, then the track is
     * not modified by this operation.<br>
     * <br>
     * 
     * If the section is not null, and the track does not already contain an
     * equivalent section, but the addition of the section would result in one
     * of the track junctions being connected to more than one section on the
     * same branch, then an InvalidTrackException is thrown, and the track is
     * not modified.<br>
     * <br>
     * 
     * Otherwise, the section is added to the track.
     * </p>
     * 
     * @param section
     *            the section to be added to the track.
     * @throws NullPointerException
     *             if section is null
     * @throws InvalidTrackException
     *             if the track does not already contain an equivalent section,
     *             but it already contains a section that is connected to one of
     *             the same end-points as the given section. (Recall that a
     *             junction can only be connected to one section on a given
     *             branch.) Two end-points are the considered to be the same if
     *             they are equivalent according to the equals method of the
     *             JunctionBranch class.
     */
    public void addSection(Section section) throws NullPointerException,
            IllegalArgumentException {
        if (section == null) {
            throw new NullPointerException(
                    "Cannot add a null section to the track.");
        }

        // do nothing if the track already contains an equivalent section
        if (sections.contains(section)) {
            return;
        }

        // for each end-point (j,b) of the section, check that the junction j
        // isn't already connected to a section on branch b.
        for (JunctionBranch endPoint : section.getEndPoints()) {
            if (endPoints.contains(endPoint)) {
                throw new InvalidTrackException("The junction "
                        + endPoint.getJunction()
                        + " is already connected to a section along branch "
                        + endPoint.getBranch());
            }
        }

        // add the section to the track
        sections.add(section);
        for (JunctionBranch endPoint : section.getEndPoints()) {
            endPoints.add(endPoint);
        }
    }

    /**
     * If the track contains a section that is equivalent to this one, then it
     * is removed from the layout of the railway, otherwise this method does not
     * alter the railway layout in any way.
     * 
     * @param section
     *            the section to be removed from the track
     */
    public void removeSection(Section section) {
        if (section != null && sections.contains(section)) {
            sections.remove(section);
            for (JunctionBranch endPoint : section.getEndPoints()) {
                endPoints.remove(endPoint);
            }
        }
    }

    /**
     * Returns true if the track contains the given section and false otherwise.
     * 
     * @param section
     *            the section whose presence in the track is to be checked
     * @return true iff the track contains a section that is equivalent to the
     *         given parameter.
     */
    public boolean contains(Section section) {
        return sections.contains(section);
    }

    /**
     * Returns a set of all the junctions in the track that are connected to at
     * least one section of the track.
     * 
     * @return The set of junctions in the track.
     */
    public Set<Junction> getJunctions() {
        Set<Junction> junctions = new HashSet<>(); // junctions to return
        for (Section section : sections) {
            for (JunctionBranch endPoint : section.getEndPoints()) {
                junctions.add(endPoint.getJunction());
            }
        }
        return junctions;
    }

    /**
     * If the track contains a section that is connected to the given junction
     * on the given branch, then it returns that section, otherwise it returns
     * null.
     * 
     * @param junction
     *            the junction for which the section will be returned
     * @param branch
     *            the branch of the junction for which the section will be
     *            returned
     * @return the section of track that is connected to the junction on the
     *         given branch, if there is one, otherwise null
     */
    public Section getTrackSection(Junction junction, Branch branch) {
        // the end-point made up of the junction and branch
        JunctionBranch endPoint = new JunctionBranch(junction, branch);
        for (Section section : sections) {
            if (section.getEndPoints().contains(endPoint)) {
                return section;
            }
        }
        return null;
    }

    /**
     * Returns an iterator over the sections in the track. (The iterator can
     * return the sections on the track in any order.)
     */
    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    /**
     * The string representation of a track contains a line-separated
     * concatenation of the string representations of the sections that make up
     * the track. The sections can appear in any order.
     * 
     * The line separator string used to separate the sections should be
     * retrieved in a machine-independent way by calling the function
     * System.getProperty("line.separator").
     */
    @Override
    public String toString() {
        // the string representation under construction
        String result = "";
        for (Section section : sections) {
            if (!result.equals("")) {
                result += LINE_SEPARATOR;
            }
            result = result + section.toString();
        }
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
        // check for null values
        if (sections == null || endPoints == null || sections.contains(null)) {
            return false;
        }

        // the end-points of the sections in this.sections
        Set<JunctionBranch> expectedEndPoints = calculateEndPoints(sections);

        // check that endPoints is the same as expectedEndPoints, and that each
        // end-point belongs to only one section.
        if (!expectedEndPoints.equals(endPoints)
                || endPoints.size() != 2 * sections.size()) {
            return false;
        }

        return true; // otherwise OK
    }

    /**
     * Calculates and returns the set of all of the end-points of the sections
     * in the given set.
     * 
     * @param sections
     *            a set of sections from which the end-points will be derived.
     * @return the set of all end-points of the sections in sections.
     */
    private Set<JunctionBranch> calculateEndPoints(Set<Section> sections) {
        // the end-points of sections under construction
        Set<JunctionBranch> endPoints = new HashSet<>();
        for (Section section : sections) {
            for (JunctionBranch endPoint : section.getEndPoints()) {
                endPoints.add(endPoint);
            }
        }
        return endPoints;
    }
}
