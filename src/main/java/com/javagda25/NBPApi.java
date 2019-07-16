package com.javagda25;

import com.javagda25.model.ExchangeRatesSeries;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class NBPApi {
    private final static String BASE_NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}/?format={dataFormat}";
    private final JAXBContext jaxbContext;
    private final Unmarshaller unmarshaller;

    public NBPApi() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(ExchangeRatesSeries.class);

        // marshaller zamienia obiekty na tekst
        // unmarshaller zamienia tekst na obiekty
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    public String loadUrl(NBPApiParameters parameters) {

        return BASE_NBP_API_URL
                .replace("{table}", parameters.getTable())
                .replace("{code}", parameters.getCurrencyCodeEnum().toString())
                .replace("{startDate}", parameters.getDateStart().toString())
                .replace("{endDate}", parameters.getDateEnd().toString())
                .replace("{dataFormat}", parameters.getDataFormat().toString());
    }

    public String loadContentFromURL(String requestURL) {
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

    public Optional<ExchangeRatesSeries> loadAndParseExchangeRatesSeries(String requestURL) {
        try {
            ExchangeRatesSeries exchangeRatesSeries = (ExchangeRatesSeries) unmarshaller.unmarshal(new URL(requestURL));

            return Optional.of(exchangeRatesSeries);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
