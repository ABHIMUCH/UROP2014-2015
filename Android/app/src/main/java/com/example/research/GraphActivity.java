package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.sf.javaml.classification.Classifier;

import java.util.ArrayList;
import java.util.Vector;


public class GraphActivity extends Activity {

    public static Vector<String> last11;
    public static Vector<Classifier> SVMs;
    public static SVMMethods methodObject;
    public static setsMeanStdDev holdInfo;
    public static ArrayList<String> result;

    private static GraphView graph;
    private static Handler UIHandler;
    private static final int SETTINGS_RESULT = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
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
        setContentView(R.layout.activity_graph);

        last11 = new Vector<String>();
        SVMs = new Vector<Classifier>();
        methodObject = new SVMMethods();

        graph = (GraphView) findViewById(R.id.graph);
        graph_init();

        result = new ArrayList<String>();

        WakefulBroadcastReceiver.startWakefulService(getApplicationContext());
    }

    private void graph_init() {
        graph.setTitle("Blood Glucose (mg/dl) over the last hour");

        Viewport display = graph.getViewport();
        display.setXAxisBoundsManual(true);
        display.setYAxisBoundsManual(true);
        display.setMaxX(60);
        display.setMaxY(300);
        display.setBackgroundColor(Color.GREEN);

        GridLabelRenderer labels = new GridLabelRenderer(graph);
        labels.setHorizontalLabelsVisible(true);
        labels.setVerticalLabelsVisible(true);
        labels.setVerticalAxisTitleTextSize(20);
        labels.setHorizontalAxisTitle("Last Hour");
        labels.setVerticalAxisTitle("Blood Glucose (mg/dl)");
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

                DataPoint displayvals[] = new DataPoint[12];
                for (int i = 0; i < 12; i++) {
                    displayvals[i] = new DataPoint(i * 5, data[i]);
                }

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
            }
        });
    }

}

