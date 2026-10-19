package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.model.AdminReport;
import com.nexusmarket.domain.port.inbound.QueryAdminReportUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final QueryAdminReportUseCase reports;
    private final CurrentUser currentUser;

    public ReportController(QueryAdminReportUseCase reports, CurrentUser currentUser) {
        this.reports = reports;
        this.currentUser = currentUser;
    }

    @GetMapping
    AdminReport query(HttpServletRequest request) {
        return reports.query(currentUser.require(request));
    }
}
