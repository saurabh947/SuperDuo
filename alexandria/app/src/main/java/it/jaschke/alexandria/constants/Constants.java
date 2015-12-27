package it.jaschke.alexandria.constants;

import it.jaschke.alexandria.camera.BarcodeScannerActivity;

public class Constants {
    /**
     * The Scandit SDK App key.
     */
    public static final String API_KEY = "<------ INSERT YOUR SCANDIT API KEY HERE ------>";
    /**
     * The request code for {@link BarcodeScannerActivity}.
     */
    public static final int REQUEST_CODE = 100;
    /**
     * The invalid result code.
     */
    public static final int RESULT_FAIL = 1;
    /**
     * The base google API url to query for book details.
     */
    public static final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
}
