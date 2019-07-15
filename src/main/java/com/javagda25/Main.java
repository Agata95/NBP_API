package com.javagda25;

import com.javagda25.model.ExchangeRatesSeries;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static String BASE_NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}/?format={dataFormat}";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Witaj w konsolowej aplikacji do pobierania kursów walut z API NBP.");

        CurrencyCode currencyCodeEnum = loadCurrencyCodeFromUser(scanner);
        DataFormat dataFormat = loadDataFormatFromUser(scanner);

        LocalDate dateStart;
        LocalDate dateEnd;

        do {
            dateStart = loadDateFromUser(scanner);
            dateEnd = loadDateFromUser(scanner);

            if (dateStart.isAfter(dateEnd)) {
                System.err.println("Data startowa jest późniejsza niż końcowa. Podaj ponownie obie daty.");
            }
        } while (!dateStart.isBefore(dateEnd));

        String table = getTableFromUser(scanner);

        //http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}/?format={dataFormat}
        String requestURL = BASE_NBP_API_URL
                .replace("{table}", table)
                .replace("{code}", currencyCodeEnum.toString())
                .replace("{startDate}", dateStart.format(DATE_TIME_FORMATTER))
                .replace("{endDate}", dateEnd.format(DATE_TIME_FORMATTER))
                .replace("{dataFormat}", dataFormat.toString());

        System.out.println("Your request url is: " + requestURL);

        // metoda loadContentFromURL ładuje zawartość strony do stringa i zwraca go w wyniku.
        // jeśli coś pójdzie nie tak, to zwróci nulla.
//        String apiContent = loadContentFromURL(requestURL);

//        System.out.println(apiContent);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ExchangeRatesSeries.class);

            // marshaller zamienia obiekty na tekst

            // unmarshaller zamienia tekst na obiekty

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ExchangeRatesSeries exchangeRatesSeries = (ExchangeRatesSeries) unmarshaller.unmarshal(new URL(requestURL));

            System.out.println(exchangeRatesSeries);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static String loadContentFromURL(String requestURL) {
        String apiContent = null;
        try {
            // tworzymy sobie obiekt URL
            URL url = new URL(requestURL);

            // input (dla naszej aplikacji) to wejście, czyli do naszej aplikacji będzie ładowany zasób
            // z zewnątrz.
            InputStream inputStream = url.openStream();

            // Buffered reader pozwala czytać zasób, ale wymaga klasy pośredniczącej (np.:
            //                                                                          plik = FileReader
            //                                                                          stream = InputStreamReader
            //
            // posiada metodę "readLine()" - zwraca jedną linię, lub jeśli nie ma treści to zwraca null.
            //
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // tworzymy string builder żeby móc do niego "dopisywać" treści które otrzymamy ze strony.
            // czytane treści będą dodawane (append) do obiektu buildera.
            StringBuilder builder = new StringBuilder();
            String liniaTekstuZReadera;

            // dopóki jest jakaś linia tekstu do przeczytania,
            // przeczytaj ją i przypisz do zmiennej "liniaTekstuZReadera"
            while ((liniaTekstuZReadera = bufferedReader.readLine()) != null) {
                builder.append(liniaTekstuZReadera);
            }

            // pamiętajmy o zamykaniu otwartych zasobów
            bufferedReader.close();

            apiContent = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiContent;
    }

    private static DataFormat loadDataFormatFromUser(Scanner scanner) {
        DataFormat dataFormat = null;
        do {
            try {
                System.out.println("Podaj format danych " + Arrays.toString(DataFormat.values()) + "?");
                String dataF = scanner.nextLine();
                dataFormat = DataFormat.valueOf(dataF.toUpperCase());
            } catch (IllegalArgumentException iae) {
                System.err.println("Niepoprawny format danych, podaj go ponownie.");
            }
        } while (dataFormat == null);
        return dataFormat;
    }

    private static CurrencyCode loadCurrencyCodeFromUser(Scanner scanner) {
        CurrencyCode currencyCodeEnum = null;
        do {
            try {
                System.out.println("Podaj kod waluty " + Arrays.toString(CurrencyCode.values()) + "?");
                String currencyCode = scanner.nextLine();
                currencyCodeEnum = CurrencyCode.valueOf(currencyCode.toUpperCase());
            } catch (IllegalArgumentException iae) {
                System.err.println("Niepoprawny kurs waluty, podaj go ponownie.");
            }
        } while (currencyCodeEnum == null);
        return currencyCodeEnum;
    }

    private static String getTableFromUser(Scanner scanner) {
        String table;
        do {
            System.out.println("Podaj typ tabeli: ASK/BID = C, MID - A/B");
            table = scanner.nextLine();

            if (!table.equalsIgnoreCase("C") && !table.equalsIgnoreCase("A") && !table.equalsIgnoreCase("B")) {
                table = null;
                System.err.println("Niepoprawny typ tabeli. Wpisz ponownie typ tabeli.");
            }
        } while (table == null);
        return table;
    }

    private static LocalDate loadDateFromUser(Scanner scanner) {
        LocalDate dateFromUser = null;
        do {
            try {
                System.out.println("Podaj datę [yyyy-MM-dd]: ");
                String date = scanner.nextLine();

                dateFromUser = LocalDate.parse(date, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException dtpe) {
                System.err.println("Niepoprawny format daty, podaj ją ponownie.");
            }
        } while (dateFromUser == null);

        return dateFromUser;
    }
}

