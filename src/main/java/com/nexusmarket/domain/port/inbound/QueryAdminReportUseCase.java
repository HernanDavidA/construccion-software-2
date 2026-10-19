package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.AdminReport;
import com.nexusmarket.domain.model.User;

public interface QueryAdminReportUseCase {
    AdminReport query(User actor);
}
