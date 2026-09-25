package com.hulkhiretech.payment.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.dto.TransactionDto;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.payalprovider.OrderRes;
import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.pojo.PaymentResponse;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.PaymentStatusService;
import com.hulkhiretech.payment.service.client.PaypalProviderClient;
import com.hulkhiretech.payment.service.helper.PaypalProviderCaptureOrderHelper;
import com.hulkhiretech.payment.service.helper.PaypalProviderCreateOrderHelper;
import com.hulkhiretech.payment.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentStatusService statusService;

    private final ModelMapper modelMapper;

    private final TransactionRepository transactionRepository;

    private final PaypalProviderClient paypalProviderClient;

    private final PaypalProviderCreateOrderHelper paypalProviderCreateOrderHelper;
    private final PaypalProviderCaptureOrderHelper paypalProviderCaptureOrderHelper;

    @Override
    public PaymentResponse createPayment(CreateTxnReq createTxnReq) {

        log.info(
                "createPayment called with createTxnReq: {}",
                createTxnReq
        );

        TransactionDto dto =
                prepareTxnDTOFromRequest(createTxnReq);

        log.info(
                "Mapped CreateTxnReq to TransactionDTO: {}",
                dto
        );

        dto.setTxnStatusId(
                TxnStatusEnum.CREATED.getName()
        );

        String txnReference =
                generateUniqueTxnReference();

        dto.setTxnReference(txnReference);

        log.info(
                "Prepared TransactionDTO: {}, txnReference={}",
                dto,
                txnReference
        );

        TransactionDto response =
                statusService.processStatus(dto);

        log.info(
                "Response from PaymentStatusService: {}",
                response
        );

        PaymentResponse paymentResponse =
                new PaymentResponse();

        if (response != null) {

            paymentResponse.setTxnReference(
                    response.getTxnReference()
            );

            paymentResponse.setTxnStatus(
                    response.getTxnStatusId()
            );
        }

        return paymentResponse;
    }

    private String generateUniqueTxnReference() {

        return UUID.randomUUID().toString();
    }

    private TransactionDto prepareTxnDTOFromRequest(
            CreateTxnReq createTxnReq) {

        return modelMapper.map(
                createTxnReq,
                TransactionDto.class
        );
    }

    @Override
    public PaymentResponse initiatePayment(
            String txnReference,
            InitiateTxnReq initiateTxnReq) {

        log.info(
                "initiatePayment called with txnReference: {}, initiateTxnReq: {}",
                txnReference,
                initiateTxnReq
        );

        // 1. Get existing transaction from DB

        TransactionEntity entity =
                transactionRepository.getTxnByTxnReference(
                        txnReference
                );

        log.info(
                "Fetched TransactionEntity from DB: {}",
                entity
        );

        // 2. Convert Entity to DTO

        TransactionDto dto =
                modelMapper.map(
                        entity,
                        TransactionDto.class
                );

        log.info(
                "Mapped TransactionEntity to TransactionDTO: {}",
                dto
        );

        // 3. Update status to INITIATED

        dto.setTxnStatusId(
                TxnStatusEnum.INITIATED.getName()
        );

        TransactionDto responseDto =
                statusService.processStatus(dto);

        log.info(
                "Response after INITIATED status: {}",
                responseDto
        );

        // 4. Prepare request for PayPal Provider

        HttpRequest httpRequest =
                paypalProviderCreateOrderHelper.prepareRequest(
                        dto,
                        initiateTxnReq
                );

        log.info(
                "Prepared PayPal Provider HttpRequest: {}",
                httpRequest
        );

        // 5. Call PayPal Provider Service

        ResponseEntity<String> httpResponse =
                paypalProviderClient.makeCall(
                        httpRequest
                );

        log.info(
                "Response from PayPal Provider: {}",
                httpResponse
        );
String redirectUrl= "https://paypal.com/checkout?orderId=PayPalOrderId12345";
        // 6. Process PayPal Provider response

        OrderRes orderRes =
                paypalProviderCreateOrderHelper.processResponse(
                        httpResponse
                );

        log.info(
                "Processed PayPal Provider response: {}",
                orderRes
        );

        // 7. Save PayPal order ID as provider reference

        dto.setProviderReference(
                orderRes.getOrderId()
        );

        log.info(
                "PayPal orderId saved as providerReference: {}",
                orderRes.getOrderId()
        );

        // 8. Update status to PENDING

        dto.setTxnStatusId(
                TxnStatusEnum.PENDING.getName()
        );

        responseDto =
                statusService.processStatus(dto);

        log.info(
                "Response after PENDING status: {}",
                responseDto
        );

        // 9. Prepare final PaymentResponse

        PaymentResponse response =
                new PaymentResponse();

        response.setTxnReference(
                responseDto.getTxnReference()
        );

        response.setTxnStatus(
                responseDto.getTxnStatusId()
        );

        response.setProviderReference(
                orderRes.getOrderId()
        );

        response.setRedirectUrl(
                orderRes.getRedirectUrl()
        );

        log.info(
                "Returning PaymentResponse: {}",
                response
        );

        return response;
    }

    @Override
    public PaymentResponse capturePayment(String txnReference) {

        log.info(
                "capturePayment called with txnReference: {}",
                txnReference
        );

        // 1. Get transaction from DB
        TransactionEntity entity =
                transactionRepository.getTxnByTxnReference(txnReference);

        log.info(
                "Fetched TransactionEntity from DB: {}",
                entity
        );

        // 2. Convert Entity to DTO
        TransactionDto dto =
                modelMapper.map(
                        entity,
                        TransactionDto.class
                );

        log.info(
                "Mapped TransactionEntity to TransactionDTO: {}",
                dto
        );

        // 3. APPROVED
        dto.setTxnStatusId(
                TxnStatusEnum.APPROVED.getName()
        );

        TransactionDto responseDto =
                statusService.processStatus(dto);

        log.info(
                "Response from PaymentStatusService after processing APPROVED status: {}",
                responseDto
        );

        // 4. Prepare PayPal capture request
        HttpRequest httpRequest =
                paypalProviderCaptureOrderHelper.prepareRequest(
                        dto.getProviderReference()
                );

        log.info(
                "Prepared HttpRequest for PayPal capture order: {}",
                httpRequest
        );

        // 5. Call PayPal Provider Service - 8083
        ResponseEntity<String> httpResponse =
                paypalProviderClient.makeCall(httpRequest);

        log.info(
                "Response from PayPal Provider capture: {}",
                httpResponse
        );

        // 6. Process PayPal capture response
        OrderRes orderRes =
                paypalProviderCaptureOrderHelper.processResponse(
                        httpResponse
                );

        log.info(
                "Processed PayPal capture response: {}",
                orderRes
        );

        // 7. Update provider reference
        dto.setProviderReference(
                orderRes.getOrderId()
        );

        // 8. SUCCESS
        dto.setTxnStatusId(
                TxnStatusEnum.SUCCESS.getName()
        );

        responseDto =
                statusService.processStatus(dto);

        log.info(
                "Response from PaymentStatusService after processing SUCCESS status: {}",
                responseDto
        );

        // 9. Prepare final PaymentResponse
        PaymentResponse response =
                new PaymentResponse();

        response.setTxnReference(
                responseDto.getTxnReference()
        );

        response.setTxnStatus(
                responseDto.getTxnStatusId()
        );

        response.setProviderReference(
                responseDto.getProviderReference()
        );

        log.info(
                "Returning PaymentResponse: {}",
                response
        );

        return response;
    }
}