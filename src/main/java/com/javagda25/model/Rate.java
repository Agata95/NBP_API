package com.javagda25.model;

import lombok.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement(name="Rate")
public class Rate {
    @XmlElement(name = "No")
    private String no;

    @XmlElement(name = "EffectiveDate")
    private String EffectiveDate;

    @XmlElement(name = "Bid")
    private Double Bid; // cena sprzedaz

    @XmlElement(name = "Ask")
    private Double Ask; // cena kupno

    @XmlElement(name = "Mid")
    private Double Mid;
}
