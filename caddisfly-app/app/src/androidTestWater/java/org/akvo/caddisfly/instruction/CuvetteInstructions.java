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

package org.akvo.caddisfly.instruction;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.RequiresDevice;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.common.ChamberTestConfig;
import org.akvo.caddisfly.common.Constants;
import org.akvo.caddisfly.common.TestConstants;
import org.akvo.caddisfly.model.TestInfo;
import org.akvo.caddisfly.model.TestType;
import org.akvo.caddisfly.repository.TestConfigRepository;
import org.akvo.caddisfly.ui.MainActivity;
import org.akvo.caddisfly.util.TestUtil;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static org.akvo.caddisfly.common.ChamberTestConfig.DELAY_BETWEEN_SAMPLING;
import static org.akvo.caddisfly.common.ChamberTestConfig.SKIP_SAMPLING_COUNT;
import static org.akvo.caddisfly.common.TestConstants.CUVETTE_TEST_TIME_DELAY;
import static org.akvo.caddisfly.common.TestConstants.DELAY_EXTRA;
import static org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton;
import static org.akvo.caddisfly.util.TestHelper.getString;
import static org.akvo.caddisfly.util.TestHelper.goToMainScreen;
import static org.akvo.caddisfly.util.TestHelper.gotoSurveyForm;
import static org.akvo.caddisfly.util.TestHelper.leaveDiagnosticMode;
import static org.akvo.caddisfly.util.TestHelper.loadData;
import static org.akvo.caddisfly.util.TestHelper.mCurrentLanguage;
import static org.akvo.caddisfly.util.TestHelper.mDevice;
import static org.akvo.caddisfly.util.TestHelper.setJsonVersion;
import static org.akvo.caddisfly.util.TestHelper.takeScreenshot;
import static org.akvo.caddisfly.util.TestUtil.nextSurveyPage;
import static org.akvo.caddisfly.util.TestUtil.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CuvetteInstructions {

    private static final int TEST_START_DELAY = 24;

    private final StringBuilder jsArrayString = new StringBuilder();
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void initialize() {
        if (mDevice == null) {
            mDevice = UiDevice.getInstance(getInstrumentation());

            for (int i = 0; i < 5; i++) {
                mDevice.pressBack();
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Before
    public void setUp() {

        loadData(mActivityTestRule.getActivity(), mCurrentLanguage);

//        SharedPreferences prefs =
//                PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
//        prefs.edit().clear().apply();

//        resetLanguage();
    }

    @Test
    @RequiresDevice
    public void testInstructionsCuvette() {

        int screenShotIndex = -1;

        leaveDiagnosticMode();

        setJsonVersion(2);

        goToMainScreen();

        gotoSurveyForm();

        clickExternalSourceButton(TestConstants.CUVETTE_TEST_ID_1);

        sleep(1000);

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        String id = TestConstants.CUVETTE_TEST_ID_1.substring(
                TestConstants.CUVETTE_TEST_ID_1.lastIndexOf("-") + 1
        );

        takeScreenshot(id, screenShotIndex);
        screenShotIndex++;

        mDevice.waitForIdle();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.next))).perform(click());

        takeScreenshot(id, screenShotIndex);
        screenShotIndex++;

        onView(withText(
                String.format(mActivityTestRule.getActivity().getString(R.string.timesDilution), 2)))
                .perform(click());

        for (int i = 0; i < 20; i++) {

            try {
                takeScreenshot(id, screenShotIndex);
                screenShotIndex++;
                onView(withId(R.id.image_pageRight)).perform(click());

                TestUtil.sleep(500);

            } catch (Exception e) {
                TestUtil.sleep(600);
                break;
            }
        }

        sleep((TEST_START_DELAY + CUVETTE_TEST_TIME_DELAY + DELAY_EXTRA
                + (DELAY_BETWEEN_SAMPLING * (ChamberTestConfig.SAMPLING_COUNT_DEFAULT + SKIP_SAMPLING_COUNT)))
                * 1000);

        takeScreenshot(id, screenShotIndex);
        screenShotIndex++;

        onView(withText("Result")).check(matches(isDisplayed()));
//        onView(withText("0.49")).check(matches(isDisplayed()));

//        List<UiObject2> button1s = mDevice.findObjects(By.text(
//                getString(mActivityTestRule.getActivity(), R.string.next)));
//        if (button1s.size() > 0) {
//            button1s.get(0).click();
//        }

        onView(withId(R.id.image_pageRight)).perform(click());

        takeScreenshot(id, screenShotIndex);

        onView(withText("Finish")).check(matches(isDisplayed()));

        List<UiObject2> buttonAccept = mDevice.findObjects(By.text(
                getString(mActivityTestRule.getActivity(), R.string.acceptResult)));
        if (buttonAccept.size() > 0) {
            buttonAccept.get(0).click();
        }

    }

    @Test
    @RequiresDevice
    public void testInstructionsBackcase2() {

        goToMainScreen();

        gotoSurveyForm();

        nextSurveyPage(4, "Water Tests 2");

        clickExternalSourceButton(0);

        sleep(1000);

        mDevice.waitForIdle();

        TestUtil.sleep(1000);

        String id = Constants.FREE_CHLORINE_ID.substring(
                Constants.FREE_CHLORINE_ID.lastIndexOf("-") + 1);

        takeScreenshot(id, -1);

        mDevice.waitForIdle();

//        onView(withText(R.string.cannotStartTest)).check(matches(isDisplayed()));

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.instructions))).perform(click());

        for (int i = 0; i < 17; i++) {

            try {
                takeScreenshot(id, i);

                onView(withId(R.id.image_pageRight)).perform(click());

            } catch (Exception e) {
                TestUtil.sleep(600);
                Espresso.pressBack();
                break;
            }
        }
    }

    @Test
    @RequiresDevice
    public void testInstructionsAll() {

        goToMainScreen();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.stripTest))).perform(click());

        TestConfigRepository testConfigRepository = new TestConfigRepository();
        List<TestInfo> testList = testConfigRepository.getTests(TestType.STRIP_TEST);

        if (TestConstants.STRIP_TESTS_COUNT == 1) {
            checkInstructions(testList.get(0).getUuid());
        } else {

            for (int i = 0; i < TestConstants.STRIP_TESTS_COUNT; i++) {

                assertEquals(testList.get(i).getSubtype(), TestType.STRIP_TEST);

                String id = testList.get(i).getUuid();
                id = id.substring(id.lastIndexOf("-") + 1);

                int pages = navigateToTest(i, id);

                jsArrayString.append("[").append("\"").append(id).append("\",").append(pages).append("],");
            }
        }

//        Log.e("Caddisfly", jsArrayString.toString());

    }

    private int navigateToTest(int index, String id) {

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list_types),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(index, click()));

        mDevice.waitForIdle();

        return checkInstructions(id);
    }

    private int checkInstructions(String id) {
        TestUtil.sleep(1000);

        takeScreenshot(id, -1);

        mDevice.waitForIdle();

        onView(withText(getString(mActivityTestRule.getActivity(), R.string.instructions))).perform(click());

        int pages = 0;
        for (int i = 0; i < 17; i++) {
            pages++;

            try {
                takeScreenshot(id, i);

                onView(withId(R.id.image_pageRight)).perform(click());

            } catch (Exception e) {
                TestUtil.sleep(600);
                Espresso.pressBack();
                Espresso.pressBack();
                TestUtil.sleep(600);
                break;
            }
        }
        return pages;
    }
}