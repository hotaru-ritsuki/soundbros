package ftl.anif.ua.audioanalyzer;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        try {
            plotSound();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "Sound" +
                AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private ArrayList<Integer> getAmplitude(byte a[], int limit){
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for(int i = 0;i<limit;i++){
            arr.add(Math.abs(a[i]-a[i+1]));
        }
        return arr;
    }

    private void plotSound() throws Exception{
        Log.v("My Activity",getFilename());

        byte []buf = loadRecordedSample();
        byte []kamaz = loadKamazSample();
        byte []dizel = loadDizelSample();


        int limit = 2500;

        ArrayList<Integer> bufAmplitude = getAmplitude(buf, limit);
        ArrayList<Integer> dizelAmplitude = getAmplitude(dizel, limit);
        ArrayList<Integer> kamazAmplitude = getAmplitude(kamaz, limit);

        ArrayList<Integer> bufferSpeedData = getCountedSpeed(getSpeed(bufAmplitude));
        ArrayList<Integer> kamazSpeedData = getCountedSpeed(getSpeed(kamazAmplitude));
        ArrayList<Integer> dizelSpeedData = getCountedSpeed(getSpeed(dizelAmplitude));

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.clear();
        List<Entry> bufferEntries = new ArrayList<Entry>();

        List<Entry> kamazEntries = new ArrayList<Entry>();

        List<Entry> dizelEntries = new ArrayList<Entry>();


        for(int i = 0;i<kamazSpeedData.size();i++){
            kamazEntries.add(new Entry(i+1, kamazSpeedData.get(i)));
        }

        for(int i = 0;i<bufferSpeedData.size();i++){
            bufferEntries.add(new Entry(i+1,bufferSpeedData.get(i)));
        }

        for(int i = 0;i<dizelSpeedData.size();i++){
            dizelEntries.add(new Entry(i+1,dizelSpeedData.get(i)));
        }

        LineDataSet kamazDataSet = new LineDataSet(kamazEntries, "Kamaz");

        LineDataSet bufferDataSet = new LineDataSet(bufferEntries, "Recorded Sound");

        LineDataSet dizelDataSet = new LineDataSet(dizelEntries, "Dizel");
        bufferDataSet.setColor(Color.BLACK);
        kamazDataSet.setColor(Color.GREEN);
        dizelDataSet.setColor(Color.CYAN);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        dataSets.add(kamazDataSet);
        dataSets.add(bufferDataSet);
        dataSets.add(dizelDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //chart.setData(bufferLineData);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

    }

    public void getVibroSoundPlot(View view){
        try {
            plotSound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAmplitudePlot(View view){
        plotAmplitude();
    }
    public void plotAmplitude(){
        byte []buf = loadRecordedSample();
        byte []kamaz = loadKamazSample();
        byte []dizel = loadDizelSample();


        int limit = 2500;

        ArrayList<Integer> bufAmplitude = getAmplitude(buf, limit);
        ArrayList<Integer> dizelAmplitude = getAmplitude(dizel, limit);
        ArrayList<Integer> kamazAmplitude = getAmplitude(kamaz, limit);


        ArrayList<Integer> bufferAmplitudeData = countAmplitude(bufAmplitude);

        ArrayList<Integer> kamazAmplitudeData = countAmplitude(kamazAmplitude);

        ArrayList<Integer> dizelAmplitudeData = countAmplitude(dizelAmplitude);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.clear();

        List<Entry> bufferEntries = new ArrayList<Entry>();

        List<Entry> kamazEntries = new ArrayList<Entry>();

        List<Entry> dizelEntries = new ArrayList<Entry>();


        for(int i = 0;i<kamazAmplitudeData.size();i++){
            kamazEntries.add(new BarEntry(i+1,kamazAmplitudeData.get(i)));
        }

        for(int i = 0;i<bufferAmplitudeData.size();i++){
            bufferEntries.add(new BarEntry(i+1,bufferAmplitudeData.get(i)));
        }

        for(int i = 0;i<dizelAmplitudeData.size();i++){
            dizelEntries.add(new BarEntry(i+1,dizelAmplitudeData.get(i)));
        }

        LineDataSet kamazDataSet = new LineDataSet(kamazEntries, "Kamaz");
        LineDataSet bufferDataSet = new LineDataSet(bufferEntries, "Recorded Sound");
        bufferDataSet.setDrawFilled(true);
        LineDataSet dizelDataSet = new LineDataSet(dizelEntries, "Dizel");
        bufferDataSet.setColor(Color.BLACK);
        kamazDataSet.setColor(Color.GREEN);
        dizelDataSet.setColor(Color.CYAN);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        dataSets.add(kamazDataSet);
        dataSets.add(bufferDataSet);
        dataSets.add(dizelDataSet);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //chart.setData(bufferLineData);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh
    }

    private ArrayList<Integer> countAmplitude(ArrayList<Integer> amp){
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for(int i: amp){
            arr.add(i);
        }
        ArrayList<Integer> result = new ArrayList<Integer>();

        Collections.sort(arr);
        double initialStep = (double) arr.get(arr.size()-1)/10 - 1;

        double currentStep = initialStep;

        int quantity = 0;

        int key = 0;

        for(int i = 0; i<arr.size();i++){

            if (arr.get(i) >= currentStep) {

                result.add(quantity);

                currentStep = currentStep + initialStep;

                quantity = 0;

            }else{

                quantity++;

            }
        }

        return result;
    }

    private ArrayList<Integer> getCountedSpeed(ArrayList<Float> amp){
        ArrayList<Float> arr = new ArrayList<Float>();
        for(float i: amp){
            arr.add(i);
        }
        ArrayList<Integer> result = new ArrayList<Integer>();

        Collections.sort(arr);
        double initialStep = (double) arr.get(arr.size()-1)/10 - 1;

        double currentStep = initialStep;

        int quantity = 0;

        int key = 0;

        for(int i = 0; i<arr.size();i++){

            if (arr.get(i) >= currentStep) {

                result.add(quantity);

                currentStep = currentStep + initialStep;

                quantity = 0;

            }else{

                quantity++;

            }
        }

        return result;
    }

    private ArrayList<Float> getSpeed(ArrayList<Integer> array){
        ArrayList<Float> result = new ArrayList<Float>();
        final float WAVELENGTH = 0.0002f;

        for(int i = 0;i<array.size()-1;i++){
            result.add((float)(Math.abs(array.get(i)-array.get(i+1))/WAVELENGTH));
        }

        return result;
    }

    private byte[] loadDizelSample(){
        byte buf[] = {};
        try {
            InputStream inStream = getApplicationContext().getResources().openRawResource(R.raw.dizel);
            buf = new byte[inStream.available()];
            inStream.read(buf);
            inStream.close();

        } catch (Exception e) {
            System.err.println(e);
        }
        return buf;
    }

    private byte[] loadKamazSample(){
        byte buf[] = {};
        try {
            InputStream inStream = getApplicationContext().getResources().openRawResource(R.raw.kamaz);
            buf = new byte[inStream.available()];
            inStream.read(buf);
            inStream.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return buf;
    }

    private byte[] loadRecordedSample(){
        byte buf[] = {};
        try {
            File srcFile = new File(getFilename());
            Log.v("My Activity",getFilename());
            FileInputStream in = new FileInputStream(srcFile);
            buf = new byte[in.available()];
            short[] shortArr = new short[buf.length/2];
            in.read(buf);
            in.close();

        } catch (Exception e) {
            System.err.println(e);
        }
        return buf;
    }

}
