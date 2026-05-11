package com.loanapp.report;

import com.loanapp.application.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final LoanApplicationService applicationService;

    // --- Customer Reports ---

    @GetMapping(value = "/customer/loan/{loanAccountId}/statement/pdf",
                produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> customerStatementPdf(@PathVariable Long loanAccountId) {
        byte[] pdf = reportService.generateCustomerStatementPdf(loanAccountId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement_" + loanAccountId + ".pdf")
                .body(pdf);
    }

    @GetMapping(value = "/customer/loan/{loanAccountId}/emi-schedule/pdf",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> emiSchedulePdf(@PathVariable Long loanAccountId) {
        byte[] pdf = reportService.generateEmiSchedulePdf(loanAccountId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emi_schedule_" + loanAccountId + ".pdf")
                .body(pdf);
    }

    // --- Admin Reports ---

    @GetMapping(value = "/admin/overdue-loans/excel",
                produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> overdueLoansExcel() {
        byte[] xlsx = reportService.generateOverdueLoansExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue_loans.xlsx")
                .body(xlsx);
    }

    @GetMapping(value = "/admin/monthly-collections/excel",
                produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> monthlyCollectionsExcel(
            @RequestParam int year,
            @RequestParam int month) {
        byte[] xlsx = reportService.generateMonthlyCollectionsExcel(year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=collections_" + year + "_" + month + ".xlsx")
                .body(xlsx);
    }

    @GetMapping(value = "/admin/pending-approvals/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> pendingApprovalsPdf() {
        byte[] pdf = reportService.generatePendingApprovalsPdf(applicationService.getPendingApplications());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pending_approvals.pdf")
                .body(pdf);
    }
}
