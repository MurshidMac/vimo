package vimo.activities.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import vimo.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import vimo.activities.HeartRateUtils;
import vimo.database.DBHandler;
import vimo.impl.GBDevice;
import vimo.model.ActivitySample;
import vimo.model.DeviceService;
import vimo.model.Measurement;
import vimo.util.GB;

public class LiveActivityFragment extends AbstractChartFragment {
    private static final Logger LOG = LoggerFactory.getLogger(LiveActivityFragment.class);
    private static final int MAX_STEPS_PER_MINUTE = 300;
    private static final int MIN_STEPS_PER_MINUTE = 60;
    private static final int RESET_COUNT = 10; // reset the max steps per minute value every 10s

    private BarEntry totalStepsEntry;
    private BarEntry stepsPerMinuteEntry;
    private BarDataSet mStepsPerMinuteData;
    private BarDataSet mTotalStepsData;
    private LineDataSet mHistorySet;
    private BarLineChartBase mStepsPerMinuteHistoryChart;
    private CustomBarChart mStepsPerMinuteCurrentChart;
    private CustomBarChart mTotalStepsChart;

    private final Steps mSteps = new Steps();
    private ScheduledExecutorService pulseScheduler;
    private int maxStepsResetCounter;
    private List<Measurement> heartRateValues;
    private LineDataSet mHeartRateSet;
    private int mHeartRate;
    private TimestampTranslation tsTranslation;

    private class Steps {
        private int steps;
        private int lastTimestamp;
        private int currentStepsPerMinute;
        private int maxStepsPerMinute;
        private int lastStepsPerMinute;

        public int getStepsPerMinute(boolean reset) {
            lastStepsPerMinute = currentStepsPerMinute;
            int result = currentStepsPerMinute;
            if (reset) {
                currentStepsPerMinute = 0;
            }
            return result;
        }

        public int getTotalSteps() {
            return steps;
        }

        public int getMaxStepsPerMinute() {
            return maxStepsPerMinute;
        }

        public void updateCurrentSteps(int stepsDelta, int timestamp) {
            try {
                if (steps == 0) {
                    steps += stepsDelta;
                    lastTimestamp = timestamp;
                    return;
                }

                int timeDelta = timestamp - lastTimestamp;
                currentStepsPerMinute = calculateStepsPerMinute(stepsDelta, timeDelta);
                if (currentStepsPerMinute > maxStepsPerMinute) {
                    maxStepsPerMinute = currentStepsPerMinute;
                    maxStepsResetCounter = 0;
                }
                steps += stepsDelta;
                lastTimestamp = timestamp;
            } catch (Exception ex) {
                GB.toast(LiveActivityFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT, GB.ERROR, ex);
            }
        }

