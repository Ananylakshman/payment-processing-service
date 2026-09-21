package com.hulkhiretech.payment.pojo;
import java.math.BigDecimal;

import lombok.Data;
@Data
public class CreateTxnReq {
	

	    private String userId;

	    private String paymentMethodId;

	    private String providerId;

	    private String paymentTypeId;

	    

	    private BigDecimal amount;

	    private String currency;

	    private String merchantTransactionReference;

}
