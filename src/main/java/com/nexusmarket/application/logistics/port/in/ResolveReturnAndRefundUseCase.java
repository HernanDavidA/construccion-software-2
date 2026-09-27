package com.nexusmarket.application.logistics.port.in;

import com.nexusmarket.domain.logistics.ReturnRequest;

public interface ResolveReturnAndRefundUseCase {

    ReturnRequest execute(String administratorId, String returnId, boolean approve,
                          boolean processRefund, String refundId);
}
