package com.javagda25;

import com.google.gson.Gson;
import com.javagda25.model.ExchangeRatesSeries;
import com.javagda25.model.Rate;

import javax.xml.bind.JAXBException;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;


public class Main {

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