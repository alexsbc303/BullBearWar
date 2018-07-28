package com.example.bullbearwar.CurrencyConverter;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyExchangeService {
    @GET("latest?base=HKD")
    Call<CurrencyExchange> loadCurrencyExchange();
}