        private int calculateStepsPerMinute(int stepsDelta, int seconds) {
            if (stepsDelta == 0) {
                return 0; // not walking or not enough data per mills?
            }
            if (seconds <= 0) {
                throw new IllegalArgumentException("delta in seconds is <= 0 -- time change?");
            }

            int oneMinute = 60;
            float factor = oneMinute / seconds;
            int result = (int) (stepsDelta * factor);
            if (result > MAX_STEPS_PER_MINUTE) {
                // ignore, return previous value instead
                result = lastStepsPerMinute;
            }
            return result;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case DeviceService.ACTION_REALTIME_SAMPLES: {
                    ActivitySample sample = (ActivitySample) intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE);
                    addSample(sample);
                    break;
                }
            }
        }
    };

    private void addSample(ActivitySample sample) {
        int heartRate = sample.getHeartRate();
        int timestamp = tsTranslation.shorten(sample.getTimestamp());
        if (isValidHeartRateValue(heartRate)) {
            setCurrentHeartRate(heartRate, timestamp);
        }
        int steps = sample.getSteps();
        if (steps != ActivitySample.NOT_MEASURED) {
            addEntries(steps, timestamp);
        }
    }

    private int translateTimestampFrom(Intent intent) {
        return translateTimestamp(intent.getLongExtra(DeviceService.EXTRA_TIMESTAMP, System.currentTimeMillis()));
    }

    private int translateTimestamp(long tsMillis) {
        int timestamp = (int) (tsMillis / 1000); // translate to seconds
        return tsTranslation.shorten(timestamp); // and shorten
    }

    private void setCurrentHeartRate(int heartRate, int timestamp) {
        addHistoryDataSet(true);
        mHeartRate = heartRate;
    }

    private int getCurrentHeartRate() {
        int result = mHeartRate;
        mHeartRate = -1;
        return result;
    }

    private void addEntries(int steps, int timestamp) {
        mSteps.updateCurrentSteps(steps, timestamp);
        if (++maxStepsResetCounter > RESET_COUNT) {
            maxStepsResetCounter = 0;
            mSteps.maxStepsPerMinute = 0;
        }
        // Or: count down the steps until goal reached? And then flash GOAL REACHED -> Set stretch goal
        LOG.info("Steps: " + steps + ", total: " + mSteps.getTotalSteps() + ", current: " + mSteps.getStepsPerMinute(false));

//        addEntries();
    }

    private void addEntries(int timestamp) {
        mTotalStepsChart.setSingleEntryYValue(mSteps.getTotalSteps());
        YAxis stepsPerMinuteCurrentYAxis = mStepsPerMinuteCurrentChart.getAxisLeft();
        int maxStepsPerMinute = mSteps.getMaxStepsPerMinute();
//        int extraRoom = maxStepsPerMinute/5;
//        buggy in MPAndroidChart? Disable.
//        stepsPerMinuteCurrentYAxis.setAxisMaxValue(Math.max(MIN_STEPS_PER_MINUTE, maxStepsPerMinute + extraRoom));
        LimitLine target = new LimitLine(maxStepsPerMinute);
        stepsPerMinuteCurrentYAxis.removeAllLimitLines();
        stepsPerMinuteCurrentYAxis.addLimitLine(target);

        int stepsPerMinute = mSteps.getStepsPerMinute(true);
        mStepsPerMinuteCurrentChart.setSingleEntryYValue(stepsPerMinute);

        if (!addHistoryDataSet(false)) {
            return;
        }

        ChartData data = mStepsPerMinuteHistoryChart.getData();
        if (stepsPerMinute < 0) {
            stepsPerMinute = 0;
        }
        mHistorySet.addEntry(new Entry(timestamp, stepsPerMinute));
        int hr = getCurrentHeartRate();
        if (hr < 0) {
            hr = 0;
        }
        mHeartRateSet.addEntry(new Entry(timestamp, hr));
    }

    private boolean addHistoryDataSet(boolean force) {
        if (mStepsPerMinuteHistoryChart.getData() == null) {
            // ignore the first default value to keep the "no-data-description" visible
            if (force || mSteps.getTotalSteps() > 0) {
                LineData data = new LineData();
                data.addDataSet(mHistorySet);
                data.addDataSet(mHeartRateSet);
                mStepsPerMinuteHistoryChart.setData(data);
                return true;
            }
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        heartRateValues = new ArrayList<>();
        tsTranslation = new TimestampTranslation();

        View rootView = inflater.inflate(R.layout.fragment_live_activity, container, false);

        mStepsPerMinuteCurrentChart = (CustomBarChart) rootView.findViewById(R.id.livechart_steps_per_minute_current);
        mTotalStepsChart = (CustomBarChart) rootView.findViewById(R.id.livechart_steps_total);
        mStepsPerMinuteHistoryChart = (BarLineChartBase) rootView.findViewById(R.id.livechart_steps_per_minute_history);

        totalStepsEntry = new BarEntry(1, 0);
        stepsPerMinuteEntry = new BarEntry(1, 0);

        mStepsPerMinuteData = setupCurrentChart(mStepsPerMinuteCurrentChart, stepsPerMinuteEntry, getString(R.string.live_activity_current_steps_per_minute));
        mTotalStepsData = setupTotalStepsChart(mTotalStepsChart, totalStepsEntry, getString(R.string.live_activity_total_steps));
        setupHistoryChart(mStepsPerMinuteHistoryChart);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filterLocal);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopActivityPulse();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ScheduledExecutorService startActivityPulse() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = LiveActivityFragment.this.getActivity();
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pulse();
                        }
                    });
                }
            }
        }, 0, getPulseIntervalMillis(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void stopActivityPulse() {
        if (pulseScheduler != null) {
            pulseScheduler.shutdownNow();
            pulseScheduler = null;
        }
    }

    /**
     * Called in the UI thread.
     */
    private void pulse() {
        addEntries(translateTimestamp(System.currentTimeMillis()));

        LineData historyData = (LineData) mStepsPerMinuteHistoryChart.getData();
        if (historyData == null) {
            return;
        }

        historyData.notifyDataChanged();
        mTotalStepsData.notifyDataSetChanged();
        mStepsPerMinuteData.notifyDataSetChanged();
        mStepsPerMinuteHistoryChart.notifyDataSetChanged();

        renderCharts();

        // have to enable it again and again to keep it measureing
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(true);
    }

    private int getPulseIntervalMillis() {
        return 1000;
    }

    @Override
    protected void onMadeVisibleInActivity() {
        GBApplication.deviceService().onEnableRealtimeSteps(true);
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(true);
        super.onMadeVisibleInActivity();
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        pulseScheduler = startActivityPulse();
    }

    @Override
    protected void onMadeInvisibleInActivity() {
        stopActivityPulse();
        GBApplication.deviceService().onEnableRealtimeSteps(false);
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(false);
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onMadeInvisibleInActivity();
    }

    @Override
    public void onDestroyView() {
        onMadeInvisibleInActivity();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    private BarDataSet setupCurrentChart(CustomBarChart chart, BarEntry entry, String title) {
        mStepsPerMinuteCurrentChart.getAxisLeft().setAxisMaxValue(MAX_STEPS_PER_MINUTE);
        return setupCommonChart(chart, entry, title);
    }

    private BarDataSet setupCommonChart(CustomBarChart chart, BarEntry entry, String title) {
        chart.setSinglAnimationEntry(entry);

//        chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setEnabled(false);
        chart.getXAxis().setTextColor(CHART_TEXT_COLOR);
        chart.getAxisLeft().setTextColor(CHART_TEXT_COLOR);

        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.getDescription().setTextColor(DESCRIPTION_COLOR);
        chart.getDescription().setText(title);
//        chart.setNoDataTextDescription("");
        chart.setNoDataText("");
        chart.getAxisRight().setEnabled(false);

        List<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        entries.add(new BarEntry(0, 0));
        entries.add(entry);
        entries.add(new BarEntry(2, 0));
        colors.add(akActivity.color);
        colors.add(akActivity.color);
        colors.add(akActivity.color);
//        //we don't want labels
//        xLabels.add("");
//        xLabels.add("");
//        xLabels.add("");

        BarDataSet set = new BarDataSet(entries, "");
        set.setDrawValues(false);
        set.setColors(colors);
        BarData data = new BarData(set);
//        data.setGroupSpace(0);
        chart.setData(data);

        chart.getLegend().setEnabled(false);

        return set;
    }

    private BarDataSet setupTotalStepsChart(CustomBarChart chart, BarEntry entry, String label) {
        mTotalStepsChart.getAxisLeft().setAxisMaximum(5000); // TODO: use daily goal - already reached steps
        return setupCommonChart(chart, entry, label); // at the moment, these look the same
    }

    private void setupHistoryChart(BarLineChartBase chart) {
        configureBarLineChartDefaults(chart);

        chart.setTouchEnabled(false); // no zooming or anything, because it's updated all the time
        chart.setBackgroundColor(BACKGROUND_COLOR);
        chart.getDescription().setTextColor(DESCRIPTION_COLOR);
        chart.getDescription().setText(getString(R.string.live_activity_steps_per_minute_history));
        chart.setNoDataText(getString(R.string.live_activity_start_your_activity));
        chart.getLegend().setEnabled(false);
        Paint infoPaint = chart.getPaint(Chart.PAINT_INFO);
        infoPaint.setTextSize(Utils.convertDpToPixel(20f));
        infoPaint.setFakeBoldText(true);
        chart.setPaint(infoPaint, Chart.PAINT_INFO);

        XAxis x = chart.getXAxis();
        x.setDrawLabels(true);
        x.setDrawGridLines(false);
        x.setEnabled(true);
        x.setTextColor(CHART_TEXT_COLOR);
        x.setDrawLimitLinesBehindData(true);

        YAxis y = chart.getAxisLeft();
        y.setDrawGridLines(false);
        y.setDrawTopYLabelEntry(false);
        y.setTextColor(CHART_TEXT_COLOR);
        y.setEnabled(true);
        y.setAxisMinimum(0);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(true);
        yAxisRight.setDrawLabels(true);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setTextColor(CHART_TEXT_COLOR);
        yAxisRight.setAxisMaximum(HeartRateUtils.MAX_HEART_RATE_VALUE);
        yAxisRight.setAxisMinimum(HeartRateUtils.MIN_HEART_RATE_VALUE);

        mHistorySet = new LineDataSet(new ArrayList<Entry>(), getString(R.string.live_activity_steps_history));
        mHistorySet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mHistorySet.setColor(akActivity.color);
        mHistorySet.setDrawCircles(false);
        mHistorySet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        mHistorySet.setDrawFilled(true);
        mHistorySet.setDrawValues(false);

        mHeartRateSet = createHeartrateSet(new ArrayList<Entry>(), getString(R.string.live_activity_heart_rate));
        mHeartRateSet.setDrawValues(false);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.liveactivity_live_activity);
    }

    @Override
    protected void showDateBar(boolean show) {
        // never show the data bar
        super.showDateBar(false);
    }

    @Override
    protected void refresh() {
        // do nothing, we don't have any db interaction
    }

    @Override
    protected ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateChartsnUIThread(ChartsData chartsData) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void renderCharts() {
        mStepsPerMinuteCurrentChart.animateY(150);
        mTotalStepsChart.animateY(150);
        mStepsPerMinuteHistoryChart.invalidate();
    }

    @Override
    protected List<ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        throw new UnsupportedOperationException("no db access supported for live activity");
    }

    @Override
    protected void setupLegend(Chart chart) {
        // no legend
    }
}
