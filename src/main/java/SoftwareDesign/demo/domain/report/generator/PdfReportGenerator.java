package SoftwareDesign.demo.domain.report.generator;

import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeScoreDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class PdfReportGenerator {

    private static final float MARGIN = 44;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);

    private static final Color INK = new Color(31, 41, 55);
    private static final Color MUTED = new Color(107, 114, 128);
    private static final Color BRAND = new Color(37, 99, 235);
    private static final Color BRAND_DARK = new Color(30, 64, 175);
    private static final Color PANEL = new Color(248, 250, 252);
    private static final Color LINE = new Color(209, 213, 219);
    private static final Color TABLE_HEADER = new Color(239, 246, 255);

    public byte[] generateGradeReport(GradeChartResponse report) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDFont font = loadKoreanFont(document);
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;

                y = drawHeader(content, font, report, y);
                y = drawSummaryCards(content, font, report, y - 24);
                y = drawScoreTable(content, font, report, y - 30);
                drawFooter(content, font, page);
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("PDF 보고서 생성에 실패했습니다.", e);
        }
    }

    public byte[] generateConsultationReport(List<ConsultationResponse> consultations) {
        String[][] rows = consultations.stream()
                .map(item -> new String[]{
                        value(item.getStudentName()),
                        value(item.getTeacherName()),
                        value(item.getConsultationDate()),
                        value(item.getNextPlanDate()),
                        value(item.getContent())
                })
                .toArray(String[][]::new);

        return generateListReport(
                "상담 내역 보고서",
                "학생 상담 기록과 후속 상담 일정을 정리한 보고서",
                "상담 건수: " + consultations.size(),
                new String[]{"학생", "교사", "상담일", "다음 상담", "내용"},
                new float[]{82, 82, 78, 78, 182},
                rows
        );
    }

    public byte[] generateFeedbackReport(List<FeedbackResponse> feedbacks) {
        String[][] rows = feedbacks.stream()
                .map(item -> new String[]{
                        value(item.getStudentName()),
                        value(item.getTeacherName()),
                        value(item.getCategory()),
                        item.isVisibleToParent() ? "공개" : "비공개",
                        value(item.getContent())
                })
                .toArray(String[][]::new);

        return generateListReport(
                "피드백 요약 보고서",
                "학생별 피드백 내용을 카테고리와 공개 여부 기준으로 정리한 보고서",
                "피드백 건수: " + feedbacks.size(),
                new String[]{"학생", "교사", "카테고리", "공개", "내용"},
                new float[]{82, 82, 72, 58, 218},
                rows
        );
    }

    private byte[] generateListReport(String title, String subtitle, String summary,
                                      String[] headers, float[] widths, String[][] rows) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDFont font = loadKoreanFont(document);
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;
                y = drawSimpleHeader(content, font, title, subtitle, summary, y);
                y = drawGenericTable(content, font, headers, widths, rows, y - 30);
                drawFooter(content, font, page);
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("PDF 보고서 생성에 실패했습니다.", e);
        }
    }

    private float drawSimpleHeader(PDPageContentStream content, PDFont font,
                                   String title, String subtitle, String summary, float y) throws IOException {
        float height = 96;
        drawFilledRect(content, MARGIN, y - height + 8, CONTENT_WIDTH, height, BRAND);
        drawFilledRect(content, MARGIN, y - height + 8, 8, height, BRAND_DARK);

        writeText(content, font, 22, MARGIN + 24, y - 28, title, Color.WHITE);
        writeText(content, font, 10, MARGIN + 24, y - 52, subtitle, new Color(219, 234, 254));
        writeText(content, font, 11, MARGIN + 24, y - 76, summary, Color.WHITE);

        String issuedAt = "발행일 " + LocalDate.now();
        float issuedWidth = textWidth(font, 10, issuedAt);
        writeText(content, font, 10, MARGIN + CONTENT_WIDTH - issuedWidth - 24, y - 76,
                issuedAt, new Color(219, 234, 254));

        return y - height - 6;
    }

    private float drawGenericTable(PDPageContentStream content, PDFont font,
                                   String[] headers, float[] widths, String[][] rows, float y) throws IOException {
        float rowHeight = 30;

        drawFilledRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, TABLE_HEADER);
        drawStrokedRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, LINE);
        drawTableTexts(content, font, headers, widths, y - 20, 9, BRAND_DARK);
        y -= rowHeight;

        for (int i = 0; i < rows.length; i++) {
            if (i % 2 == 1) {
                drawFilledRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, new Color(249, 250, 251));
            }
            drawStrokedRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, LINE);
            drawTableTexts(content, font, rows[i], widths, y - 20, 8, INK);
            y -= rowHeight;
        }

        return y;
    }

    private float drawHeader(PDPageContentStream content, PDFont font,
                             GradeChartResponse report, float y) throws IOException {
        float height = 96;
        drawFilledRect(content, MARGIN, y - height + 8, CONTENT_WIDTH, height, BRAND);
        drawFilledRect(content, MARGIN, y - height + 8, 8, height, BRAND_DARK);

        writeText(content, font, 22, MARGIN + 24, y - 28, "학생 성적 보고서", Color.WHITE);
        writeText(content, font, 10, MARGIN + 24, y - 52,
                "학기별 성적 요약 및 과목별 성취 분석", new Color(219, 234, 254));
        writeText(content, font, 11, MARGIN + 24, y - 76,
                report.getStudentName() + " · " + report.getSemester(), Color.WHITE);

        String issuedAt = "발행일 " + LocalDate.now();
        float issuedWidth = textWidth(font, 10, issuedAt);
        writeText(content, font, 10, MARGIN + CONTENT_WIDTH - issuedWidth - 24, y - 76,
                issuedAt, new Color(219, 234, 254));

        return y - height - 6;
    }

    private float drawSummaryCards(PDPageContentStream content, PDFont font,
                                   GradeChartResponse report, float y) throws IOException {
        float gap = 10;
        float cardWidth = (CONTENT_WIDTH - (gap * 3)) / 4;
        float cardHeight = 68;

        drawSummaryCard(content, font, MARGIN, y, cardWidth, cardHeight,
                "총점", String.valueOf(report.getTotalScore()));
        drawSummaryCard(content, font, MARGIN + cardWidth + gap, y, cardWidth, cardHeight,
                "평균", formatNumber(report.getAverageScore()));
        drawSummaryCard(content, font, MARGIN + ((cardWidth + gap) * 2), y, cardWidth, cardHeight,
                "전체 등급", report.getOverallGrade());
        drawSummaryCard(content, font, MARGIN + ((cardWidth + gap) * 3), y, cardWidth, cardHeight,
                "과목 수", String.valueOf(report.getScores().size()));

        return y - cardHeight;
    }

    private void drawSummaryCard(PDPageContentStream content, PDFont font,
                                 float x, float y, float width, float height,
                                 String label, String value) throws IOException {
        drawFilledRect(content, x, y - height, width, height, PANEL);
        drawStrokedRect(content, x, y - height, width, height, LINE);
        writeText(content, font, 9, x + 14, y - 20, label, MUTED);
        writeText(content, font, 18, x + 14, y - 46, value, INK);
    }

    private float drawScoreTable(PDPageContentStream content, PDFont font,
                                 GradeChartResponse report, float y) throws IOException {
        writeText(content, font, 14, MARGIN, y, "과목별 성적", INK);
        y -= 24;

        float[] widths = {170, 70, 70, 105, 105};
        String[] headers = {"과목", "점수", "등급", "반 평균", "학년 평균"};
        float rowHeight = 30;

        drawFilledRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, TABLE_HEADER);
        drawStrokedRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, LINE);
        drawTableTexts(content, font, headers, widths, y - 20, 10, BRAND_DARK);
        y -= rowHeight;

        int rowIndex = 0;
        for (GradeScoreDto score : report.getScores()) {
            if (rowIndex % 2 == 1) {
                drawFilledRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, new Color(249, 250, 251));
            }
            drawStrokedRect(content, MARGIN, y - rowHeight, CONTENT_WIDTH, rowHeight, LINE);

            String[] values = {
                    score.getSubjectName(),
                    String.valueOf(score.getScore()),
                    score.getLetterGrade(),
                    formatNumber(score.getClassAverage()),
                    formatNumber(score.getTotalAverage())
            };
            drawTableTexts(content, font, values, widths, y - 20, 10, INK);

            y -= rowHeight;
            rowIndex++;
        }

        return y;
    }

    private void drawTableTexts(PDPageContentStream content, PDFont font,
                                String[] values, float[] widths, float y,
                                int fontSize, Color color) throws IOException {
        float x = MARGIN;
        for (int i = 0; i < values.length; i++) {
            writeText(content, font, fontSize, x + 10, y, values[i], color);
            x += widths[i];
        }
    }

    private void drawFooter(PDPageContentStream content, PDFont font, PDPage page) throws IOException {
        String footer = "SoftwareDesign Student Management System";
        writeText(content, font, 8, MARGIN, MARGIN - 14, footer, MUTED);

        String pageText = "1";
        float pageTextWidth = textWidth(font, 8, pageText);
        writeText(content, font, 8, page.getMediaBox().getWidth() - MARGIN - pageTextWidth,
                MARGIN - 14, pageText, MUTED);
    }

    private PDFont loadKoreanFont(PDDocument document) throws IOException {
        String[] fontPaths = {
                "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
                "/usr/share/fonts/truetype/nanum/NanumGothicBold.ttf",
                "C:\\Windows\\Fonts\\malgun.ttf",
                "C:\\Windows\\Fonts\\gulim.ttc"
        };
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                return PDType0Font.load(document, fontFile);
            }
        }

        throw new IllegalStateException("PDF 생성을 위한 한글 폰트를 찾을 수 없습니다.");
    }

    private void writeText(PDPageContentStream content, PDFont font, int fontSize,
                           float x, float y, String text, Color color) throws IOException {
        content.beginText();
        content.setNonStrokingColor(color);
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text == null ? "" : text);
        content.endText();
    }

    private void drawFilledRect(PDPageContentStream content, float x, float y,
                                float width, float height, Color color) throws IOException {
        content.setNonStrokingColor(color);
        content.addRect(x, y, width, height);
        content.fill();
    }

    private void drawStrokedRect(PDPageContentStream content, float x, float y,
                                 float width, float height, Color color) throws IOException {
        content.setStrokingColor(color);
        content.addRect(x, y, width, height);
        content.stroke();
    }

    private float textWidth(PDFont font, int fontSize, String text) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    private String formatNumber(Double value) {
        if (value == null) {
            return "-";
        }
        return String.format("%.2f", value);
    }

    private String formatNumber(double value) {
        return String.format("%.2f", value);
    }

    private String value(Object value) {
        if (value == null) {
            return "-";
        }

        String text = String.valueOf(value)
                .replace("\r", " ")
                .replace("\n", " ");
        if (text.length() > 34) {
            return text.substring(0, 34) + "...";
        }
        return text;
    }
}
