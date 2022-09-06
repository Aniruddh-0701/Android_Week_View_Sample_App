package com.example.android_week_view_sample_app.apiclient;

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.GET;

/**
 * Created by Raquib-ul-Alam Kanak on 1/3/16.
 * Website: <a href="https://github.com/Aniruddh-0701/Android_Week_View_Sample_App">Android Week View Sample App</a>
 */
public interface MyJsonService {

    @GET("/1kpjf")
    void listEvents(Callback<List<Event>> eventsCallback);

}
