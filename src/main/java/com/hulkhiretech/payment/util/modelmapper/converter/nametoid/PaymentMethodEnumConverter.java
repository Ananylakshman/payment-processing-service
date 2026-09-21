package com.hulkhiretech.payment.util.modelmapper.converter.nametoid;

import org.modelmapper.AbstractConverter;

import com.hulkhiretech.payment.constant.PaymentMethodEnum;

public class PaymentMethodEnumConverter extends AbstractConverter<String, Integer> {

	@Override
	protected Integer convert(String source) {

	    PaymentMethodEnum paymentMethod =
	            PaymentMethodEnum.getByName(source);

	    if (paymentMethod == null) {
	        return null;
	    }

	    return paymentMethod.getId();
	}
}