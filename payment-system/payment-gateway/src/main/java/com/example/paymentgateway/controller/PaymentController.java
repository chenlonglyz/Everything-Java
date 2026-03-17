package com.example.paymentgateway;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.paymentapi.PaymentLink;
import com.example.paymentapi.PaymentMethod;
import com.example.paymentapi.PaymentRequest;
import com.example.paymentapi.PaymentResult;
import com.example.paymentcore.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMQService mqService;
    private final IdempotentService idempotentService;

    public PaymentController(PaymentService paymentService, PaymentMQService mqService, IdempotentService idempotentService) {
        this.paymentService = paymentService;
        this.mqService = mqService;
        this.idempotentService = idempotentService;
    }

    /**
     * 获取支付方式
     */
    @GetMapping("/methods")
    public List<PaymentMethod> methods() {
        return paymentService.getPaymentMethods();
    }


    @PostMapping("/link")
    public PaymentLink create(@RequestBody PaymentCreateDTO dto) {

        PaymentRequest request = new PaymentRequest();

        request.setOrderId(dto.getOrderId());
        request.setAmount(dto.getAmount());

        return paymentService.createPayment(
                dto.getPayCode(),
                request
        );
    }

    @PostMapping("/callback/{payCode}")
    public String callback(
            @PathVariable String payCode,
            @RequestBody String body) {

        PaymentResult result = paymentService.callback(payCode, body);

        String key = "pay:callback:" + result.getOrderId();

        if (!idempotentService.tryLock(key, 10)) {
            return "duplicate";
        }

        mqService.send(result);

        return "success";
    }

}
