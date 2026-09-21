package com.hulkhiretech.payment.service;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.dto.TransactionDto;
import com.hulkhiretech.payment.service.factory.TxnStatusFactory;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {

    private final TxnStatusFactory txnStatusFactory;

    public TransactionDto processStatus(TransactionDto dto) {

        log.info("Processing status in PaymentStatusService dto: {}", dto);

        TxnStatusEnum statusEnum =
                TxnStatusEnum.getByName(dto.getTxnStatusId());

        TxnStatusProcessor statusProcessor =
                txnStatusFactory.getStatusProcessor(statusEnum);

        TransactionDto responseDto =
                statusProcessor.processStatus(dto);

        log.info(
                "response DTO from {}: {}",
                statusProcessor.getClass().getSimpleName(),
                responseDto
        );

        return responseDto;
    }
}