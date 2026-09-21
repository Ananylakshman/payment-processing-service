package com.hulkhiretech.payment.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.dto.TransactionDto;
import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.pojo.PaymentResponse;
import com.hulkhiretech.payment.service.PaymentStatusService;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.service.interfaces.PaymentService;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentStatusService statusService;
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;

    @Override
    public PaymentResponse createPayment(CreateTxnReq createTxnReq) {

        log.info("createPayment called with createTxnReq: {}", createTxnReq);

        TransactionDto dto = prepareTxnDTOFromRequest(createTxnReq);

        log.info("Mapped CreateTxnReq to TransactionDTO: {}", dto);

        dto.setTxnStatusId(TxnStatusEnum.CREATED.getName());

        String txnReference = generateUniqueTxnReference();
        dto.setTxnReference(txnReference);

        log.info("Prepared TransactionDTO: {}, txnReference={}",
                dto, txnReference);

        TransactionDto response = statusService.processStatus(dto);

        log.info("Response from PaymentStatusService: {}", response);

        PaymentResponse paymentResponse = new PaymentResponse();

        if (response != null) {
            paymentResponse.setTxnReference(response.getTxnReference());
            paymentResponse.setTxnStatus(response.getTxnStatusId());
        }

        return paymentResponse;
    }

    private String generateUniqueTxnReference() {
        return UUID.randomUUID().toString();
    }

    private TransactionDto prepareTxnDTOFromRequest(
            CreateTxnReq createTxnReq) {

        return modelMapper.map(createTxnReq, TransactionDto.class);
    }
    @Override
    public PaymentResponse initiatePayment(
            String txnReference,
            InitiateTxnReq initiateTxnReq) {

        log.info("initiatePayment called with txnReference: {}, initiateTxnReq: {}",
                txnReference, initiateTxnReq);

        // Get existing transaction from DB
        TransactionEntity entity =
                transactionRepository.getTxnByTxnReference(txnReference);

        log.info("Fetched TransactionEntity from DB: {}", entity);

        // Convert existing entity to DTO
        TransactionDto dto =
                modelMapper.map(entity, TransactionDto.class);

        log.info("Mapped TransactionEntity to TransactionDTO: {}", dto);

        // INITIATED
        dto.setTxnStatusId(TxnStatusEnum.INITIATED.getName());

        TransactionDto responseDto =
                statusService.processStatus(dto);

        log.info("Response after INITIATED status: {}", responseDto);

        // PENDING
        dto.setTxnStatusId(TxnStatusEnum.PENDING.getName());

        responseDto =
                statusService.processStatus(dto);

        log.info("Response after PENDING status: {}", responseDto);

        PaymentResponse response = new PaymentResponse();

        response.setTxnReference(responseDto.getTxnReference());
        response.setTxnStatus(responseDto.getTxnStatusId());

        log.info("Returning PaymentResponse: {}", response);

        return response;
    }

    @Override
	public PaymentResponse capturePayment(String txnReference) {
		log.info("capturePayment called with "
				+ "txnReference: {}", txnReference);

		TransactionEntity entity = transactionRepository.getTxnByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from DB: {}", entity);

		// use modelMapper to map entity to DTO
		TransactionDto dto = modelMapper.map(entity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDTO: {}", dto);

		dto.setTxnStatusId(TxnStatusEnum.APPROVED.getName());
		TransactionDto responseDto = statusService.processStatus(dto);
		log.info("Response from PaymentStatusService after processing "
				+ "APPROVED status: {}", responseDto);


dto.setTxnStatusId(TxnStatusEnum.SUCCESS.getName());
responseDto = statusService.processStatus(dto);
PaymentResponse response = new PaymentResponse();
response.setTxnReference(responseDto.getTxnReference());
response.setTxnStatus(responseDto.getTxnStatusId());
log.info("Returning PaymentResponse: {}", response);
        return response;
    }
}