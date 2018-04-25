package com.shykhmat.vcsstatistics.collector;

import java.time.LocalDate;

import com.shykhmat.vcsstatistics.domain.ProjectReport;

/**
 * Interface for VCS statistic collector classes.
 */
public interface VCSStatisticsCollector {
    /**
     * Method to calculate statistics for a project.
     * 
     * @param projectPath
     *            - location of a project on a hard drive
     * @param dateFrom
     *            - first date in range to be analyzed
     * @param dateTo
     *            - last date in range to be analyzed
     * @return - commit statistics for users for specific date range
     * @throws VCSStatisticsCollectorException
     *             in case of any error occurred during statistics collection
     */
    ProjectReport collectStatistics(String projectPath, LocalDate dateFrom, LocalDate dateTo) throws VCSStatisticsCollectorException;
}
