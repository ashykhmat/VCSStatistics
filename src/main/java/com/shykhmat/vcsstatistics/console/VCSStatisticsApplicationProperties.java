package com.shykhmat.vcsstatistics.console;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shykhmat.vcsstatistics.domain.VCSType;

/**
 * Command line arguments that application can process.
 */
public class VCSStatisticsApplicationProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(VCSStatisticsApplicationProperties.class);
    private static final String PROJECT_PATH_OPTION = "projectPath";
    private static final String REPORT_PATH_OPTION = "reportPath";
    private static final String DATE_FROM_OPTION = "dateFrom";
    private static final String DATE_TO_OPTION = "dateTo";
    private static final String VST_TYPE_OPTION = "vcs";
    private static final String HELP_OPTION = "help";

    private Options statisticsCalculationOptions;
    private Options additionalOptions;

    private String projectPath;
    private String reportPath;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private VCSType vcsType;

    public VCSStatisticsApplicationProperties() {
        statisticsCalculationOptions = new Options();
        Option projectPathOption = new Option(PROJECT_PATH_OPTION, true, "Required. Specifies path to the folder, that contains application to analyze");
        projectPathOption.setRequired(true);
        Option reportPathOption = new Option(REPORT_PATH_OPTION, true, "Required. Specifies path to the folder, that will be used to store generated Excel report");
        reportPathOption.setRequired(true);
        Option vcsTypeOption = new Option(VST_TYPE_OPTION, true, "Required. Specifies repository type to be analyzed. Supported types: " + Stream.of(VCSType.values()).map(Enum::name).collect(Collectors.toList()));
        vcsTypeOption.setRequired(true);
        statisticsCalculationOptions.addOption(projectPathOption);
        statisticsCalculationOptions.addOption(reportPathOption);
        statisticsCalculationOptions.addOption(vcsTypeOption);
        statisticsCalculationOptions.addOption(DATE_FROM_OPTION, true, "Specifies date from which statistic will be calculated");
        statisticsCalculationOptions.addOption(new Option(DATE_TO_OPTION, true, "Specifies date to which statistic will be calculated"));
        additionalOptions = new Options();
        additionalOptions.addOption(new Option(HELP_OPTION, false, "Command to see application help information"));
    }

    /**
     * Method to collect application properties from command line arguments
     * 
     * @param args
     *            - specified command line arguments
     * @return true if arguments were parsed successfully, false in another case
     */
    public boolean parse(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(additionalOptions, args, true);
            if (line.hasOption(HELP_OPTION)) {
                printHelp();
                return false;
            }
            line = parser.parse(statisticsCalculationOptions, args);
            projectPath = line.getOptionValue(PROJECT_PATH_OPTION);
            reportPath = line.getOptionValue(REPORT_PATH_OPTION);
            vcsType = VCSType.valueOf(line.getOptionValue(VST_TYPE_OPTION).toUpperCase());
            if (line.hasOption(DATE_FROM_OPTION)) {
                dateFrom = LocalDate.parse(line.getOptionValue(DATE_FROM_OPTION));
            }
            if (line.hasOption(DATE_TO_OPTION)) {
                dateTo = LocalDate.parse(line.getOptionValue(DATE_TO_OPTION));
            }
            return true;
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
            return false;
        }

    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public VCSType getVcsType() {
        return vcsType;
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        Options helpOptions = new Options();
        for (Option option : statisticsCalculationOptions.getOptions()) {
            helpOptions.addOption(option);
        }
        for (Option option : additionalOptions.getOptions()) {
            helpOptions.addOption(option);
        }
        formatter.printHelp("Version Control System Commit Statistics Application", "Read following instructions to work with the application", helpOptions, "Developed by Anton Shykhmat");
    }
}
