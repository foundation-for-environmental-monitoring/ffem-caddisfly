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

package org.akvo.caddisfly.navigation

import android.content.Intent
import android.os.Environment
import android.widget.DatePicker
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.RequiresDevice
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import org.akvo.caddisfly.BuildConfig
import org.akvo.caddisfly.R.id
import org.akvo.caddisfly.R.string
import org.akvo.caddisfly.common.TestConstants
import org.akvo.caddisfly.ui.MainActivity
import org.akvo.caddisfly.util.TestHelper
import org.akvo.caddisfly.util.TestHelper.clearPreferences
import org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton
import org.akvo.caddisfly.util.TestHelper.enterDiagnosticMode
import org.akvo.caddisfly.util.TestHelper.goToMainScreen
import org.akvo.caddisfly.util.TestHelper.gotoSurveyForm
import org.akvo.caddisfly.util.TestHelper.leaveDiagnosticMode
import org.akvo.caddisfly.util.TestHelper.loadData
import org.akvo.caddisfly.util.TestHelper.mCurrentLanguage
import org.akvo.caddisfly.util.TestHelper.saveCalibration
import org.akvo.caddisfly.util.TestHelper.takeScreenshot
import org.akvo.caddisfly.util.TestUtil
import org.akvo.caddisfly.util.TestUtil.childAtPosition
import org.akvo.caddisfly.util.TestUtil.sleep
import org.akvo.caddisfly.util.mDevice
import org.hamcrest.Matchers.*
import org.hamcrest.`object`.HasToString.hasToString
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTest {
    @JvmField
    @Rule
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        loadData(mActivityRule.activity, mCurrentLanguage)
        clearPreferences(mActivityRule)
//        resetLanguage();

    }

    @Test
    @RequiresDevice
    fun testNavigateAll() {
        saveCalibration("TestInvalid", TestConstants.CUVETTE_TEST_ID_1)
        val path = (Environment.getExternalStorageDirectory().path
                + "/" + BuildConfig.APPLICATION_ID + "/screenshots")
        val folder = File(path)
        if (!folder.exists()) {

            folder.mkdirs()
        }
        mDevice.waitForWindowUpdate("", 2000)
        goToMainScreen()

        //Main Screen


        takeScreenshot()
        onView(withText(string.settings)).perform(click())

        //Settings Screen


        takeScreenshot()
        onView(withText(string.about)).check(matches(isDisplayed())).perform(click())
        mDevice.waitForWindowUpdate("", 1000)

        //About Screen


        takeScreenshot()
        Espresso.pressBack()

//        onView(withText(R.string.language)).perform(click());

//        mDevice.waitForWindowUpdate("", 1000);

        //Language Dialog
//        takeScreenshot();

//        onView(withId(android.R.id.button2)).perform(click());


        onView(withText(string.about)).check(matches(isDisplayed())).perform(click())
        enterDiagnosticMode()
        goToMainScreen()
        onView(withText(string.calibrate)).perform(click())
        sleep(4000)
        onView(allOf(withId(id.list_types), childAtPosition(withClassName(`is`("android.widget.LinearLayout")),
                0))).perform(actionOnItemAtPosition<ViewHolder?>(
                TestConstants.TEST_INDEX, click()))
        if (TestUtil.isEmulator) {
            onView(withText(string.errorCameraFlashRequired))
                    .inRoot(withDecorView(not(`is`(mActivityRule.activity.window
                            .decorView)))).check(matches(isDisplayed()))
            return
        }
        onView(withId(id.menuLoad)).perform(click())
        sleep(2000)
        onData(hasToString(startsWith("TestInvalid"))).perform(click())
        sleep(2000)
        onView(withText(String.format("%s. %s", mActivityRule.activity
                .getString(string.calibrationIsInvalid),
                mActivityRule.activity.getString(string.tryRecalibrating)))).check(matches(isDisplayed()))
        leaveDiagnosticMode()
        sleep(4000)

        goToMainScreen()
        onView(withText(string.calibrate)).perform(click())

        //Test Types Screen


        takeScreenshot()
        onView(allOf(withId(id.list_types), childAtPosition(withClassName(`is`("android.widget.LinearLayout")),
                0))).perform(actionOnItemAtPosition<ViewHolder?>(
                TestConstants.TEST_INDEX, click()))

        //Calibrate Swatches Screen


        takeScreenshot()

//        DecimalFormatSymbols dfs = new DecimalFormatSymbols();


        onView(withId(id.fabEditCalibration)).perform(click())

//        onView(withId(R.id.editBatchCode))
//                .perform(typeText("TEST 123#*@!"), closeSoftKeyboard());
//


        onView(withId(id.editExpiryDate)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(2025, 8, 25))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withText(string.save)).perform(click())
        val recyclerView3: ViewInteraction = onView(allOf(withId(id.calibrationList), childAtPosition(withClassName(`is`("android.widget.RelativeLayout")),
                0)))
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder?>(4, click()))

        // onView(withText("2" + dfs.getDecimalSeparator() + "0 mg/l")).perform(click());

        //onView(withId(R.id.buttonStart)).perform(click());


        saveCalibration("TestValid", TestConstants.CUVETTE_TEST_ID_1)
        goToMainScreen()
        onView(withText(string.settings)).perform(click())
        onView(withText(string.about)).check(matches(isDisplayed())).perform(click())
        enterDiagnosticMode()
        Espresso.pressBack()
        Espresso.pressBack()
        onView(withText(string.calibrate)).perform(click())
        sleep(4000)
        onView(allOf(withId(id.list_types), childAtPosition(withClassName(`is`("android.widget.LinearLayout")),
                0))).perform(actionOnItemAtPosition<ViewHolder?>(
                TestConstants.TEST_INDEX, click()))
        onView(withId(id.menuLoad)).perform(click())
        sleep(2000)
        onData(hasToString(startsWith("TestValid"))).perform(click())
        sleep(2000)
        leaveDiagnosticMode()

        goToMainScreen()
        onView(withText(string.calibrate)).perform(click())

