package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.knowledge.MedicationKnowledgeBase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("HealthMate AI", appName)
  }

  @Test
  fun `verify medication knowledge matching`() {
    val amoxicillin = MedicationKnowledgeBase.findMedication("Amoxicillin 500mg capsules")
    assertNotNull(amoxicillin)
    assertEquals("Amoxicillin", amoxicillin?.genericName)
    assertEquals("Antibiotic (Penicillin group)", amoxicillin?.medicineType)

    val advil = MedicationKnowledgeBase.findMedication("Advil Liquid Gels")
    assertNotNull(advil)
    assertEquals("Ibuprofen", advil?.genericName)
  }
}
