package org.akvo.caddisfly.sensor.colorimetry.strip.util.calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markwestra on 01/08/2015
 */
public class CalibrationData {
    public final Map<String, Location> locations;
    public final Map<String, CalValue> calValues;
    public final List<WhiteLine> whiteLines;
    public final double[] stripArea;
    public int hSizePixel;
    public int vSizePixel;
    public String cardVersion;
    public String date;
    public String unit;
    public double patchSize;
    public double hSize;
    public double vSize;

    public CalibrationData() {
        this.locations = new HashMap<>();
        this.calValues = new HashMap<>();
        this.whiteLines = new ArrayList<>();
        this.stripArea = new double[4];
    }

    public void addLocation(String label, Double x, Double y, Boolean grayPatch) {
        Location loc = new Location(x, y, grayPatch);
        this.locations.put(label, loc);
    }

    public void addCal(String label, double CIE_L, double CIE_A, double CIE_B) {
        CalValue calVal = new CalValue(CIE_L, CIE_A, CIE_B);
        this.calValues.put(label, calVal);
    }

    public void addWhiteLine(Double x1, Double y1, Double x2, Double y2, Double width) {
        WhiteLine line = new WhiteLine(x1, y1, x2, y2, width);
        this.whiteLines.add(line);
    }

    public class Location {
        public final Double x;
        public final Double y;
        public final Boolean grayPatch;

        public Location(Double x, Double y, Boolean grayPatch) {
            this.x = x;
            this.y = y;
            this.grayPatch = grayPatch;
        }
    }

    public class CalValue {
        public final double CIE_L;
        public final double CIE_A;
        public final double CIE_B;

        public CalValue(double CIE_L, double CIE_A, double CIE_B) {
            this.CIE_L = CIE_L;
            this.CIE_A = CIE_A;
            this.CIE_B = CIE_B;

        }
    }

    public class WhiteLine {
        public final Double[] p;
        public final Double width;

        public WhiteLine(Double x1, Double y1, Double x2, Double y2, Double width) {
            Double[] pArray = new Double[4];
            pArray[0] = x1;
            pArray[1] = y1;
            pArray[2] = x2;
            pArray[3] = y2;
            this.p = pArray;
            this.width = width;
        }
    }
}