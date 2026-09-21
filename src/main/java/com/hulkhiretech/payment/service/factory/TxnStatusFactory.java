package com.hulkhiretech.payment.service.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.service.impl.serviceprocessor.ApprovedStatusProcessor2;
import com.hulkhiretech.payment.service.impl.serviceprocessor.CreatedStatusProcessor;
import com.hulkhiretech.payment.service.impl.serviceprocessor.FailedStatusProcessor2;
import com.hulkhiretech.payment.service.impl.serviceprocessor.InitiatedStatusProcessor2;
import com.hulkhiretech.payment.service.impl.serviceprocessor.PendingStatusProcessor2;
import com.hulkhiretech.payment.service.impl.serviceprocessor.SuccessStatusProcessor2;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TxnStatusFactory {

    private final ApplicationContext appContext;

    public TxnStatusProcessor getStatusProcessor(TxnStatusEnum statusEnum) {

        if (statusEnum == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        switch (statusEnum) {

            case CREATED:
                return appContext.getBean(CreatedStatusProcessor.class);

            case INITIATED:
                return appContext.getBean(InitiatedStatusProcessor2.class);

            case PENDING:
                return appContext.getBean(PendingStatusProcessor2.class);
            case APPROVED:
                return appContext.getBean(ApprovedStatusProcessor2.class);

            case SUCCESS:
                return appContext.getBean(SuccessStatusProcessor2.class);

            case FAILED:
                return appContext.getBean(FailedStatusProcessor2.class);

            default:
                throw new IllegalArgumentException(
                        "Invalid status: " + statusEnum
                );
        }
    }
}