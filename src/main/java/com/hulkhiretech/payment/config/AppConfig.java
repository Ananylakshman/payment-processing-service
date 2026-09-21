package com.hulkhiretech.payment.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hulkhiretech.payment.dto.TransactionDto;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.util.modelmapper.converter.idtoname.PaymentMethodEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.idtoname.PaymentTypeEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.idtoname.ProviderEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.idtoname.TxnStatusEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.nametoid.PaymentMethodEnumConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.nametoid.PaymentTypeEnumConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.nametoid.ProviderEnumConverter;
import com.hulkhiretech.payment.util.modelmapper.converter.nametoid.TxnStatusEnumConverter;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<String, Integer> paymentMethodEnumConverter =
                new PaymentMethodEnumConverter();

        Converter<String, Integer> providerEnumConverter =
                new ProviderEnumConverter();

        Converter<String, Integer> paymentTypeEnumConverter =
                new PaymentTypeEnumConverter();

        Converter<String, Integer> txnStatusEnumConverter =
                new TxnStatusEnumConverter();

        Converter<Integer, String> paymentMethodIdToNameConverter =
                new PaymentMethodEnumIdToNameConverter();

        Converter<Integer, String> providerIdToNameConverter =
                new ProviderEnumIdToNameConverter();

        Converter<Integer, String> paymentTypeIdToNameConverter =
                new PaymentTypeEnumIdToNameConverter();

        Converter<Integer, String> txnStatusIdToNameConverter =
                new TxnStatusEnumIdToNameConverter();

        modelMapper.addMappings(
                new PropertyMap<TransactionDto, TransactionEntity>() {
                    @Override
                    protected void configure() {

                        using(paymentMethodEnumConverter)
                                .map(source.getPaymentMethodId(),
                                        destination.getPaymentMethodId());

                        using(providerEnumConverter)
                                .map(source.getProviderId(),
                                        destination.getProviderId());

                        using(paymentTypeEnumConverter)
                                .map(source.getPaymentTypeId(),
                                        destination.getPaymentTypeId());

                        using(txnStatusEnumConverter)
                                .map(source.getTxnStatusId(),
                                        destination.getTxnStatusId());
                    }
                }
        );

        modelMapper.addMappings(
                new PropertyMap<TransactionEntity, TransactionDto>() {
                    @Override
                    protected void configure() {

                        using(paymentMethodIdToNameConverter)
                                .map(source.getPaymentMethodId(),
                                        destination.getPaymentMethodId());

                        using(providerIdToNameConverter)
                                .map(source.getProviderId(),
                                        destination.getProviderId());

                        using(paymentTypeIdToNameConverter)
                                .map(source.getPaymentTypeId(),
                                        destination.getPaymentTypeId());

                        using(txnStatusIdToNameConverter)
                                .map(source.getTxnStatusId(),
                                        destination.getTxnStatusId());
                    }
                }
        );

        return modelMapper;
    }
}