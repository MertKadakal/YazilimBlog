package mert.kadakal.yazlmblog.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = "https://13.60.84.136:5000/";
    private static final String BASE_URL = "http://10.0.2.2:5001/";
    //private static final String BASE_URL = "http://0.0.0.0:5000/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

