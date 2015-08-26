/*
 *  Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo Caddisfly
 *
 *  Akvo Caddisfly is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo Caddisfly is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.caddisfly.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;
import android.util.SparseIntArray;

import org.akvo.caddisfly.AppConfig;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.preference.AppPreferences;

/**
 * Manages various sounds used in the app
 */
public class SoundPoolPlayer {
    private final SparseIntArray mSounds = new SparseIntArray();
    private final Context mContext;
    private SoundPool mShortPlayer = null;

    public SoundPoolPlayer(Context context) {

        mContext = context;

        //noinspection deprecation
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_ALARM, 0);

        //beep sound for every photo taken during a test
        mSounds.put(R.raw.beep, this.mShortPlayer.load(context, R.raw.beep, 1));

        //long beep sound if the contamination in the water sample is very high
        mSounds.put(R.raw.beep_long, this.mShortPlayer.load(context, R.raw.beep_long, 1));

        //done sound when the test completes successfully
        mSounds.put(R.raw.done, this.mShortPlayer.load(context, R.raw.done, 1));

        //error sound when the test fails
        mSounds.put(R.raw.err, this.mShortPlayer.load(context, R.raw.err, 1));
    }

    /**
     * Play a short sound effect
     *
     * @param resourceId the
     */
    public void playShortResource(@RawRes int resourceId) {
        //play sound if the sound is not turned off in the preference
        if (!AppPreferences.isSoundOff(mContext)) {
            this.mShortPlayer.play(mSounds.get(resourceId), AppConfig.SOUND_VOLUME,
                    AppConfig.SOUND_VOLUME, 0, 0, 1);
        }
    }

    public void release() {
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}