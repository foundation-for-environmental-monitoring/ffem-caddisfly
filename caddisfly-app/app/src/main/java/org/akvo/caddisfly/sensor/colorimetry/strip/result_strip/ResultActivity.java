package org.akvo.caddisfly.sensor.colorimetry.strip.result_strip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.sensor.SensorConstants;
import org.akvo.caddisfly.sensor.colorimetry.strip.BaseActivity;
import org.akvo.caddisfly.sensor.colorimetry.strip.ColorimetryStripActivity;
import org.akvo.caddisfly.sensor.colorimetry.strip.colorimetry_strip.StripTest;
import org.akvo.caddisfly.sensor.colorimetry.strip.ui.CircleView;
import org.akvo.caddisfly.sensor.colorimetry.strip.util.Constant;
import org.akvo.caddisfly.sensor.colorimetry.strip.util.FileStorage;
import org.akvo.caddisfly.sensor.colorimetry.strip.util.color.ColorDetected;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ResultActivity extends BaseActivity {

    private final JSONObject resultJsonObj = new JSONObject();
    private final JSONArray resultJsonArr = new JSONArray();
    private Mat resultImage = null;
    private FileStorage fileStorage;
    private String resultImageUrl;
    private StripTest.Brand brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (savedInstanceState == null) {
            resultImageUrl = UUID.randomUUID().toString() + ".png";
            Intent intent = getIntent();
            fileStorage = new FileStorage(this);
            String brandName = intent.getStringExtra(Constant.BRAND);

            Mat strip;
            StripTest stripTest = new StripTest();

            // get information on the strip test from JSON
            brand = stripTest.getBrand(brandName);
            List<StripTest.Brand.Patch> patches = brand.getPatches();

            // get the JSON describing the images of the patches that were stored before
            JSONArray imagePatchArray = null;
            try {
                String json = fileStorage.readFromInternalStorage(Constant.IMAGE_PATCH + ".txt");
                if (json != null) {
                    imagePatchArray = new JSONArray(json);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // cycle over the patches and interpret them
            if (imagePatchArray != null) {
                // if this strip is of type 'GROUP', take the first image and use that for all the patches
                System.out.println("*** grouping:" + brand.getGroupingType());
                if (brand.getGroupingType() == StripTest.groupType.GROUP) {
                    // handle grouping case

                    // get the first patch image
                    JSONArray array;
                    try {
                        // get strip image into Mat object
                        array = imagePatchArray.getJSONArray(0);
                        int imageNo = array.getInt(0);
                        boolean isInvalidStrip = fileStorage.checkIfFilenameContainsString(Constant.STRIP + imageNo + Constant.ERROR);
                        strip = ResultUtils.getMatFromFile(fileStorage, imageNo);
                        if (strip != null) {
                            // create empty mat to serve as a template
                            resultImage = new Mat(0, strip.cols(), CvType.CV_8UC3, new Scalar(255, 255, 255));
                            new BitmapTask(isInvalidStrip, strip, true, brand, patches, 0).execute(strip);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // if this strip is of type 'INDIVIDUAL' handle patch by patch
                    for (int i = 0; i < patches.size(); i++) { // handle patch
                        JSONArray array;
                        try {
                            array = imagePatchArray.getJSONArray(i);

                            // get the image number from the json array
                            int imageNo = array.getInt(0);
                            boolean isInvalidStrip = fileStorage.checkIfFilenameContainsString(Constant.STRIP + imageNo + Constant.ERROR);

                            // read strip from file
                            strip = ResultUtils.getMatFromFile(fileStorage, imageNo);

                            if (strip != null) {
                                if (i == 0) {
                                    // create empty mat to serve as a template
                                    resultImage = new Mat(0, strip.cols(), CvType.CV_8UC3, new Scalar(255, 255, 255));
                                }
                                new BitmapTask(isInvalidStrip, strip, false, brand, patches, i).execute(strip);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                TextView textView = new TextView(this);
                textView.setText(R.string.noData);
                LinearLayout layout = (LinearLayout) findViewById(R.id.activity_resultLinearLayout);

                layout.addView(textView);
            }
        }
        Button save = (Button) findViewById(R.id.activity_resultButtonSave);
        Button redo = (Button) findViewById(R.id.activity_resultButtonRedo);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = "";
                try {
                    // store image on sd card
                    path = FileStorage.writeBitmapToExternalStorage(ResultUtils.makeBitmap(resultImage), "/result-images", resultImageUrl);

                    resultJsonObj.put(SensorConstants.TYPE, SensorConstants.TYPE_NAME);
                    resultJsonObj.put(SensorConstants.NAME, brand.getName());
                    resultJsonObj.put(SensorConstants.UUID, brand.getUuid());
                    if (path.length() > 0) {
                        resultJsonObj.put(SensorConstants.IMAGE, resultImageUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onResult(resultJsonObj.toString(), path);

                Intent i = new Intent(v.getContext(), ColorimetryStripActivity.class);
                i.putExtra(SensorConstants.FINISH, true);
                i.putExtra(SensorConstants.RESPONSE, resultJsonObj.toString());
                i.putExtra(SensorConstants.IMAGE, path);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

                finish();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileStorage != null) {
                    fileStorage.deleteFromInternalStorage(Constant.INFO);
                    fileStorage.deleteFromInternalStorage(Constant.DATA);
                    fileStorage.deleteFromInternalStorage(Constant.STRIP);
                }

                Intent intentRedo = new Intent(getBaseContext(), ColorimetryStripActivity.class);
                intentRedo.putExtra(SensorConstants.FINISH, true);
                intentRedo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intentRedo);
                finish();
            }
        });
    }

    private class BitmapTask extends AsyncTask<Mat, Void, Void> {
        private final boolean invalid;
        private final Boolean grouped;
        private final StripTest.Brand brand;
        private final List<StripTest.Brand.Patch> patches;
        private final int patchNum;
        private final Mat strip;
        String unit;
        int id;
        String desc;
        private Bitmap stripBitmap = null;
        private Mat combined;
        private ColorDetected colorDetected;
        private ColorDetected[] colorsDetected;
        private double ppm = -1;

        public BitmapTask(boolean invalid, Mat strip, Boolean grouped, StripTest.Brand brand,
                          List<StripTest.Brand.Patch> patches, int patchNum) {
            this.invalid = invalid;
            this.grouped = grouped;
            this.strip = strip;
            this.brand = brand;
            this.patches = patches;
            this.patchNum = patchNum;
        }

        @Override
        protected Void doInBackground(Mat... params) {
            Mat mat = params[0];
            int subMatSize = 7;
            int borderSize = (int) Math.ceil(mat.height() * 0.5);

            if (mat.empty() || mat.height() < subMatSize) {
                return null;
            }

            if (invalid) {
                //System.out.println("***invalid mat object***");
                if (!mat.empty()) {
                    //done with lab schema, make rgb to show in image view
                    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_Lab2RGB);
                    stripBitmap = ResultUtils.makeBitmap(mat);
                }
                return null;
            }

            // get the name and unit of the patch
            desc = patches.get(patchNum).getDesc();
            unit = patches.get(patchNum).getUnit();
            id = patches.get(patchNum).getId();

            // depending on the boolean grouped, we either handle all patches at once, or we handle only a single one

            JSONArray colours;
            Point centerPatch = null;

            double xTranslate;

            // compute location of point to be sampled
            if (grouped) {
                // collect colours
                double ratioW = strip.width() / brand.getStripLength();
                colorsDetected = new ColorDetected[patches.size()];
                double[][] colorsValueLab = new double[patches.size()][3];
                for (int p = 0; p < patches.size(); p++) {
                    double x = patches.get(p).getPosition() * ratioW;
                    double y = strip.height() / 2;
                    centerPatch = new Point(x, y);

                    colorDetected = ResultUtils.getPatchColour(mat, centerPatch, subMatSize);
                    double[] colorValueLab = colorDetected.getLab().val;

                    colorsDetected[p] = colorDetected;
                    colorsValueLab[p] = colorValueLab;
                }

                try {
                    ppm = ResultUtils.calculatePpmGroup(colorsValueLab, patches);
                } catch (Exception e) {
                    e.printStackTrace();
                    ppm = Double.NaN;
                }

                // calculate size of each color range block
                // divide the original strip width by the number of colours
                colours = patches.get(0).getColours();
                xTranslate = (double) mat.cols() / (double) colours.length();

            } else {
                double ratioW = strip.width() / brand.getStripLength();
                double x = patches.get(patchNum).getPosition() * ratioW;
                double y = strip.height() / 2;
                centerPatch = new Point(x, y);

                colorDetected = ResultUtils.getPatchColour(mat, centerPatch, subMatSize);
                double[] colorValueLab = colorDetected.getLab().val;

                //set the colours needed to calculate ppm
                colours = patches.get(patchNum).getColours();

                try {
                    ppm = ResultUtils.calculatePpmSingle(colorValueLab, colours);
                } catch (Exception e) {
                    e.printStackTrace();
                    ppm = Double.NaN;
                }

                // calculate size of each color range block
                // divide the original strip width by the number of colours
                xTranslate = (double) mat.cols() / (double) colours.length();
            }

            ////////////// Create Image ////////////////////

            // create Mat to hold strip itself
            mat = ResultUtils.createStripMat(mat, borderSize, centerPatch, grouped);

            // Create Mat to hold description of patch
            Mat descMat = ResultUtils.createDescriptionMat(desc, mat.cols());

            // Create Mat to hold the colour range
            Mat colorRangeMat;
            if (grouped) {
                colorRangeMat = ResultUtils.createColourRangeMatGroup(patches, mat.cols(), xTranslate);
            } else {
                colorRangeMat = ResultUtils.createColourRangeMatSingle(patches, patchNum, mat.cols(), xTranslate);
            }


            // create Mat to hold value measured
            Mat valueMeasuredMat;
            if (grouped) {
                valueMeasuredMat = ResultUtils.createValueMeasuredMatGroup(colours, ppm, colorsDetected, mat.cols(), xTranslate);
            } else {
                valueMeasuredMat = ResultUtils.createValueMeasuredMatSingle(colours, ppm, colorDetected, mat.cols(), xTranslate);
            }

            // PUTTING IT ALL TOGETHER
            // transform all mats to RGB. The strip Mat is already RGB
            Imgproc.cvtColor(descMat, descMat, Imgproc.COLOR_Lab2RGB);
            Imgproc.cvtColor(colorRangeMat, colorRangeMat, Imgproc.COLOR_Lab2RGB);
            Imgproc.cvtColor(valueMeasuredMat, valueMeasuredMat, Imgproc.COLOR_Lab2RGB);

            // create empty mat to serve as a template
            combined = new Mat(0, mat.cols(), CvType.CV_8UC3, new Scalar(255, 255, 255));

            combined = ResultUtils.concatenate(combined, mat); // add strip
            combined = ResultUtils.concatenate(combined, colorRangeMat); // add color range
            combined = ResultUtils.concatenate(combined, valueMeasuredMat); // add measured value

            //make bitmap to be rendered on screen, which doesn't contain the patch description
            if (!combined.empty()) {
                stripBitmap = ResultUtils.makeBitmap(combined);
            }

            //add description of patch to combined mat, at the top
            combined = ResultUtils.concatenate(descMat, combined);

            //make bitmap to be send to server
            if (!combined.empty()) {
                resultImage = ResultUtils.concatenate(resultImage, combined);
            }

            //put ppm in resultJsonArr
            if (!combined.empty()) {
                try {
                    JSONObject object = new JSONObject();
                    object.put(SensorConstants.NAME, desc);
                    object.put(SensorConstants.VALUE, ResultUtils.roundSignificant(ppm));
                    object.put(SensorConstants.UNIT, unit);
                    object.put(SensorConstants.ID, id);
                    resultJsonArr.put(object);
                    resultJsonObj.put(SensorConstants.RESULT, resultJsonArr);

                    //TESTING write image string to external storage
                    //FileStorage.writeLogToSDFile("base64.txt", img, false);
//                    FileStorage.writeLogToSDFile("json.txt", resultJsonObj.toString(), false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // End making mats to put into image to be send back as an String to server

            return null;
        }

        /*
        * Puts the result on screen.
        * data is taken from the globals stripBitmap, ppm, colorDetected and unit variables
        */
        protected void onPostExecute(Void result) {
            LayoutInflater inflater = (LayoutInflater) ResultActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final ViewGroup nullParent = null;
            LinearLayout result_ppm_layout = (LinearLayout) inflater.inflate(R.layout.result_ppm_layout, nullParent, false);

            TextView descView = (TextView) result_ppm_layout.findViewById(R.id.result_ppm_layoutDescView);
            descView.setText(desc);

            ImageView imageView = (ImageView) result_ppm_layout.findViewById(R.id.result_ppm_layoutImageView);
            CircleView circleView = (CircleView) result_ppm_layout.findViewById(R.id.result_ppm_layoutCircleView);

            if (stripBitmap != null) {
                imageView.setImageBitmap(stripBitmap);

                if (!invalid) {
                    if (colorDetected != null && !grouped) {
                        circleView.circleView(colorDetected.getColor());
                    }

                    TextView textView = (TextView) result_ppm_layout.findViewById(R.id.text_ppm_result);
                    if (ppm > -1) {
                        if (ppm < 1.0) {
                            textView.setText(String.format(Locale.getDefault(), "%.2f %s", ppm, unit));
                        } else {
                            textView.setText(String.format(Locale.getDefault(), "%.1f %s", ppm, unit));
                        }
                    }
                }
            } else {
                descView.append("\n\n" + getResources().getString(R.string.no_data));
                circleView.circleView(Color.RED);
            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.activity_resultLinearLayout);
            layout.addView(result_ppm_layout);
        }
    }
}