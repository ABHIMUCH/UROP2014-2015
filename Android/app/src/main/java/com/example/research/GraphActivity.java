package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import net.sf.javaml.classification.Classifier;

import java.util.ArrayList;
import java.util.Vector;


public class GraphActivity extends Activity {

    public static Vector<String> last11;
    public static Vector<Classifier> SVMs;
    public static SVMMethods methodObject;
    public static setsMeanStdDev holdInfo;
    public static ArrayList<String> result;

    // private static GraphView graph;
    private static LineChart graph;
    private static ArrayList<LineDataSet> dataSets;
    private static LineDataSet set1;
    private static Handler UIHandler;
    private static LineData linedata;
    private static final int SETTINGS_RESULT = 1;
    private static TextView titleTextView;
    private static TextView titleTextView2;

    public static SharedPreferences sharedPrefs;
    public static int HIGH;
    public static int LOW;
    public static float YMAX;
    public static float YMIN;
    public static String PATIENTNAME;
    public static String PHONENUMBER;
    public static boolean TWILIOALERTS;
    public static boolean YOALERTS;

    /*
    MainActivity used to be the "base" class of the program, but due to the fact that it is easier
    to have user interaction on one screen instead of flipping through menus to start the program,
    this is now the new base class of the program.

    That makes MainActivity's role to basically just to start this Activity and close itself.
    It's a bit redundant, but it's to keep the old code around (commented out) for reference.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Various menu items pressed
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Settings button pressed
                Intent settingsIntent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_refresh:
                // TODO Implement refreshing data through refresh button
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View the Graph activity.
        setContentView(R.layout.activity_graph);

        /*
        Here we read from the preferences file instead of the old method of manually entering
        every time the app was opened.
        TODO: Implement exception and error checks for ParseInt.
         */
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        LOW = Integer.parseInt(sharedPrefs.getString("lowbs", "100"));
        HIGH = Integer.parseInt(sharedPrefs.getString("highbs", "180"));
        YMAX = Float.parseFloat(sharedPrefs.getString("ymax", "300"));
        YMIN = Float.parseFloat(sharedPrefs.getString("ymin", "0"));
        PATIENTNAME = sharedPrefs.getString("patientname", "NULLNAME");
        PHONENUMBER = sharedPrefs.getString("phonenumber", "1234567890");
        TWILIOALERTS = sharedPrefs.getBoolean("twilioalerts", false);
        YOALERTS = sharedPrefs.getBoolean("yoalerts", false);

        // Set the size of the TextViews at the top.
        titleTextView = (TextView) findViewById(R.id.titleTextViewLayout);
        titleTextView2 = (TextView) findViewById(R.id.titleTextViewLayout2);
        titleTextView.setTextSize(12);
        titleTextView2.setTextSize(20);
        titleTextView2.setShadowLayer(1,0,0,Color.BLACK);
        titleTextView.setText("Blood Glucose Warning Level:");


        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

        //MPAndroidChart commands
        graph = (LineChart) findViewById(R.id.graph);
        graph.setDrawGridBackground(false);
        graph.setNoDataTextDescription("Graph is loading, please wait...");
        graph.setDescription("Measured blood glucose over the last hour");
        // Set Upper / Lower limit lines
        LimitLine upperlimitline = new LimitLine((float) HIGH, "High Glucose Limit");
        upperlimitline.setLineWidth(4f);
        upperlimitline.enableDashedLine(10f, 10f, 0f);
        upperlimitline.setLabelPosition(LimitLine.LimitLabelPosition.POS_RIGHT);
        upperlimitline.setTextSize(10f);
        LimitLine lowerlimitline = new LimitLine((float) LOW, "Low Glucose Limit");
        lowerlimitline.setLineWidth(4f);
        lowerlimitline.enableDashedLine(10f, 10f, 0f);
        lowerlimitline.setLabelPosition(LimitLine.LimitLabelPosition.POS_RIGHT);
        lowerlimitline.setTextSize(10f);
        graph.setHighlightEnabled(false);
        YAxis leftAxis = graph.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upperlimitline);
        leftAxis.addLimitLine(lowerlimitline);
        leftAxis.setAxisMaxValue(YMAX);
        leftAxis.setAxisMinValue(YMIN);
        leftAxis.setStartAtZero(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);
        graph.getAxisRight().setEnabled(false);
        graph.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        graph.invalidate();

        // graph_init();

        result = new ArrayList<String>();
        WakefulBroadcastReceiver.startWakefulService(getApplicationContext());
    }

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_graph, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static void graph(final double data[], final int alertVal) {
        runOnUI(new Runnable() {
            public void run() {

                /*
                Graph[] is passed an array of data[] and an alertVal integer.
                The index of data[] (which is 13 long) is the X-axis variable,
                and the corresponding value is the Y-axis variable.
                 */

                /*
                For some reason, data[12] is a weird value which should be data[0].
                Therefore, as a temporary fix, we will read data[1-11] and then read data[0].
                TODO: Properly fix program logic to output correct graphing data.
                 */
                ArrayList<String> xValues = new ArrayList<String>();
                ArrayList<Entry> yValues = new ArrayList<Entry>();

                for (int i = 0; i < data.length; i++) {
                    Log.d("Data Debug: ", Integer.toString(i) + ": " + Double.toString(data[i]));
                }
                // Add x-values
                for (int i = 0; i < 13; i++) {
                    xValues.add((i) + "");
                }

                // Add y-values from data[1-11]
                for (int i = 1; i <= 11; i++) {
                    yValues.add(new Entry((float) data[i], i - 1));
                }
                yValues.add(new Entry((float) data[0], 12 - 1));

                // Create a new data set (i.e. Y-values + Line properties)
                LineDataSet ds1 = new LineDataSet(yValues, "");
                ds1.enableDashedLine(10f, 5f, 0f);
                ds1.setColor(Color.BLACK);
                ds1.setCircleColor(Color.BLACK);
                ds1.setLineWidth(2f);
                ds1.setCircleSize(5f);
                ds1.setDrawCircleHole(true);
                ds1.setValueTextSize(9f);
                ds1.setFillAlpha(65);
                ds1.setFillColor(Color.BLACK);

                dataSets = new ArrayList<LineDataSet>();
                dataSets.add(ds1); // Add the line data set

                // Now, graph everything and refresh with invalidate();
                linedata = new LineData(xValues, dataSets);
                graph.setData(linedata);
                graph.invalidate();

                // Now set the TextView object:

                if (alertVal == 1) {
                    // Warning level CRITICAL
                    titleTextView2.setText("CRITICAL");
                    titleTextView2.setTextColor(Color.RED);
                } else if (alertVal == -1) {
                    // Warning level CAUTION
                    titleTextView2.setText("CAUTION");
                    titleTextView2.setTextColor(Color.YELLOW);
                } else {
                    // Warning level GOOD
                    titleTextView2.setText("GOOD");
                    titleTextView2.setTextColor(Color.GREEN);
                }

                /*
                Depreciated GraphView Code
                PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(displayvals);


                graph.removeAllSeries();
                graph.addSeries(series);

                if (alertVal == 1)
                    graph.getViewport().setBackgroundColor(Color.RED);
                else if (alertVal == -1) {
                    graph.getViewport().setBackgroundColor(Color.YELLOW);
                } else {
                    graph.getViewport().setBackgroundColor(Color.GREEN);
                }
                */
            }
        });
    }
}

