package com.example.android_week_view_sample_app;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.android_week_view_sample_app.apiclient.Event;
import com.example.android_week_view_sample_app.apiclient.MyJsonService;
import com.my_widgets.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * An example of how events can be fetched from network and be displayed on the week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Modified by Aniruddh Ramanujam on 31/08/2022
 */
public class AsynchronousActivity extends BaseActivity implements Callback<List<Event>> {

    private final List<WeekViewEvent> events = new ArrayList<>();
    boolean calledNetwork = false;

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Download events from network if it hasn't been done already. To understand how events are
        // downloaded using retrofit, visit http://square.github.io/retrofit
        if (!calledNetwork) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.myjson.com/bins").build();
            MyJsonService service = retrofit.create(MyJsonService.class);
            service.listEvents(this);
            calledNetwork = true;
        }

        // Return only the events that matches newYear and newMonth.
        List<WeekViewEvent> matchedEvents = new ArrayList<>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    /**
     * Checks if an event falls into a specific year and month.
     *
     * @param event The event to check for.
     * @param year  The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        if (event.getStartTime().get(Calendar.YEAR) == year)
            if (event.getStartTime().get(Calendar.MONTH) == month - 1) return true;
        if (event.getEndTime().get(Calendar.YEAR) == year)
            return event.getEndTime().get(Calendar.MONTH) == month - 1;
        return false;
    }


    @Override
    public void onResponse(Call<List<Event>> call, @NonNull Response<List<Event>> response) {
        if (call.isExecuted() && response.body() != null) {
            for (Event event : response.body()) {
                this.events.add(event.toWeekViewEvent());
            }
            getWeekView().notifyDatasetChanged();
        }
    }

    @Override
    public void onFailure(Call<List<Event>> call, @NonNull Throwable t) {
        if (call.isCanceled()) {
            t.printStackTrace();
            Toast.makeText(this, R.string.async_error, Toast.LENGTH_SHORT).show();
        }
    }
}
