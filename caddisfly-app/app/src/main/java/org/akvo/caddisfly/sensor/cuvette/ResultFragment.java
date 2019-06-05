/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.sensor.cuvette;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.akvo.caddisfly.BuildConfig;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.databinding.FragmentResult2Binding;
import org.akvo.caddisfly.model.Result;
import org.akvo.caddisfly.model.TestInfo;

import static org.akvo.caddisfly.common.ConstantKey.IS_INTERNAL;
import static org.akvo.caddisfly.common.ConstantKey.TEST_INFO;

public class ResultFragment extends Fragment {

    private FragmentResult2Binding b;

    /**
     * Get the instance.
     */
    public static ResultFragment newInstance(TestInfo testInfo, boolean isInternal) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(TEST_INFO, testInfo);
        args.putBoolean(IS_INTERNAL, isInternal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(inflater,
                R.layout.fragment_result_2, container, false);
        View view = b.getRoot();

        if (!BuildConfig.showExperimentalTests) {
            b.buttonSendToServer.setVisibility(View.GONE);
        }

        b.buttonAccept.setVisibility(View.GONE);

        if (getArguments() != null) {
            TestInfo testInfo = getArguments().getParcelable(TEST_INFO);
            if (testInfo != null) {
                setInfo(testInfo);
            }
        }

        return view;
    }

    public void setInfo(TestInfo testInfo) {
        Result result = testInfo.getResults().get(0);

        b.textResult.setText(result.getResult());
        b.textTitle.setText(testInfo.getName());
        b.textDilution.setText(getResources().getQuantityString(R.plurals.dilutions,
                testInfo.getDilution(), testInfo.getDilution()));
        b.textUnit.setText(result.getUnit());

        if (testInfo.getDilution() == testInfo.getMaxDilution()) {
            b.textDilutionInfo.setVisibility(View.GONE);
        } else if (result.highLevelsFound()) {
            b.textDilutionInfo.setVisibility(View.VISIBLE);
        } else {
            b.textDilutionInfo.setVisibility(View.GONE);
        }
    }

}
