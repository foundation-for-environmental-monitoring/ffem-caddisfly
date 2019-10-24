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

@file:Suppress("DEPRECATION")

package org.akvo.caddisfly.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.*
import org.akvo.caddisfly.BuildConfig
import org.akvo.caddisfly.R
import org.akvo.caddisfly.R.id
import org.akvo.caddisfly.R.string
import org.akvo.caddisfly.app.CaddisflyApp
import org.akvo.caddisfly.helper.FileHelper
import org.akvo.caddisfly.helper.FileHelper.FileType
import org.akvo.caddisfly.util.TestUtil.clickListViewItem
import org.akvo.caddisfly.util.TestUtil.findButtonInScrollable
import org.akvo.caddisfly.util.TestUtil.nextSurveyPage
import org.akvo.caddisfly.util.TestUtil.sleep
import org.hamcrest.Matchers
import timber.log.Timber
import java.io.File
import java.util.*

lateinit var mDevice: UiDevice

object TestHelper {
    var mCurrentLanguage: String = "en"
    private const val TAKE_SCREENSHOTS = false
    private val STRING_HASH_MAP_EN = HashMap<String, String>()
    private val STRING_HASH_MAP_FR = HashMap<String, String>()
    private val STRING_HASH_MAP_HI = HashMap<String, String>()
    private val CALIBRATION_HASH_MAP: MutableMap<String, String> = HashMap()
    lateinit var currentHashMap: Map<String, String>

    private var mCounter = 0

    private fun addString(key: String, vararg values: String) {
        STRING_HASH_MAP_EN[key] = values[0]
        if (values.size > 1) {
            STRING_HASH_MAP_FR[key] = values[1]
        }
        if (values.size > 2) {
            STRING_HASH_MAP_HI[key] = values[2]
        }
        STRING_HASH_MAP_FR[key] = values[0]
        STRING_HASH_MAP_HI[key] = values[0]
    }

    private fun addCalibration(key: String, colors: String) {
        CALIBRATION_HASH_MAP[key] = colors
    }

    @Suppress("SameParameterValue")
    fun getString(activity: Activity?, @StringRes resourceId: Int): String {
        val currentResources: Resources? = activity!!.resources
        val assets: AssetManager? = currentResources!!.assets
        val metrics: DisplayMetrics? = currentResources.displayMetrics
        val config = Configuration(currentResources.configuration)
        config.locale = Locale(mCurrentLanguage)
        val res = Resources(assets, metrics, config)
        return res.getString(resourceId)
    }

