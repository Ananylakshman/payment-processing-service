package com.hulkhiretech.payment.constant;
import lombok.Getter;
@Getter
public enum PaymentMethodEnum {
     APM(1, "APM");

    
    private final Integer id;
    private final String name;

    PaymentMethodEnum( Integer id , String name) {
        this.name = name;
        this.id = id;
    }
    public static PaymentMethodEnum getById(Integer id) {

        if (id == null) {
            return null;
        }

        for (PaymentMethodEnum method : values()) {
            if (id.equals(method.id)) {
                return method;
            }
        }

        return null;
    }

    public static PaymentMethodEnum getByName(String name) {

        if (name == null) {
            return null;
        }

        for (PaymentMethodEnum method : values()) {
            if (name.equalsIgnoreCase(method.name)) {
                return method;
            }
        }

        return null;
    }

}