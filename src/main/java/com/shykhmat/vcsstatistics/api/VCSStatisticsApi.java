package com.shykhmat.vcsstatistics.api;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shykhmat.vcsstatistics.collector.VCSStatisticsCollector;
import com.shykhmat.vcsstatistics.collector.VCSStatisticsCollectorException;
import com.shykhmat.vcsstatistics.collector.git.GitStatisticsCollector;
import com.shykhmat.vcsstatistics.domain.LinesOfCodeStatusResolver;
import com.shykhmat.vcsstatistics.domain.ProjectReport;
import com.shykhmat.vcsstatistics.domain.VCSType;
import com.shykhmat.vcsstatistics.excel.ExcelWriter;

/**
 * Public API for statistics calculation and report generation functionality.
 */
public class VCSStatisticsApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(VCSStatisticsApi.class);

    private final Map<VCSType, VCSStatisticsCollector> vcsStatisticsCollectors;
    private LinesOfCodeStatusResolver linesOfCodeStatusResolver;
    private ExcelWriter excelWriter;

    public VCSStatisticsApi() {
        vcsStatisticsCollectors = new HashMap<>();
        vcsStatisticsCollectors.put(VCSType.GIT, new GitStatisticsCollector());
        linesOfCodeStatusResolver = new LinesOfCodeStatusResolver();
        excelWriter = new ExcelWriter(linesOfCodeStatusResolver);
    }

    /**
     * Method to calculate statistics for a project.
     * 
     * @param projectPath
     *            - location of a project on a hard drive
     * @param vcsType
     *            - type of repository to be analyzed
     * @param dateFrom
     *            - first date in range to be analyzed
     * @param dateTo
     *            - last date in range to be analyzed
     * @return - commit statistics for users for specific date range
     * @throws VCSStatisticsCollectorException
     *             if any error occurred
     */
    public ProjectReport calculateStatistics(String projectPath, VCSType vcsType, LocalDate dateFrom, LocalDate dateTo) throws VCSStatisticsCollectorException {
        LOGGER.info("Calculating commit statistics for project {}", projectPath);
        VCSStatisticsCollector vcsStatisticsCollector = vcsStatisticsCollectors.get(vcsType);
        if (vcsStatisticsCollectors == null) {
            LOGGER.error("Unsupported Version Control System type {}", vcsType);
        } else {
            return vcsStatisticsCollector.collectStatistics(projectPath, dateFrom, dateTo);
        }
        return null;
    }

    /**
     * Method to calculate metrics for project and write them into Excel report.
     * 
     * @param projectPath
     *            - location of a project to calculate metrics
     * @param reportPath
     *            - location of a report file that will be used to write metrics
     * @param vcsType
     *            - type of repository to be analyzed
     * @param dateFrom
     *            - first date in range to be analyzed
     * @param dateTo
     *            - last date in range to be analyzed
     * @return true if metrics were written successfully, false in another case
     */
    public boolean writeMetricsToExcel(String projectPath, String reportPath, VCSType vcsType, LocalDate dateFrom, LocalDate dateTo) {
        try {
            ProjectReport projectReport = calculateStatistics(projectPath, vcsType, dateFrom, dateTo);
            if (excelWriter.writeMetricsToExcel(reportPath, projectReport)) {
                LOGGER.info("Statistics were written successfully");
                return true;
            }
        } catch (VCSStatisticsCollectorException e) {
            LOGGER.error("Error during statistics collection occurred {}", e);
        }
        return false;
    }

}
