package railway;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read a route from a text file.
 */
public class RouteReader {

    /**
     * <p>
     * Reads a text file named fileName that describes a route and returns the
     * route read from the file.
     * </p>
     * 
     * <p>
     * The file contains zero or more lines, each of which corresponds to a
     * segment on the route. <br>
     * <br>
     * Each line should contain seven items separated by one or more whitespace
     * characters: a positive integer representing the length of the segment's
     * section, followed by the name of a first junction, then the type of a
     * first branch, followed by the name of a second junction, and then the
     * type of a second branch, then a non-negative integer representing the
     * start-offset of the segment, and a non-negative integer representing the
     * end-offset of the segment. <br>
     * <br>
     * The section of the segment denoted by the line has the given length, and
     * two end-points: one constructed from the first junction and first branch
     * on the line, and the other constructed from the second junction and
     * section branch. The segment's departing end-point is given by the first
     * junction-branch pair on the line, and it's start and end-offset with
     * respect to that end-point are given by the last two non-negative integers
     * on the line. <br>
     * <br>
     * A junction name is simply an unformatted non-empty string that doesn't
     * contain any whitespace characters. The type of a branch is one of the
     * three strings "FACING", "NORMAL" or "REVERSE", which correspond to the
     * branches Branch.FACING, Branch.NORMAL, and Branch.REVERSE, respectively.
     * <br>
     * <br>
     * There may be leading or trailing whitespace on each line of the file. <br>
     * <br>
     * For example, the line <br>
     * <br>
     * 10 j1 FACING j2 NORMAL 0 5 <br>
     * <br>
     * denotes a segment on a section with length 10 and end-points (j1, FACING)
     * and (j2, NORMAL), that has departing end-point (j1, FACING), start-offset
     * 0 and end-offset 5.
     * </p>
     * 
     * <p>
     * The route read should be comprised of the segments, in the order that
     * they appear in the file, and it should be a valid route, comprised of
     * valid segments.
     * </p>
     * 
     * The method throws an IOException if there is an input error with the
     * input file (e.g. the file with name given by input parameter fileName
     * does not exist); otherwise it throws a FormatException if there is an
     * error with the input format (this includes the case the route is not
     * valid or one of the segments read is not valid), otherwise it returns a
     * route with the segments read from the file.
     * 
     * @param fileName
     *            the file to read from
     * @return the route read from the file.
     * @throws NullPointerException
     *             if fileName == null
     * @throws IOException
     *             if there is an error reading from the input file
     * @throws FormatException
     *             if there is an error with the input format.
     */
    public static Route read(String fileName) throws IOException,
            FormatException {
        // scanner for reading the file a line at a time
        Scanner in = new Scanner(new FileReader(fileName));
        // segments on the route to be returned
        List<Segment> segments = new ArrayList<>();
        // the number of the line being read
        int lineNumber = 1;
        try {
            while (in.hasNextLine()) {
                segments.add(readSegment(lineNumber, in.nextLine()));
                lineNumber++;
            }
            return new Route(segments);
        } catch (InvalidRouteException e) {
            throw new FormatException("Invalid Route: " + e.getMessage());
        } finally {
            in.close();
        }
    }

    /**
     * Reads the segment from the given line.
     * 
     * @require line != null
     * @ensure Reads the segment from the line and returns it.
     * @throws FormaException
     *             if the line is not of the form (e.g.
     *             "9 j1 FACING j2 NORMAL 2 5") as described by the
     *             RouteReader.read method. The exception has a message that
     *             identifies the lineNumber given, and describes the nature of
     *             the error.
     */
    private static Segment readSegment(int lineNumber, String line)
            throws FormatException {
        // a scanner for the line
        Scanner lineScanner = new Scanner(line);

        try {
            // the parameters of the section read from the line
            int length = readSectionLength(lineNumber, lineScanner);
            JunctionBranch[] endPoints = new JunctionBranch[2];
            endPoints[0] = readEndPoint(lineNumber, lineScanner);
            endPoints[1] = readEndPoint(lineNumber, lineScanner);
            // the start and end offset of the segment read
            int startOffset = readOffset(lineNumber, lineScanner);
            int endOffset = readOffset(lineNumber, lineScanner);

            if (lineScanner.hasNext()) {
                throw new FormatException(errorMessage(lineNumber,
                        "additional information at end of line"));
            }

            if (endPoints[0].equals(endPoints[1])) {
                throw new FormatException(errorMessage(lineNumber,
                        "the end-points of a section must be distinct"));
            }

            if (!(0 <= startOffset && startOffset < endOffset && endOffset <= length)) {
                throw new FormatException(errorMessage(lineNumber,
                        "the segment start and end offsets are"
                                + " not within bounds."));
            }

            return new Segment(new Section(length, endPoints[0], endPoints[1]),
                    endPoints[0], startOffset, endOffset);
        } finally {
            lineScanner.close();
        }
    }

