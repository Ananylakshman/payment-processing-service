package com.hulkhiretech.payment.util.modelmapper.converter.nametoid;
import org.modelmapper.AbstractConverter;

import com.hulkhiretech.payment.constant.ProviderEnum;

public class ProviderEnumConverter extends AbstractConverter<String, Integer> {

    @Override
    protected Integer convert(String source) {
    	if (source == null) {
			return null; // Handle null input gracefully
		}
    	ProviderEnum providerEnum = ProviderEnum.getByName(source);
        return ProviderEnum.getByName(source).getId();
    }
}