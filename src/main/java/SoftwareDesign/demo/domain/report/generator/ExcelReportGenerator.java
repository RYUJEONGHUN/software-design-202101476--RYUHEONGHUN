package SoftwareDesign.demo.domain.report.generator;

import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeScoreDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelReportGenerator {

    public byte[] generateGradeReport(GradeChartResponse report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("grade-report");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("학생 성적 보고서");
            titleCell.setCellStyle(titleStyle);

            createSummaryRow(sheet, 2, "학생명", report.getStudentName());
            createSummaryRow(sheet, 3, "학기", report.getSemester());
            createSummaryRow(sheet, 4, "총점", report.getTotalScore());
            createSummaryRow(sheet, 5, "평균", report.getAverageScore());
            createSummaryRow(sheet, 6, "전체 등급", report.getOverallGrade());

            Row header = sheet.createRow(8);
            String[] headers = {"과목", "점수", "등급", "반 평균", "학년 평균"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 9;
            for (GradeScoreDto score : report.getScores()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(score.getSubjectName());
                row.createCell(1).setCellValue(score.getScore());
                row.createCell(2).setCellValue(score.getLetterGrade());
                row.createCell(3).setCellValue(score.getClassAverage());
                row.createCell(4).setCellValue(score.getTotalAverage());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Excel 보고서 생성에 실패했습니다.", e);
        }
    }

    public byte[] generateConsultationReport(List<ConsultationResponse> consultations) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("consultation-report");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("상담 내역 보고서");
            titleCell.setCellStyle(titleStyle);

            createSummaryRow(sheet, 2, "상담 건수", consultations.size());

            Row header = sheet.createRow(4);
            String[] headers = {"학생명", "학번", "상담 교사", "상담일", "다음 상담일", "상담 내용"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 5;
            for (ConsultationResponse item : consultations) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getStudentName());
                row.createCell(1).setCellValue(item.getStudentNumber());
                row.createCell(2).setCellValue(item.getTeacherName());
                row.createCell(3).setCellValue(String.valueOf(item.getConsultationDate()));
                row.createCell(4).setCellValue(String.valueOf(item.getNextPlanDate()));
                row.createCell(5).setCellValue(item.getContent());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Excel 상담 보고서 생성에 실패했습니다.", e);
        }
    }

    public byte[] generateFeedbackReport(List<FeedbackResponse> feedbacks) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("feedback-report");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("피드백 요약 보고서");
            titleCell.setCellStyle(titleStyle);

            createSummaryRow(sheet, 2, "피드백 건수", feedbacks.size());

            Row header = sheet.createRow(4);
            String[] headers = {"학생명", "작성 교사", "카테고리", "학부모 공개", "작성일", "내용"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 5;
            for (FeedbackResponse item : feedbacks) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getStudentName());
                row.createCell(1).setCellValue(item.getTeacherName());
                row.createCell(2).setCellValue(String.valueOf(item.getCategory()));
                row.createCell(3).setCellValue(item.isVisibleToParent() ? "Y" : "N");
                row.createCell(4).setCellValue(String.valueOf(item.getCreatedAt()));
                row.createCell(5).setCellValue(item.getContent());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Excel 피드백 보고서 생성에 실패했습니다.", e);
        }
    }

    private void createSummaryRow(Sheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void createSummaryRow(Sheet sheet, int rowIndex, String label, int value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void createSummaryRow(Sheet sheet, int rowIndex, String label, double value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
}
