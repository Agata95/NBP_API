package com.javagda25.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

// POJO - Plan Old Java Object -> pusty konstruktor, gettery i settery
@NoArgsConstructor // musi istnieć
@Getter
@Setter
@ToString
@XmlRootElement(name = "ExchangeRatesSeries")
public class ExchangeRatesSeries {
    @XmlElement(name = "Table")
    @SerializedName(value = "table")
    private String Table;

    @XmlElement(name = "Currency")
    @SerializedName(value = "currency")
    private String Currency;

    @XmlElement(name = "Code")
    @SerializedName(value = "code")
    private String Code;

    @XmlElement(name = "Rate")
    @XmlElementWrapper(name = "Rates")  // to co jest na zewnątrz
    @SerializedName(value = "rates")
    private List<Rate> Rates;
}
