package com.shykhmat.vcsstatistics.domain;

/**
 * Class to calculate status for committed lines of code.
 */
public class LinesOfCodeStatusResolver {
    /**
     * Method to retrieve {@link Status} for specified lines of code.
     * 
     * @param linesOfCode
     *            - lines of code number to be analyzed
     * @return - {@link Status} for specified lines of code
     */
    public Status getStatus(Long linesOfCode) {
        if (linesOfCode == 0) {
            return Status.ERROR;
        } else if (linesOfCode > 0 && linesOfCode < 50) {
            return Status.WARNING;
        }
        return Status.OK;
    }
}
