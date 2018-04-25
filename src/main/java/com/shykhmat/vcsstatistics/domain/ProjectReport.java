package com.shykhmat.vcsstatistics.domain;

import java.time.LocalDate;
import java.util.Map;

/**
 * Class that contains project statistics report.
 */
public class ProjectReport {
    private String projectName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, Map<LocalDate, Long>> userReport;

    public ProjectReport(String projectName, LocalDate dateFrom, LocalDate dateTo, Map<String, Map<LocalDate, Long>> userReport) {
        this.projectName = projectName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.userReport = userReport;
    }

    public String getProjectName() {
        return projectName;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Map<String, Map<LocalDate, Long>> getUserReport() {
        return userReport;
    }

}
