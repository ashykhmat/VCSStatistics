package com.shykhmat.vcsstatistics.console;

import java.time.LocalDate;
import java.util.Scanner;

import com.shykhmat.vcsstatistics.api.VCSStatisticsApi;
import com.shykhmat.vcsstatistics.domain.VCSType;

/**
 * Console application to collect version control system commit statistics for
 * project and to generate Excel report.
 */
public class VCSStatisticsApplication {
    public static void main(String[] args) {
        VCSStatisticsApi vcsStatisticsApi = new VCSStatisticsApi();
        VCSStatisticsApplicationProperties applicationProperties = new VCSStatisticsApplicationProperties();
        if (applicationProperties.parse(args)) {
            String projectPath = applicationProperties.getProjectPath();
            String reportPath = applicationProperties.getReportPath();
            VCSType vcsType = applicationProperties.getVcsType();
            LocalDate dateFrom = applicationProperties.getDateFrom();
            LocalDate dateTo = applicationProperties.getDateTo();
            vcsStatisticsApi.writeMetricsToExcel(projectPath, reportPath, vcsType, dateFrom, dateTo);
        }
        closeApplication();
    }


    private static void closeApplication() {
        System.out.println("Press \"ENTER\" to exit application...");
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            scanner.nextLine();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
