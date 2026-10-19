package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RequestReturn;
import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.port.inbound.ProcessRefundUseCase;
import com.nexusmarket.domain.port.inbound.QueryReturnsUseCase;
import com.nexusmarket.domain.port.inbound.RequestReturnUseCase;
import com.nexusmarket.domain.port.inbound.ReviewReturnUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final RequestReturnUseCase requestReturn;
    private final ReviewReturnUseCase reviewReturn;
    private final ProcessRefundUseCase processRefund;
    private final QueryReturnsUseCase queryReturns;
    private final CurrentUser currentUser;

    public ReturnController(
            RequestReturnUseCase requestReturn,
            ReviewReturnUseCase reviewReturn,
            ProcessRefundUseCase processRefund,
            QueryReturnsUseCase queryReturns,
            CurrentUser currentUser) {
        this.requestReturn = requestReturn;
        this.reviewReturn = reviewReturn;
        this.processRefund = processRefund;
        this.queryReturns = queryReturns;
        this.currentUser = currentUser;
    }

    @PostMapping
    ReturnRequest request(HttpServletRequest httpRequest, @RequestBody RequestReturn command) {
        return requestReturn.request(currentUser.require(httpRequest), command);
    }

    @PostMapping("/{id}/approve")
    ReturnRequest approve(HttpServletRequest request, @PathVariable String id) {
        return reviewReturn.approve(currentUser.require(request), id);
    }

    @PostMapping("/{id}/reject")
    ReturnRequest reject(HttpServletRequest request, @PathVariable String id) {
        return reviewReturn.reject(currentUser.require(request), id);
    }

    @PostMapping("/{id}/refund")
    Refund refund(HttpServletRequest request, @PathVariable String id) {
        return processRefund.process(currentUser.require(request), id);
    }

    @GetMapping("/order/{orderId}")
    List<ReturnRequest> byOrder(HttpServletRequest request, @PathVariable String orderId) {
        return queryReturns.listByOrder(currentUser.require(request), orderId);
    }
}
