package railway;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read a track from a text file.
 */
public class TrackReader {

    /**
     * <p>
     * Reads a text file named fileName that describes the sections on a track,
     * and returns a track containing each of the sections in the file.
     * </p>
     * 
     * <p>
     * The file contains zero or more lines, each of which corresponds to a
     * section on the track.
     * 
     * Each line should contain five items separated by one or more whitespace
     * characters: a positive integer representing the length of the section,
     * followed by the name of a first junction, then the type of a first
     * branch, followed by the name of a second junction, and then the type of a
     * second branch. The section denoted by the line has the given length, and
     * two end-points: one constructed from the first junction and first branch
     * on the line, and the other constructed from the second junction and
     * section branch.
     * 
     * A junction name is simply an unformatted non-empty string that doesn't
     * contain any whitespace characters. The type of a branch is one of the
     * three strings "FACING", "NORMAL" or "REVERSE", which correspond to the
     * branches Branch.FACING, Branch.NORMAL, and Branch.REVERSE, respectively.
     * 
     * There may be leading or trailing whitespace on each line of the file.
     * (Refer to the Character.isWhitespace() method for the definition of a
     * white space in java.)
     * 
     * For example, the line <br>
     * <br>
     * 
     * 10 j1 FACING j2 NORMAL
     * 
     * <br>
     * <br>
     * denotes a section with length 10 and end-points (j1, FACING) and (j2,
     * NORMAL).
     * </p>
     * 
     * <p>
     * No two lines of the file should denote equivalent sections (as defined by
     * the equals method of the Section class), and no two sections described by
     * the input file should have a common end-point (since each junction can
     * only be connected to at most one section on each branch on a valid
     * track).
     * </p>
     * 
     * <p>
     * The method throws an IOException if there is an input error with the
     * input file (e.g. the file with name given by input parameter fileName
     * does not exist); otherwise it throws a FormatException if there is an
     * error with the input format (this includes the case where there is a
     * duplicate section, and the case where two or more sections have a common
     * end-point), otherwise it returns a track that contains each of the
     * sections described in the file (and no others).
     * 
     * If a FormatException is thrown, it will have a meaningful message that
     * accurately describes the problem with the input file format, including
     * the line of the file where the problem was detected.
     * </p>
     * 
     * @param fileName
     *            the file to read from
     * @return a track containing the sections from the file
     * @throws IOException
     *             if there is an error reading from the input file
     * @throws FormatException
     *             if there is an error with the input format. The
     *             FormatExceptions thrown should have a meaningful message that
     *             accurately describes the problem with the input file format,
     *             including the line of the file where the problem was
     *             detected.
     */
    public static Track read(String fileName) throws IOException,
            FormatException {
        // scanner for reading the file a line at a time
        Scanner in = new Scanner(new FileReader(fileName));
        Track track = new Track(); // the track to be returned
        int lineNumber = 1; // the number of the line being read

        try {
            while (in.hasNextLine()) {
                // the section read from the line
                Section section = readSection(lineNumber, in.nextLine());

                // add section to the track unless it is a duplicate, or adding
                // it would cause the track to become invalid
                if (track.contains(section)) {
                    throw new FormatException(errorMessage(lineNumber,
                            "duplicate section detected: " + section));
                }
                try {
                    track.addSection(section);
                } catch (InvalidTrackException e) {
                    throw new FormatException(errorMessage(lineNumber,
                            "cannot add section " + section + " to the track: "
                                    + e.getMessage()));
                }
                lineNumber++;
            }
        } finally {
            in.close();
        }
        return track;
    }

    /**
     * Reads the section from the given line.
     * 
     * @require line != null
     * @ensure Reads the section from the line and returns it.
     * @throws FormaException
     *             if the line is not of the form (e.g. "9 j1 FACING j2 NORMAL")
     *             as described by the TrackReader.read method. The exception
     *             has a message that identifies the lineNumber given, and
     *             describes the nature of the error.
     */
    private static Section readSection(int lineNumber, String line)
            throws FormatException {
        // a scanner for the line
        Scanner lineScanner = new Scanner(line);

        try {
            // the parameters of the section read from the line
            int length = readSectionLength(lineNumber, lineScanner);
            JunctionBranch[] endPoints = new JunctionBranch[2];
            endPoints[0] = readEndPoint(lineNumber, lineScanner);
            endPoints[1] = readEndPoint(lineNumber, lineScanner);

            if (lineScanner.hasNext()) {
                throw new FormatException(errorMessage(lineNumber,
                        "additional information at end of line"));
            }

            if (endPoints[0].equals(endPoints[1])) {
                throw new FormatException(errorMessage(lineNumber,
                        "the end-points of a section must be distinct"));
            }
            return new Section(length, endPoints[0], endPoints[1]);
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
                        "length is less than or equal to zero"));
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
