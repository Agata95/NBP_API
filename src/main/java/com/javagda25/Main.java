package com.javagda25;

import com.javagda25.model.ExchangeRatesSeries;

import javax.xml.bind.JAXBException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Main {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {

        ScannerContentLoader scanner = new ScannerContentLoader();
        NBPApiParameters parameters = new NBPApiParameters();
        NBPApi api = null;
        try {
            api = new NBPApi();
        } catch (JAXBException e) {
            System.err.println("Niepoprawna budowa XML.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Witaj w konsolowej aplikacji do pobierania kursów walut z API NBP.");

        parameters.setCurrencyCodeEnum(scanner.loadCurrencyCodeFromUser());
        parameters.setDataFormat(scanner.loadDataFormatFromUser());

        do {
            // wykonuj
            parameters.setDateStart(scanner.loadDateFromUser());
            parameters.setDateEnd(scanner.loadDateFromUser());

            if (parameters.startIsAfterEnd()) {
                System.err.println("Data startowa jest późniejsza niż końcowa. Podaj ponownie obie daty.");
            }
            // powtarzaj dopóki start jest po end
        } while (parameters.startIsAfterEnd());

        parameters.setTable(scanner.getTableFromUser());
        String requestURL = api.loadUrl(parameters);

        Optional<ExchangeRatesSeries> exchangeRatesSeriesOptional = api.loadAndParseExchangeRatesSeries(requestURL);
        if (exchangeRatesSeriesOptional.isPresent()) {
            ExchangeRatesSeries series = exchangeRatesSeriesOptional.get();

            System.out.println(series);
        }
    }

}

