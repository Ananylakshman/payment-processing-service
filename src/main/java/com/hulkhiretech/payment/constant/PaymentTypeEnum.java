package com.hulkhiretech.payment.constant;
import lombok.Getter;
@Getter
public enum PaymentTypeEnum {

   SALE(1, "SALE");

    private final Integer id;
	 private final String name;

    PaymentTypeEnum( Integer id , String name) {
        this.name = name;
        this.id = id;
    }
    public static PaymentTypeEnum getById(Integer id) {

        if (id == null) {
            return null;
        }

        for (PaymentTypeEnum type : values()) {
            if (id.equals(type.id)) {
                return type;
            }
        }

        return null;
    }

    public static PaymentTypeEnum getByName(String name) {

        if (name == null) {
            return null;
        }

        for (PaymentTypeEnum type : values()) {
            if (name.equalsIgnoreCase(type.name)) {
                return type;
            }
        }

        return null;
    }
}