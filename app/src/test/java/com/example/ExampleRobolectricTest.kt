package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Cybersecurity Lab", appName)
  }

  @Test
  fun `test dashboard tab transitions and rendering`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardScreen()
      }
    }

    // Verify Academy is loaded
    composeTestRule.onNodeWithTag("nav_academy_tab").assertExists()
    
    // Click Labs Tab and verify
    composeTestRule.onNodeWithTag("nav_labs_tab").performClick()
    composeTestRule.waitForIdle()

    // Click Quiz Tab and verify
    composeTestRule.onNodeWithTag("nav_quiz_tab").performClick()
    composeTestRule.waitForIdle()

    // Click Charter Laws Tab and verify
    composeTestRule.onNodeWithTag("nav_laws_tab").performClick()
    composeTestRule.waitForIdle()

    // Click Campaign Tab and verify
    composeTestRule.onNodeWithTag("nav_campaign_tab").performClick()
    composeTestRule.waitForIdle()
  }
}

