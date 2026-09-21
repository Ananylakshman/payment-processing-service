package com.hulkhiretech.payment.service.impl.serviceprocessor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.dto.TransactionDto;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class CreatedStatusProcessor implements TxnStatusProcessor {

    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto processStatus(TransactionDto dto) {

        log.info("Processing Created Status with dto: {}", dto);

        TransactionEntity transactionEntity =
                modelMapper.map(dto, TransactionEntity.class);

        log.info("TransactionEntity created: {}", transactionEntity);

        int pkId = transactionRepository.createTransaction(transactionEntity);

        log.info("Response from TransactionRepository: {}", pkId);

        dto.setId(pkId);

        return dto;
    }
}