    fun loadData(activity: Activity, languageCode: String) {
        mCurrentLanguage = languageCode
        STRING_HASH_MAP_EN.clear()
        STRING_HASH_MAP_FR.clear()
        STRING_HASH_MAP_HI.clear()
        CALIBRATION_HASH_MAP.clear()
        val currentResources: Resources? = activity.resources
        val assets: AssetManager? = currentResources!!.assets
        val metrics: DisplayMetrics? = currentResources.displayMetrics
        val config = Configuration(currentResources.configuration)
        config.locale = Locale(languageCode)
        val res = Resources(assets, metrics, config)
        addString(TestConstant.LANGUAGE, "English", "Français", "Hindi")
//        addString("otherLanguage", "Français", "English")
        addString(TestConstant.FLUORIDE, "Water - Fluoride", res.getString(string.testName))
        addString("chlorine", "Water - Free Chlorine", res.getString(string.freeChlorine))
        addString("survey", "Survey", res.getString(string.survey))
        addString("sensors", "Sensors", res.getString(string.sensors))
        addString("electricalConductivity", "Water - Electrical Conductivity", res.getString(string.electricalConductivity))
        addString("next", "Next", res.getString(string.next))
        addString(TestConstant.GO_TO_TEST, "Launch", res.getString(string.launch))
        addString("soilRange", "0 - 125 mg/kg (Up to 625+ with dilution)",
                "0 - 125 mg/kg (कमजोर पड़ने के साथ 625+ तक)")
        // Restore device-specific locale


        Resources(assets, metrics, currentResources.configuration)
        addCalibration("TestValid", "0.0=255  38  186\n"
                + "0.5=255  51  129\n"
                + "1.0=255  59  89\n"
                + "1.5=255  62  55\n"
                + "2.0=255  81  34\n")
        addCalibration("TestInvalid", "0.0=255  88  177\n"
                + "0.5=255  110  15\n"
                + "1.0=255  138  137\n"
                + "1.5=253  174  74\n"
                + "2.0=253  174  76\n"
                + "2.5=236  172  81\n"
                + "3.0=254  169  61\n")
        addCalibration("OutOfSequence", "0.0=255  38  186\n"
                + "0.5=255  51  129\n"
                + "1.0=255  62  55\n"
                + "1.5=255  59  89\n"
                + "2.0=255  81  34\n")
        addCalibration("HighLevelTest", "0.0=255  38  180\n"
                + "0.5=255  51  129\n"
                + "1.0=255  53  110\n"
                + "1.5=255  55  100\n"
                + "2.0=255  59  89\n")
        addCalibration("TestInvalid2", "0.0=255  88  47\n"
                + "0.5=255  60  37\n"
                + "1.0=255  35  27\n"
                + "1.5=253  17  17\n"
                + "2.0=254  0  0\n")
        addCalibration("LowLevelTest", "0.0=255  60  37\n"
                + "0.5=255  35  27\n"
                + "1.0=253  17  17\n"
                + "1.5=254  0  0\n"
                + "2.0=224  0  0\n")
        addCalibration("TestValidChlorine", "0.0=255  38  186\n"
                + "0.5=255  51  129\n"
                + "1.0=255  59  89\n"
                + "1.5=255  62  55\n"
                + "2.0=255  81  34\n"
                + "2.5=255  101  24\n"
                + "3.0=255  121  14\n")
        currentHashMap = when (languageCode) {
            "en" -> STRING_HASH_MAP_EN
            "hi" -> STRING_HASH_MAP_HI
            else -> STRING_HASH_MAP_FR
        }
    }

    fun takeScreenshot() {
        @Suppress("ConstantConditionIf")
        if (TAKE_SCREENSHOTS) {
            val path = File(Environment.getExternalStorageDirectory().path
                    + "/" + BuildConfig.APPLICATION_ID + "/screenshots/"
                    + "screen-" + mCounter++.toString() + "-" + mCurrentLanguage + ".png")
            mDevice.takeScreenshot(path, 0.5f, 60)
        }
    }

    fun takeScreenshot(name: String?, page: Int) {
        if (TAKE_SCREENSHOTS && VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            val path: File = if (page < 0) {
                File(Environment.getExternalStorageDirectory().path
                        + "/" + BuildConfig.APPLICATION_ID + "/screenshots/"
                        + name + "-" + mCurrentLanguage + ".png")
            } else {
                File(Environment.getExternalStorageDirectory().path
                        + "/" + BuildConfig.APPLICATION_ID + "/screenshots/"
                        + name + "-" + page.toString() + "-" + mCurrentLanguage + ".png")
            }
            mDevice.takeScreenshot(path, 0.2f, 40)
        }
    }

    fun goToMainScreen() {
        var found = false
        while (!found) {
            try {
                onView(withId(R.id.buttonSettings)).check(matches(isDisplayed()))
                found = true
            } catch (e: NoMatchingViewException) {
                Espresso.pressBack()
            }
        }
    }

    fun activateTestMode(activity: Activity?) {
        onView(withText(string.settings)).perform(click())
        onView(withText(string.about)).check(matches(isDisplayed())).perform(click())
        val version: String? = CaddisflyApp.getAppVersion(false)
        onView(withText(version)).check(matches(isDisplayed()))
        enterDiagnosticMode()
        goToMainScreen()
        onView(withText(string.settings)).perform(click())
        clickListViewItem(getString(activity, string.testModeOn))
    }

    fun clickExternalSourceButton(id: String?) {
        when (id) {
            TestConstant.WATER_FLUORIDE_ID -> {
                nextSurveyPage(3, "Water Tests 1")
                clickExternalSourceButton(2)
            }
            TestConstant.SOIL_IRON_ID -> {
                nextSurveyPage(3, "Soil Tests 2")
                clickExternalSourceButton(2)
            }
        }
    }

