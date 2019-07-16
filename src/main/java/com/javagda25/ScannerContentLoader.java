package com.javagda25;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class ScannerContentLoader {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Scanner scanner = new Scanner(System.in);

    public DataFormat loadDataFormatFromUser() {
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

    public CurrencyCode loadCurrencyCodeFromUser() {
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

    public String getTableFromUser() {
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

    public LocalDate loadDateFromUser() {
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