//        onView(withText(currentHashMap.get("electricalConductivity"))).perform(click());
//
//        try {
//            onView(withText(R.string.incorrectCalibrationCanAffect)).check(matches(isDisplayed()));
//            //Calibrate EC Warning
//            takeScreenshot();
//
//            onView(withText(R.string.cancel)).perform(click());
//
//            onView(withText(currentHashMap.get("electricalConductivity"))).perform(click());
//
//            onView(withText(R.string.warning)).check(matches(isDisplayed()));
//
//            onView(withText(R.string.calibrate)).perform(click());
//
//            //Calibrate EC
//            takeScreenshot();
//
//            onView(withId(R.id.buttonStartCalibrate)).perform(click());
//
//            //EC not found dialog
//            takeScreenshot();
//
//            onView(withId(android.R.id.button1)).perform(click());
//
//        } catch (Exception ex) {
//            String message = String.format("%s\r\n\r\n%s", mActivityRule.getActivity().getString(R.string.phoneDoesNotSupport),
//                    mActivityRule.getActivity().getString(R.string.pleaseContactSupport));
//
//            onView(withText(message)).check(matches(isDisplayed()));
//
//            //Feature not supported
//            takeScreenshot();
//
//            onView(withText(R.string.ok)).perform(click());
//        }


        goToMainScreen()
        gotoSurveyForm()
        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_1)
        onView(withId(id.button_prepare)).check(matches(isDisplayed()))
        onView(withId(id.button_prepare)).perform(click())
        onView(withId(id.buttonNoDilution)).check(matches(isDisplayed()))

        //Dilution dialog


        takeScreenshot()
        TestUtil.goBack(5)
        mActivityRule.launchActivity(Intent())
        gotoSurveyForm()
        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_1)
        onView(withText(string.testName)).check(matches(isDisplayed()))

//        //Calibration incomplete


        takeScreenshot()

        // Chlorine not calibrated
        //onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

        //onView(withId(android.R.id.button2)).perform(click());


        mDevice.pressBack()
        mDevice.waitForWindowUpdate("", 2000)

//        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_2);

//        onView(withText(R.string.chromium)).check(matches(isDisplayed()));

//        onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

//        takeScreenshot();

//        mDevice.pressBack();

//        TestUtil.nextSurveyPage(3);
//
//        //Unknown test
//        clickExternalSourceButton(0, TestConstant.USE_EXTERNAL_SOURCE);
//
//        onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));
//
//        mDevice.pressBack();

//        TestUtil.swipeRight(7);
//
//        clickExternalSourceButton(0); //Iron
//
//        onView(withText(R.string.prepare_test)).check(matches(isDisplayed()));

        //onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

        //onView(withText(R.string.ok)).perform(click());


        mDevice.pressBack()

        //mDevice.pressBack();
        //onView(withId(android.R.id.button1)).perform(click());


    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun initialize() {
            if (!TestHelper.isDeviceInitialized()) {
                mDevice = UiDevice.getInstance(getInstrumentation())
            }
        }
    }
}