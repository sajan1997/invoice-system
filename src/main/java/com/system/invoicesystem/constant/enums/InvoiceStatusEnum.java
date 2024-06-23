package com.system.invoicesystem.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatusEnum {

    PENDING("pending"),
    PAID("paid"),
    VOID("void");

    private final String value;
}
