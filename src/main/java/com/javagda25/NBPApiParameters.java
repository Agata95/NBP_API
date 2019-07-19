package com.javagda25;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NBPApiParameters {
    private String table;
    private LocalDate dateEnd;
    private LocalDate dateStart;
    private CurrencyCode currencyCodeEnum;
    private DataFormat dataFormat;

    public boolean startIsAfterEnd() {
        return dateStart.isAfter(dateEnd);
    }
}
