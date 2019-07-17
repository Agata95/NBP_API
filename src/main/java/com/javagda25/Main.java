package com.javagda25;

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
import java.util.Scanner;


public class Main {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static String BASE_NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}/?format={dataFormat}";

    public static void main(String[] args) {
        /**
         * Zadanie NBP API:
         *
         * Stwórz main'a w którym pytasz użytkownika o 4 parametry, są nimi:
         * - kod waluty
         * - data początku zakresu
         * - data końca zakresu  (zweryfikuj że data końca jest
         *      późniejsza niż początku zakresu)
         * - rodzaj tabeli
         *      - jeśli użytkownik wybierze ASK/BID, chodzi o tabelę C
         *      - jeśli użytkownik wybierze MID, chodzi o tabelę A/B
         *      (możemy przyjąć że będzie to zawsze tabela A, przy wybraniu
         *       drugiej opcji).
         *
         *
         * Jako wynik aplikacji wypisz System.out.println() zapytanie które
         * należy wywołać na API by otrzymać wynik zgodny z danymi które
         * wprowadził użytkownik.
         *
         * Przetestuj działanie aplikacji - sprawdź czy zapytanie (skopiuj je do
         * przeglądarki) zwraca poprawne wyniki.
         *
         **/
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

        String content = loadContentFromURL(requestURL);
        if(content == null){
            System.err.println("Brak danych");
            System.exit(1);
        }
//        Matcher title = Pattern.compile("<ExchangeRatesSeries \"?(.+?)\"?").matcher(content);

//        if(!title.matches()){
//            System.err.println("Zły format danych");
//            System.exit(2); // brak danych
//        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ExchangeRatesSeries.class);

            // marshaller zamienia obiekty na tekst
            // unmarshaller zamienia tekst na obiekty

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ExchangeRatesSeries exchangeRatesSeries = (ExchangeRatesSeries) unmarshaller.unmarshal(new URL(requestURL));


            String choice = loadCalculationsToDo(scanner);
            CalculatedRates valueMinimum = calculateMinimum(exchangeRatesSeries);
            CalculatedRates valueMaximum = calculateMaximum(exchangeRatesSeries);
            CalculatedRates valueAverege = calculateMid(exchangeRatesSeries);

            switch (choice) {
                case "A":
                    if (valueAverege.getMid() != null) {
                        System.out.println("Average mid: " + valueAverege.getMid());
                    }
                    if (valueAverege.getAsk() != null) {
                        System.out.println("Average ask: " + valueAverege.getAsk());
                    }
                    if (valueAverege.getBid() != null) {
                        System.out.println("Average bid: " + valueAverege.getBid());
                    }
                    break;
                case "B":
                    if (valueMaximum.getMid() != null && valueMinimum.getMid() != null) {
                        System.out.println("Deviation mid: " + (valueMaximum.getMid() - valueMinimum.getMid()));
                    }
                    if (valueMaximum.getAsk() != null && valueMinimum.getAsk() != null) {
                        System.out.println("Deviation ask: " + (valueMaximum.getAsk() - valueMinimum.getAsk()));
                    }
                    if (valueMaximum.getBid() != null && valueMinimum.getBid() != null) {
                        System.out.println("Deviation bid: " + (valueMaximum.getBid() - valueMinimum.getBid()));
                    }

                    break;
                case "C":
                    if (valueMaximum.getMid() != null && valueMinimum.getMid() != null) {
                        System.out.println("Max value mid: " + valueMaximum.getMid() + ", min value mid: " + valueMinimum.getMid());
                    }
                    if (valueMaximum.getAsk() != null && valueMinimum.getAsk() != null) {
                        System.out.println("Max value ask: " + valueMaximum.getAsk() + ", min value ask: " + valueMinimum.getAsk());
                    }
                    if (valueMaximum.getBid() != null && valueMinimum.getBid() != null) {
                        System.out.println("Max value bid: " + valueMaximum.getBid() + ", min value bid: " + valueMinimum.getBid());
                    }
                    //
                    break;
            }

        } catch (UnmarshalException ue) {
            System.err.println("Brak danych");
            System.exit(1);
        } catch (JAXBException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static CalculatedRates calculateMinimum(ExchangeRatesSeries exchangeRatesSeries) {
        CalculatedRates calculatedRates = new CalculatedRates();

        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getAsk() != null).mapToDouble(Rate::getAsk).min().ifPresent(calculatedRates::setAsk);
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getBid() != null).mapToDouble(Rate::getBid).min().ifPresent(streamResult -> calculatedRates.setBid(streamResult));
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getMid() != null).mapToDouble(Rate::getMid).min().ifPresent(streamResult -> calculatedRates.setMid(streamResult));

        return calculatedRates;
    }

    private static CalculatedRates calculateMaximum(ExchangeRatesSeries exchangeRatesSeries) {
        // stworzyliśmy obiekt do przechowywania wyników obliczeń (min max avg) - przechowuje 3 wyniki
        CalculatedRates calculatedRates = new CalculatedRates();

        // tabela A zawiera MID
        // tabela C zawiera ASK BID
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getAsk() != null).mapToDouble(Rate::getAsk).max().ifPresent(calculatedRates::setAsk);
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getBid() != null).mapToDouble(Rate::getBid).max().ifPresent(streamResult -> calculatedRates.setBid(streamResult));
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getMid() != null).mapToDouble(Rate::getMid).max().ifPresent(streamResult -> calculatedRates.setMid(streamResult));

        return calculatedRates;
    }

    private static CalculatedRates calculateMid(ExchangeRatesSeries exchangeRatesSeries) {
        CalculatedRates calculatedRates = new CalculatedRates();

        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getAsk() != null).mapToDouble(Rate::getAsk).average().ifPresent(calculatedRates::setAsk);
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getBid() != null).mapToDouble(Rate::getBid).average().ifPresent(streamResult -> calculatedRates.setBid(streamResult));
        exchangeRatesSeries.getRates().stream().filter(rate -> rate.getMid() != null).mapToDouble(Rate::getMid).average().ifPresent(streamResult -> calculatedRates.setMid(streamResult));

        return calculatedRates;
    }

    private static String loadCalculationsToDo(Scanner scanner) {
        String whatToDo;
        do {
            System.out.println("Co chciałbyś obliczyć? [a-średni kurs waluty, b-odchylenie maksymalne, c-znalezienie min i max]");
            whatToDo = scanner.nextLine().toUpperCase();

            if (!whatToDo.equalsIgnoreCase("C") && !whatToDo.equalsIgnoreCase("A") && !whatToDo.equalsIgnoreCase("B")) {
                whatToDo = null;
                System.err.println("Niepoprawny typ tabeli. Wpisz ponownie typ tabeli.");
            }
        } while (whatToDo == null);
        return whatToDo;
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

