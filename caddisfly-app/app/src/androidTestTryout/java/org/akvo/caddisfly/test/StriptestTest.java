package org.akvo.caddisfly.test;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.RequiresDevice;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.ui.MainActivity;
import org.akvo.caddisfly.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;
import static org.akvo.caddisfly.util.TestHelper.activateTestMode;
import static org.akvo.caddisfly.util.TestHelper.clearPreferences;
import static org.akvo.caddisfly.util.TestHelper.clickExternalSourceButton;
import static org.akvo.caddisfly.util.TestHelper.goToMainScreen;
import static org.akvo.caddisfly.util.TestHelper.gotoSurveyForm;
import static org.akvo.caddisfly.util.TestHelper.loadData;
import static org.akvo.caddisfly.util.TestHelper.mCurrentLanguage;
import static org.akvo.caddisfly.util.TestHelper.mDevice;
import static org.akvo.caddisfly.util.TestUtil.childAtPosition;
import static org.akvo.caddisfly.util.TestUtil.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class StriptestTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void initialize() {
        if (mDevice == null) {
            mDevice = UiDevice.getInstance(getInstrumentation());
        }
    }

    @Before
    public void setUp() {

        loadData(mActivityRule.getActivity(), mCurrentLanguage);

        clearPreferences(mActivityRule);

    }

    @Test
    @RequiresDevice
    public void startStriptest() {

        activateTestMode(mActivityRule.getActivity());

        test5in1(false);
        testSoilNitrogen(false);
        testMerckPH(false);
        testNitrate100();

        testArsenic(true);
    }

    private void testArsenic(boolean external) {
        if (external) {

            gotoSurveyForm();

            TestUtil.nextSurveyPage(3, "Arsenic");

            clickExternalSourceButton(2);

            mDevice.waitForIdle();

        } else {

            goToMainScreen();

            onView(withText(R.string.stripTest)).perform(click());

            onView(withText("Chlorine, Hardness, Alkalinity, pH")).perform(click());
        }

        sleep(1000);

        onView(withText(R.string.prepare_test)).perform(click());

        sleep(12000);

        onView(withText(R.string.start)).perform(click());

        sleep(8000);

        onView(withText(R.string.no_result)).check(matches(isDisplayed()));
        onView(withText(R.string.save)).check(matches(isDisplayed()));

        mDevice.waitForIdle();
        onView(withText(R.string.save)).perform(click());
    }

    @After
    public void tearDown() {
        clearPreferences(mActivityRule);
    }

    private void test5in1(boolean external) {

        if (external) {

            gotoSurveyForm();

            TestUtil.nextSurveyPage(3);

            clickExternalSourceButton(3);

            mDevice.waitForIdle();

        } else {

            goToMainScreen();

            onView(withText(R.string.stripTest)).perform(click());

            onView(withText("Chlorine, Hardness, Alkalinity, pH")).perform(click());
        }

        sleep(1000);

        onView(withText(R.string.prepare_test)).perform(click());

        sleep(12000);

        onView(withText(R.string.start)).perform(click());

        sleep(36000);

        onView(withText(R.string.start)).perform(click());

        sleep(35000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("Total Chlorine")).check(matches(isDisplayed()));
        onView(withText("0 mg/l")).check(matches(isDisplayed()));
        onView(withText("Free Chlorine")).check(matches(isDisplayed()));
        onView(withText("0.15 mg/l")).check(matches(isDisplayed()));
        onView(withText("Total Hardness")).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));

        mDevice.waitForIdle();
        mDevice.swipe(200, 750, 200, 600, 4);

        onView(withText(R.string.no_result)).check(matches(isDisplayed()));

        mDevice.waitForIdle();
        mDevice.swipe(200, 750, 200, 600, 4);

        onView(withText("Total Alkalinity")).check(matches(isDisplayed()));
        onView(withText("32 mg/l")).check(matches(isDisplayed()));

        mDevice.waitForIdle();
        mDevice.swipe(200, 750, 200, 600, 4);

        onView(withText("pH")).check(matches(isDisplayed()));
        onView(withText("6.2")).check(matches(isDisplayed()));

        onView(withText(R.string.save)).check(matches(isDisplayed()));

        mDevice.waitForIdle();
        onView(withText(R.string.save)).perform(click());

        if (external) {
            assertNotNull(mDevice.findObject(By.text("Total Chlorine: 0.0 mg/l")));
            assertNotNull(mDevice.findObject(By.text("Free Chlorine: 0.15 mg/l")));
            assertNotNull(mDevice.findObject(By.text("Total Hardness:  mg/l")));
            assertNotNull(mDevice.findObject(By.text("Total Alkalinity: 32.0 mg/l")));
            assertNotNull(mDevice.findObject(By.text("pH: 6.2 ")));
        }
    }

    private void testSoilNitrogen(boolean external) {

        if (external) {

            gotoSurveyForm();

            TestUtil.nextSurveyPage(3);

            clickExternalSourceButton(2);

            mDevice.waitForIdle();

        } else {

            goToMainScreen();

            onView(withText(R.string.stripTest)).perform(click());

            ViewInteraction recyclerView = onView(
                    allOf(withId(R.id.list_types),
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    0)));
            recyclerView.perform(actionOnItemAtPosition(7, click()));

        }


        sleep(1000);

        onView(withText(R.string.prepare_test)).perform(click());

        sleep(10000);

        onView(withText(R.string.start)).perform(click());

        sleep(65000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("Nitrogen")).check(matches(isDisplayed()));
        onView(withText("205.15 mg/l")).check(matches(isDisplayed()));
        onView(withText("Nitrate Nitrogen")).check(matches(isDisplayed()));
        onView(withText("41 mg/l")).check(matches(isDisplayed()));
        onView(withText("Nitrite Nitrogen")).check(matches(isDisplayed()));
        onView(withText("0.03 mg/l")).check(matches(isDisplayed()));
        onView(withText(R.string.save)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).perform(click());

        if (external) {
            assertNotNull(mDevice.findObject(By.text("Nitrogen: 205.15 mg/l")));
            assertNotNull(mDevice.findObject(By.text("Nitrate Nitrogen: 41.0 mg/l")));
            assertNotNull(mDevice.findObject(By.text("Nitrite Nitrogen: 0.03 mg/l")));
        }
    }

    private void testMerckPH(boolean external) {

        if (external) {

            gotoSurveyForm();

            TestUtil.nextSurveyPage(3);

            clickExternalSourceButton(1);

            mDevice.waitForIdle();

        } else {

            goToMainScreen();

            onView(withText(R.string.stripTest)).perform(click());

            ViewInteraction recyclerView = onView(
                    allOf(withId(R.id.list_types),
                            childAtPosition(
                                    withClassName(is("android.widget.LinearLayout")),
                                    0)));
            recyclerView.perform(actionOnItemAtPosition(10, click()));

        }

        sleep(1000);

        onView(withText(R.string.prepare_test)).perform(click());

        sleep(12000);

        onView(withText(R.string.start)).perform(click());

        sleep(5000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("pH")).check(matches(isDisplayed()));
        onView(withText("4.8")).check(matches(isDisplayed()));

        onView(withId(R.id.image_result)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).perform(click());

        if (external) {
            assertNotNull(mDevice.findObject(By.text("pH: 4.8 ")));
        }
    }

    private void testNitrate100() {

        goToMainScreen();

        onView(withText(R.string.stripTest)).perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list_types),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(6, click()));

        sleep(1000);

        onView(withText(R.string.prepare_test)).perform(click());

        sleep(12000);

        onView(withText(R.string.start)).perform(click());

        sleep(60000);

        onView(withText(R.string.result)).check(matches(isDisplayed()));
        onView(withText("Nitrate")).check(matches(isDisplayed()));
        onView(withText("14.5 mg/l")).check(matches(isDisplayed()));
        onView(withText("Nitrite")).check(matches(isDisplayed()));
        onView(withText("1.85 mg/l")).check(matches(isDisplayed()));
        onView(withText(R.string.save)).check(matches(isDisplayed()));

        onView(withText(R.string.save)).perform(click());
    }
}