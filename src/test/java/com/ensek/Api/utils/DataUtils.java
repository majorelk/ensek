package com.ensek.Api.utils;

import io.restassured.response.Response;

public class DataUtils {

    public static String getResponseValueAsString(Response response, String key) {
        return response.jsonPath().getString(key);
    }

    // Add more data manipulation methods if needed
}
