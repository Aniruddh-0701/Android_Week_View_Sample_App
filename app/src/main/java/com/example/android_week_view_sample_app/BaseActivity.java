package com.example.android_week_view_sample_app;

import android.content.ClipData;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.my_widgets.weekview.DateTimeInterpreter;
import com.my_widgets.weekview.MonthLoader;
import com.my_widgets.weekview.WeekView;
import com.my_widgets.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Updated by Aniruddh Ramanujam on 06/09/2022
 * Website: <a href="https://github.com/Aniruddh-0701/Android_Week_View_Sample_App">Android Week View Sample App</a>
 */
public abstract class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, WeekView.EmptyViewClickListener, WeekView.AddEventClickListener, WeekView.DropListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    protected WeekView mWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        TextView draggableView = findViewById(R.id.draggable_view);
        draggableView.setOnLongClickListener(new DragTapListener());


        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set EmptyView Click Listener
        mWeekView.setEmptyViewClickListener(this);

        // Set AddEvent Click Listener
        mWeekView.setAddEventClickListener(this);

        // Set Drag and Drop Listener
        mWeekView.setDropListener(this);

        // Set minDate
        // Calendar minDate = Calendar.getInstance();
        // minDate.set(Calendar.DAY_OF_MONTH, 1);
        // minDate.add(Calendar.MONTH, 1);
        // mWeekView.setMinDate(minDate);
        //
        // // Set maxDate
        // Calendar maxDate = Calendar.getInstance();
        // maxDate.add(Calendar.MONTH, 1);
        // maxDate.set(Calendar.DAY_OF_MONTH, 10);
        // mWeekView.setMaxDate(maxDate);
        //
        // Calendar calendar = (Calendar) maxDate.clone();
        // calendar.add(Calendar.DATE, -2);
        // mWeekView.goToDate(calendar);

        //mWeekView.setAutoLimitTime(true);
        //mWeekView.setLimitTime(4, 16);

        //mWeekView.setMinTime(10);
        //mWeekView.setMaxTime(20);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
    }

    @SuppressWarnings({"CommentedOutCode"})
    @Override
    protected void onResume() {
        super.onResume();
        mWeekView.setShowDistinctWeekendColor(true);

// mWeekView.setShowDistinctPastFutureColor(true);
// mWeekView.setFutureBackgroundColor(Color.rgb(24, 85, 96));
// mWeekView.setFutureWeekendBackgroundColor(Color.rgb(255, 0, 0));
// mWeekView.setPastBackgroundColor(Color.rgb(85, 189, 200));
// mWeekView.setPastWeekendBackgroundColor(Color.argb(50, 0, 255, 0));
    }

    private static final class DragTapListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        if (id == R.id.action_today) {
            mWeekView.goToToday();
            return true;
        } else if (id == R.id.action_day_view) {
            if (mWeekViewType != TYPE_DAY_VIEW) {
                item.setChecked(!item.isChecked());
                mWeekViewType = TYPE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(1);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            }
            return true;
        } else if (id == R.id.action_three_day_view) {
            if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                item.setChecked(!item.isChecked());
                mWeekViewType = TYPE_THREE_DAY_VIEW;
                mWeekView.setNumberOfVisibleDays(3);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            }
            return true;
        } else if (id == R.id.action_week_view) {
            if (mWeekViewType != TYPE_WEEK_VIEW) {
                item.setChecked(!item.isChecked());
                mWeekViewType = TYPE_WEEK_VIEW;
                mWeekView.setNumberOfVisibleDays(7);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour, int minutes) {
                String strMinutes = String.format(Locale.getDefault(), "%02d", minutes);
                if (hour > 11) {
                    return (hour - 12) + ":" + strMinutes + " PM";
                } else {
                    if (hour == 0) {
                        return "12:" + strMinutes + " AM";
                    } else {
                        return hour + ":" + strMinutes + " AM";
                    }
                }
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format(Locale.getDefault(), "Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public void onEmptyViewClicked(Calendar date) {
        Toast.makeText(this, "Empty view" + " clicked: " + getEventTitle(date), Toast.LENGTH_SHORT).show();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return null;
    }

    @Override
    public void onAddEventClicked(Calendar startTime, Calendar endTime) {
        Toast.makeText(this, "Add event clicked.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrop(View view, Calendar date) {
        Toast.makeText(this, "View dropped to " + date.toString(), Toast.LENGTH_SHORT).show();
    }
}
