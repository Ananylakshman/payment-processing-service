package com.hulkhiretech.payment.service.interfaces;

import com.hulkhiretech.payment.dto.TransactionDto;
public interface TxnStatusProcessor {
    TransactionDto processStatus(TransactionDto dto);
}