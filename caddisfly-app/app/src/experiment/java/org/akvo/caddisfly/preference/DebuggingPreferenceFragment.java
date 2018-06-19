package org.akvo.caddisfly.preference;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.app.CaddisflyApp;
import org.akvo.caddisfly.common.ConstantKey;
import org.akvo.caddisfly.common.Constants;
import org.akvo.caddisfly.entity.Calibration;
import org.akvo.caddisfly.helper.CameraHelper;
import org.akvo.caddisfly.helper.ErrorMessages;
import org.akvo.caddisfly.helper.SwatchHelper;
import org.akvo.caddisfly.model.TestInfo;
import org.akvo.caddisfly.sensor.cuvette.ui.CuvetteMeasureActivity;
import org.akvo.caddisfly.sensor.cuvette.ui.CuvetteResultActivity;
import org.akvo.caddisfly.util.ListViewUtil;
import org.akvo.caddisfly.viewmodel.TestListViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebuggingPreferenceFragment extends PreferenceFragment {

    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_debugging);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_row, container, false);
        view.setBackgroundColor(Color.rgb(255, 240, 220));

        Preference sendPreference = findPreference("bluetoothSendTest");
        if (sendPreference != null) {
            sendPreference.setOnPreferenceClickListener(preference -> {
                //Only start the colorimetry calibration if the device has a camera flash
                if (CameraHelper.hasFeatureCameraFlash(getActivity(),
                        R.string.cannotStartTest, R.string.ok, null)) {

                    final TestListViewModel viewModel =
                            ViewModelProviders.of((FragmentActivity) getActivity()).get(TestListViewModel.class);

                    TestInfo testInfo = viewModel.getTestInfo(Constants.CUVETTE_BLUETOOTH_ID);

                    List<Calibration> calibrations = CaddisflyApp.getApp().getDb()
                            .calibrationDao().getAll(Constants.FLUORIDE_ID);

                    testInfo.setCalibrations(calibrations);

                    if (!SwatchHelper.isSwatchListValid(testInfo)) {
                        ErrorMessages.alertCalibrationIncomplete(getActivity(), testInfo);
                        return true;
                    }

                    final Intent intent = new Intent(getActivity(), CuvetteMeasureActivity.class);
                    intent.putExtra("internal", true);
                    intent.putExtra(ConstantKey.TEST_INFO, testInfo);
                    startActivity(intent);
                }
                return true;
            });
        }

        Preference receivePreference = findPreference("bluetoothReceiveTest");
        if (receivePreference != null) {
            receivePreference.setOnPreferenceClickListener(preference -> {
                final Intent intent = new Intent(getActivity(), CuvetteResultActivity.class);
                startActivity(intent);
                return true;
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(android.R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListViewUtil.setListViewHeightBasedOnChildren(list, 0);
    }
}