package com.hulkhiretech.payment.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
@Data
public class TransactionDto {

    private Integer id;

    private String userId;
    private String paymentMethodId;
    private String providerId;
    private String paymentTypeId;
    private String txnStatusId;
    private BigDecimal amount;

    private String currency;

    private String merchantTransactionReference;

    private String txnReference;

    private String providerReference;

    private String errorCode;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer retryCount;
}
