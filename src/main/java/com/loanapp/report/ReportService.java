package com.loanapp.report;

import com.loanapp.account.LoanAccountRepository;
import com.loanapp.emi.EmiRepository;
import com.loanapp.entity.*;
import com.loanapp.enums.LoanStatus;
import com.loanapp.enums.PaidStatus;
import com.loanapp.exception.ResourceNotFoundException;
import com.loanapp.penalty.PenaltyRepository;
import com.loanapp.repayment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final LoanAccountRepository loanAccountRepository;
    private final EmiRepository emiRepository;
    private final PaymentRepository paymentRepository;
    private final PenaltyRepository penaltyRepository;

    // ---- Customer: Loan Statement PDF ----
    public byte[] generateCustomerStatementPdf(Long loanAccountId) {
        LoanAccount account = loanAccountRepository.findById(loanAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan account not found"));
        List<EmiSchedule> schedule = emiRepository.findByLoanAccountIdOrderByInstallmentNo(loanAccountId);
        List<Payment> payments = paymentRepository.findByLoanAccountId(loanAccountId);

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("LOAN_ACCOUNT_NO", account.getLoanAccountNumber());
            params.put("CUSTOMER_NAME", account.getApplication().getUser().getFullName());
            params.put("APPROVED_AMOUNT", account.getApprovedAmount().toString());
            params.put("OUTSTANDING", account.getOutstandingBalance().toString());
            params.put("INTEREST_RATE", account.getInterestRate().toString());
            params.put("TENURE", account.getTenureMonths().toString());
            params.put("DISBURSED_DATE", account.getDisbursedDate().toString());
            params.put("REPORT_DATE", LocalDate.now().toString());
            params.put("EMI_DATA", schedule);
            params.put("PAYMENT_DATA", payments);

            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(schedule);
            JasperReport jasperReport = loadReport("loan_statement");
            JasperPrint print = JasperFillManager.fillReport(jasperReport, params, ds);
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            log.error("PDF generation error: {}", e.getMessage());
            return generateSimplePdfFallback("Loan Statement - " + account.getLoanAccountNumber(), schedule, payments);
        }
    }

    // ---- Customer: EMI Schedule PDF ----
    public byte[] generateEmiSchedulePdf(Long loanAccountId) {
        List<EmiSchedule> schedule = emiRepository.findByLoanAccountIdOrderByInstallmentNo(loanAccountId);
        return generateSimplePdfFallback("EMI Schedule - Account #" + loanAccountId, schedule, List.of());
    }

    // ---- Admin: Overdue Loans Excel ----
    public byte[] generateOverdueLoansExcel() {
        List<LoanAccount> overdueAccounts = loanAccountRepository.findAll().stream()
                .filter(a -> a.getLoanStatus() == LoanStatus.ACTIVE)
                .filter(a -> {
                    long overdueCount = emiRepository.findByLoanAccountIdOrderByInstallmentNo(a.getId())
                            .stream().filter(e -> e.getPaidStatus() == PaidStatus.OVERDUE).count();
                    return overdueCount > 0;
                }).toList();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Overdue Loans");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Headers
            Row header = sheet.createRow(0);
            String[] cols = {"Account No", "Customer Name", "Approved Amount",
                    "Outstanding Balance", "Overdue EMIs", "Disbursed Date", "Status"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 1;
            for (LoanAccount account : overdueAccounts) {
                Row row = sheet.createRow(rowNum++);
                long overdueEmis = emiRepository.findByLoanAccountIdOrderByInstallmentNo(account.getId())
                        .stream().filter(e -> e.getPaidStatus() == PaidStatus.OVERDUE).count();
                row.createCell(0).setCellValue(account.getLoanAccountNumber());
                row.createCell(1).setCellValue(account.getApplication().getUser().getFullName());
                row.createCell(2).setCellValue(account.getApprovedAmount().doubleValue());
                row.createCell(3).setCellValue(account.getOutstandingBalance().doubleValue());
                row.createCell(4).setCellValue(overdueEmis);
                row.createCell(5).setCellValue(account.getDisbursedDate().toString());
                row.createCell(6).setCellValue(account.getLoanStatus().name());
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Excel generation error: {}", e.getMessage());
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    // ---- Admin: Monthly Collections Excel ----
    public byte[] generateMonthlyCollectionsExcel(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        List<Payment> payments = paymentRepository.findByDateRange(start, end);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Monthly Collections " + year + "-" + month);
            Row header = sheet.createRow(0);
            String[] cols = {"Payment Ref", "Customer Name", "Account No", "Amount", "Mode", "Date", "Status"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

            int rowNum = 1;
            double total = 0;
            for (Payment p : payments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getPaymentReference());
                row.createCell(1).setCellValue(p.getLoanAccount().getApplication().getUser().getFullName());
                row.createCell(2).setCellValue(p.getLoanAccount().getLoanAccountNumber());
                row.createCell(3).setCellValue(p.getAmountPaid().doubleValue());
                row.createCell(4).setCellValue(p.getPaymentMode().name());
                row.createCell(5).setCellValue(p.getPaidAt().toString());
                row.createCell(6).setCellValue(p.getPaymentStatus().name());
                total += p.getAmountPaid().doubleValue();
            }

            // Total row
            Row totalRow = sheet.createRow(rowNum + 1);
            totalRow.createCell(2).setCellValue("TOTAL");
            totalRow.createCell(3).setCellValue(total);

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate collections report", e);
        }
    }

    // ---- Admin: Pending Approvals PDF ----
    public byte[] generatePendingApprovalsPdf(List<?> pendingApplications) {
        return generateSimplePdfFallback("Pending Loan Approvals", pendingApplications, List.of());
    }

    private JasperReport loadReport(String reportName) throws Exception {
        String path = "/reports/" + reportName + ".jrxml";
        var stream = getClass().getResourceAsStream(path);
        if (stream == null) throw new RuntimeException("Report template not found: " + path);
        return JasperCompileManager.compileReport(stream);
    }

    // Fallback: plain text PDF when no JRXML template present
    private byte[] generateSimplePdfFallback(String title, List<?> data1, List<?> data2) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("Records: ").append(data1.size()).append("\n");
        // Return as bytes — replace with actual PDF library call if iText is added
        return sb.toString().getBytes();
    }
}