    /**
     * Reads the section length from the scanner.
     * 
     * @require Parameter lineScanner is not null and it is open for reading.
     * @ensure Consumes the next token on the lineScanner, and returns the
     *         positive integer that it represents.
     * @throws FormatException
     *             If the next token on the lineScanner does not exist, or if it
     *             is not a positive integer. The exception has a message that
     *             identifies the lineNumber given, and describes the nature of
     *             the error.
     */
    private static int readSectionLength(int lineNumber, Scanner lineScanner)
            throws FormatException {
        if (!lineScanner.hasNextInt()) {
            throw new FormatException(errorMessage(lineNumber,
                    "invalid or missing section length"));
        } else {
            int length = lineScanner.nextInt(); // the read section length
            if (length <= 0) {
                throw new FormatException(errorMessage(lineNumber,
                        "section length is less than or equal to zero"));
            }
            return length;
        }
    }

    /**
     * Reads the next end-point from the scanner.
     * 
     * @require Parameter lineScanner is not null and is open for reading.
     * @ensure Consumes the next two tokens on the lineScanner, that represent a
     *         junction and its branch, respectively, and returns the end-point
     *         defined by the pair.
     * @throws FormatException
     *             If the next two tokens on the lineScanner do not exist, or if
     *             they do not represent a junction name followed by a branch
     *             type. The exception has a message that identifies the
     *             lineNumber given, and describes the nature of the error.
     */
    private static JunctionBranch readEndPoint(int lineNumber,
            Scanner lineScanner) throws FormatException {
        try {
            // the junction read from lineScanner
            Junction junction = new Junction(lineScanner.next());
            // the string representation of the branch read from lineScanner
            String branchString = lineScanner.next();
            // the corresponding enumerated type of the branch string
            Branch branch;
            try {
                branch = Branch.valueOf(branchString);
            } catch (IllegalArgumentException e) {
                throw new FormatException(errorMessage(lineNumber,
                        "invalid branch: " + branchString));
            }
            return new JunctionBranch(junction, branch);
        } catch (NoSuchElementException e) {
            // thrown if there are not two tokens on the scanner to consume
            throw new FormatException(errorMessage(lineNumber,
                    "missing or incomplete end-point"));
        }
    }

    /**
     * Reads the offset from the scanner.
     * 
     * @require Parameter lineScanner is not null and it is open for reading.
     * @ensure Consumes the next token on the lineScanner, and returns the
     *         non-negative integer that it represents.
     * @throws FormatException
     *             If the next token on the lineScanner does not exist, or if it
     *             is not a non-negative integer. The exception has a message
     *             that identifies the lineNumber given, and describes the
     *             nature of the error.
     */
    private static int readOffset(int lineNumber, Scanner lineScanner)
            throws FormatException {
        if (!lineScanner.hasNextInt()) {
            throw new FormatException(errorMessage(lineNumber,
                    "invalid or missing offset"));
        } else {
            int offset = lineScanner.nextInt(); // the read offset
            if (offset < 0) {
                throw new FormatException(errorMessage(lineNumber, "offset "
                        + offset + " is less than  zero"));
            }
            return offset;
        }
    }

    /**
     * Returns an error message for a FormatException that contains the given
     * lineNumber and message.
     * 
     * @require message != null
     * @ensure Returns an error message for line lineNumber of the file that
     *         contains the given message.
     */
    private static String errorMessage(int lineNumber, String message) {
        return "Error on line " + lineNumber + ": " + message;
    }
}
