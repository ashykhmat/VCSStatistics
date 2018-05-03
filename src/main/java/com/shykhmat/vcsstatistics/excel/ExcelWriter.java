package com.shykhmat.vcsstatistics.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.shykhmat.vcsstatistics.domain.LinesOfCodeStatusResolver;
import com.shykhmat.vcsstatistics.domain.ProjectReport;
import com.shykhmat.vcsstatistics.domain.Status;
import com.shykhmat.vcsstatistics.utils.DateUtils;

/**
 * Class to write statistics report into Excel file.
 */
public class ExcelWriter {
    private static final String STATISTICS_SHEET_NAME = "Statistics";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);
    private static final String EXCEL_EXTENSION = ".xlsx";
    private LinesOfCodeStatusResolver linesOfCodeStatusResolver;

    public ExcelWriter(LinesOfCodeStatusResolver linesOfCodeStatusResolver) {
        this.linesOfCodeStatusResolver = linesOfCodeStatusResolver;
    }

    /**
     * Method to write calculated {@link ProjectReport} into Excel file.
     * 
     * @param pathToFile
     *            - path to Excel file that will be created
     * @param projectReport
     *            - report with project commit statistics
     * @return true if metrics were written successfully, false in another case
     */
    public boolean writeMetricsToExcel(String pathToFile, ProjectReport projectReport) {
        try (Workbook workbook = writeToWorkbook(projectReport)) {
            writeToFile(fixReportPath(projectReport, pathToFile), workbook);
        } catch (IOException e) {
            LOGGER.error("Error during writing statistics to Excel file: ", e);
            return false;
        } 
        return true;
    }

    private Workbook writeToWorkbook(ProjectReport projectReport) {
        Workbook workbook = new SXSSFWorkbook();
        Map<Status, CellStyle> statusCellStyles = prepareStatusCellStyles(workbook);
        writeStatisticsMetrics(projectReport, workbook, statusCellStyles);
        return workbook;
    }

    private Map<Status, CellStyle> prepareStatusCellStyles(Workbook workbook) {
        CellStyle okCellStyle = workbook.createCellStyle();
        okCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        okCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle warningCellStyle = workbook.createCellStyle();
        warningCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        warningCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle errorCellStyle = workbook.createCellStyle();
        errorCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Map<Status, CellStyle> statusCellStyles = new HashMap<>();
        statusCellStyles.put(Status.OK, okCellStyle);
        statusCellStyles.put(Status.WARNING, warningCellStyle);
        statusCellStyles.put(Status.ERROR, errorCellStyle);
        return statusCellStyles;
    }

    private String fixReportPath(ProjectReport projectReport, String reportPath) {
        if (!reportPath.endsWith(EXCEL_EXTENSION)) {
            String projectName = projectReport.getProjectName();
            if (!reportPath.endsWith(File.separator)) {
                projectName = File.separator + projectName;
            }
            reportPath += projectName + EXCEL_EXTENSION;
        }
        return reportPath;
    }

    private void writeToFile(String pathToFile, Workbook workbook) throws IOException {
        LOGGER.info("Writing report file {}", pathToFile);
        OutputStream fileOutStream = null;
        File fileOut = new File(pathToFile);
        Files.createParentDirs(fileOut);
        Files.touch(fileOut);
        try {
            fileOutStream = new FileOutputStream(pathToFile);
            workbook.write(fileOutStream);
        } finally {
            if (fileOutStream != null) {
                fileOutStream.close();
            }
        }
    }

    private void writeStatisticsMetrics(ProjectReport projectReport, Workbook workbook, Map<Status, CellStyle> statusCellStyles) {
        Sheet worksheet = createStatisticsSheet(workbook);
        List<LocalDate> datesInStatistics = DateUtils.getDatesBetweenUsing(projectReport.getDateFrom(), projectReport.getDateTo());
        createStatisticsHeader(worksheet, datesInStatistics);
        AtomicInteger rowIndex = new AtomicInteger();
        projectReport.getUserReport().entrySet().stream().forEach(entry -> {
            Row row = worksheet.createRow(rowIndex.incrementAndGet());
            AtomicInteger columnIndex = new AtomicInteger();
            row.createCell(columnIndex.getAndIncrement()).setCellValue(entry.getKey());
            Map<LocalDate, Long> userStatistics = entry.getValue();
            datesInStatistics.stream().forEach(date -> {
                Long linesOfCode = 0l;
                if (userStatistics.containsKey(date)) {
                    linesOfCode = userStatistics.get(date);
                }
                Cell locCell = row.createCell(columnIndex.getAndIncrement());
                locCell.setCellStyle(statusCellStyles.get(linesOfCodeStatusResolver.getStatus(linesOfCode)));
                locCell.setCellValue(linesOfCode);
            });
            row.createCell(columnIndex.getAndIncrement()).setCellValue(userStatistics.values().stream().mapToLong(Long::longValue).sum());
        });
    }

    private void createStatisticsHeader(Sheet worksheet, List<LocalDate> datesInStatistics) {
        Row header = worksheet.createRow(0);
        header.createCell(0).setCellValue("Author");
        AtomicInteger currentColumnIndex = new AtomicInteger();
        datesInStatistics.stream().forEach(d -> {
            header.createCell(currentColumnIndex.incrementAndGet()).setCellValue(d.toString());
        });
        header.createCell(currentColumnIndex.incrementAndGet()).setCellValue("Total");

    }

    private Sheet createStatisticsSheet(Workbook workbook) {
        int classesSheetIndex = workbook.getSheetIndex(STATISTICS_SHEET_NAME);
        if (classesSheetIndex >= 0) {
            workbook.removeSheetAt(classesSheetIndex);
        }
        return workbook.createSheet(STATISTICS_SHEET_NAME);
    }

}
