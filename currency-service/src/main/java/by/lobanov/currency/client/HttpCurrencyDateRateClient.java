package by.lobanov.currency.client;

import java.time.LocalDate;

public interface HttpCurrencyDateRateClient {

    String requestByDate(LocalDate date);
}
