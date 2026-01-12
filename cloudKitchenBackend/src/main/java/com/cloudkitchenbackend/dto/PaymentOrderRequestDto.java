package com.cloudkitchenbackend.dto;

import com.cloudkitchenbackend.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderRequestDto {
    private long orderId;
    private PaymentMethod paymentMethod;
    private String paymentReciept;

    public PaymentOrderRequestDto(long orderId, String s) {
        this.orderId=orderId;
        this.paymentReciept=s;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReciept() {
        return paymentReciept;
    }

    public void setPaymentReciept(String paymentReciept) {
        this.paymentReciept = paymentReciept;
    }
}
