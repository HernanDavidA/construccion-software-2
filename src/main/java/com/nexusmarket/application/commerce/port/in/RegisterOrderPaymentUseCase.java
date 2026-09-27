package com.nexusmarket.application.commerce.port.in;

import com.nexusmarket.domain.commerce.Invoice;

public interface RegisterOrderPaymentUseCase {

    Invoice execute(String orderId, String invoiceNumber);
}