    fun clickExternalSourceButton(index: Int) {
        clickExternalSourceButton(index, TestConstant.GO_TO_TEST)
    }

    fun clickExternalSourceButton(index: Int, text: String?) {
        var buttonText = currentHashMap[text]
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            buttonText = buttonText!!.toUpperCase()
        }
        findButtonInScrollable(buttonText)
        val buttons: List<UiObject2?>? = mDevice.findObjects(By.text(buttonText))
        buttons!![index]!!.click()
        mDevice.waitForWindowUpdate("", 2000)
        sleep(4000)
    }

    fun clickExternalSourceButton(context: Context?, text: String?) {
        try {
            var buttonText = currentHashMap[text]
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                buttonText = buttonText!!.toUpperCase()
            }
            findButtonInScrollable(buttonText)
            mDevice.findObject(UiSelector().text(buttonText)).click()

            // New Android OS seems to popup a button for external app


            if (VERSION.SDK_INT == VERSION_CODES.M
                    && (text == TestConstant.USE_EXTERNAL_SOURCE || text == TestConstant.GO_TO_TEST)) {
                sleep(1000)
                mDevice.findObject(By.text(context!!.getString(string.appName))).click()
                sleep(1000)
            }
            mDevice.waitForWindowUpdate("", 2000)
        } catch (e: UiObjectNotFoundException) {
            Timber.e(e)
        }
    }

    fun saveCalibration(name: String?, id: String?) {
        val path: File? = FileHelper.getFilesDir(FileType.CALIBRATION, id)
        FileUtil.saveToFile(path, name, CALIBRATION_HASH_MAP[name])
    }

    fun gotoSurveyForm() {
        val context: Context? = InstrumentationRegistry.getInstrumentation().context
        val intent = context!!.packageManager.getLaunchIntentForPackage(TestConstant.EXTERNAL_SURVEY_PACKAGE_NAME)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        mDevice.waitForIdle()
        sleep(1000)
        val addButton: UiObject? = mDevice.findObject(UiSelector()
                .resourceId(TestConstant.EXTERNAL_SURVEY_PACKAGE_NAME + ":id/enter_data"))
        try {
            if (addButton!!.exists() && addButton.isEnabled) {
                addButton.click()
            }
        } catch (e: UiObjectNotFoundException) {
            Timber.e(e)
        }
        mDevice.waitForIdle()
        clickListViewItem("Automated Testing")
        mDevice.waitForIdle()
        val goToStartButton: UiObject? = mDevice.findObject(UiSelector()
                .resourceId(TestConstant.EXTERNAL_SURVEY_PACKAGE_NAME + ":id/jumpBeginningButton"))
        try {
            if (goToStartButton!!.exists() && goToStartButton.isEnabled) {
                goToStartButton.click()
            }
        } catch (e: UiObjectNotFoundException) {
            Timber.e(e)
        }
        mDevice.waitForIdle()
    }

    fun enterDiagnosticMode() {
        for (i in 0..9) {
            onView(withId(id.textVersion)).perform(click())
        }
    }

    fun leaveDiagnosticMode() {
        goToMainScreen()
        onView(withText(string.settings)).perform(click())
        onView(withId(id.disableDiagnostics)).perform(click())
//        onView(withId(id.fabDisableDiagnostics)).perform(click())
    }

    fun resetLanguage() {
        goToMainScreen()
        onView(withText(string.settings)).perform(click())
        onView(withText(string.language)).perform(click())
        onData(Matchers.hasToString(Matchers.startsWith(currentHashMap["language"]))).perform(click())
        mDevice.waitForIdle()
        goToMainScreen()
        onView(withText(string.settings)).perform(click())
        onView(withText(string.language)).perform(click())
        onData(Matchers.hasToString(Matchers.startsWith(currentHashMap["language"]))).perform(click())
        mDevice.waitForIdle()
        goToMainScreen()
    }

    fun clearPreferences(activityTestRule: ActivityTestRule<*>?) {
        val prefs: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(activityTestRule!!.activity)
        prefs!!.edit().clear().apply()
    }

    fun isDeviceInitialized(): Boolean {
        return ::mDevice.isInitialized
    }
}