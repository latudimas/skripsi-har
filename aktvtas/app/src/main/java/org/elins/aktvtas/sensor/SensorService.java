package org.elins.aktvtas.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.elins.aktvtas.sensor.SensorData;
import org.elins.aktvtas.sensor.SensorDataSequence;
import org.elins.aktvtas.sensor.SensorReader;

import java.util.ArrayList;
import java.util.List;

public class SensorService extends Service implements SensorReader.SensorReaderEvent {
    public static final String EXTRA_SENSOR_TO_READ = "org.elins.aktvtas.extra.EXTRA_SENSOR_TO_READ";
    public static final String EXTRA_ACTIVITY_ID = "org.elins.aktvtas.extra.EXTRA_ACTIVITY_ID";
    public static final String EXTRA_SENSOR_PLACEMENT = "org.elins.aktvtas.extra.EXTRA_SENSOR_PLACEMENT";
    public static final String EXTRA_DURATION_SECOND = "org.elins.aktvtas.extra.EXTRA_DURATION_SECOND";

    protected List<Integer> sensorToRead = new ArrayList<>();
    protected List<Integer> numberOfAxis = new ArrayList<>();

    protected String logType = "BASE";
    protected int entryCounter = 0;

    protected SensorReader sensorReader;
    protected SensorDataSequence sensorDataSequence;

    protected String filePath;
    private List<SensorData> buffer;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Should be used as base class only");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sensorDataSequence.clear();
        return false;
    }

    @Override
    public void onSensorDataReady() {
        buffer = sensorReader.read();
        if (buffer != null) {
            for (SensorData data : buffer) {
                sensorDataSequence.setData(data);
            }
            sensorDataSequence.commit();
            entryCounter++;
        }
    }

    public List<SensorData> getLastSensorData() {
        if (sensorDataSequence.size() > 0) {
            return sensorDataSequence.getLastData();
        } else {
            return new ArrayList<>();
        }
    }

    protected void extractSensorToRead(int[] sensors) {
        for (int sensor : sensors) {
            sensorToRead.add(sensor);
            numberOfAxis.add(3);
        }
    }

    protected void createSensorDataReader(List<Integer> sensorToRead) {
        sensorReader = new SensorReader(this, sensorToRead);
        sensorReader.enableEventCallback(this);
    }

    protected void createSensorDataSequence(List<Integer> sensorToRead,
                                            List<Integer> numberOfAxis) {
        sensorDataSequence = SensorDataSequence.create(sensorToRead, numberOfAxis);
    }

    public String getFilePath() {
        return filePath;
    }
}
