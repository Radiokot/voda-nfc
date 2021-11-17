package ua.com.radiokot.voda

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LocalizationTest {
    @Test
    fun litersRu() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val localizedContext =getLocalizedContext(appContext, Locale("ru"))

        val printout = (0..29).joinToString(separator = "\n") {
            "$it ${
                localizedContext.resources.getQuantityString(
                    R.plurals.liters,
                    it
                )
            }"
        }

        Assert.assertEquals(
            "0 литров\n" +
                    "1 литр\n" +
                    "2 литра\n" +
                    "3 литра\n" +
                    "4 литра\n" +
                    "5 литров\n" +
                    "6 литров\n" +
                    "7 литров\n" +
                    "8 литров\n" +
                    "9 литров\n" +
                    "10 литров\n" +
                    "11 литров\n" +
                    "12 литров\n" +
                    "13 литров\n" +
                    "14 литров\n" +
                    "15 литров\n" +
                    "16 литров\n" +
                    "17 литров\n" +
                    "18 литров\n" +
                    "19 литров\n" +
                    "20 литров\n" +
                    "21 литр\n" +
                    "22 литра\n" +
                    "23 литра\n" +
                    "24 литра\n" +
                    "25 литров\n" +
                    "26 литров\n" +
                    "27 литров\n" +
                    "28 литров\n" +
                    "29 литров",
            printout
        )
    }

    @Test
    fun litersUk() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val localizedContext =getLocalizedContext(appContext, Locale("uk"))

        val printout = (0..29).joinToString(separator = "\n") {
            "$it ${
                localizedContext.resources.getQuantityString(
                    R.plurals.liters,
                    it
                )
            }"
        }

        Assert.assertEquals(
            "0 літрів\n" +
                    "1 літр\n" +
                    "2 літри\n" +
                    "3 літри\n" +
                    "4 літри\n" +
                    "5 літрів\n" +
                    "6 літрів\n" +
                    "7 літрів\n" +
                    "8 літрів\n" +
                    "9 літрів\n" +
                    "10 літрів\n" +
                    "11 літрів\n" +
                    "12 літрів\n" +
                    "13 літрів\n" +
                    "14 літрів\n" +
                    "15 літрів\n" +
                    "16 літрів\n" +
                    "17 літрів\n" +
                    "18 літрів\n" +
                    "19 літрів\n" +
                    "20 літрів\n" +
                    "21 літр\n" +
                    "22 літри\n" +
                    "23 літри\n" +
                    "24 літри\n" +
                    "25 літрів\n" +
                    "26 літрів\n" +
                    "27 літрів\n" +
                    "28 літрів\n" +
                    "29 літрів",
            printout
        )
    }

    private fun getLocalizedContext(
        base: Context,
        locale: Locale
    ): Context {
        return base.createConfigurationContext(base.resources.configuration.apply {
            setLocale(locale)
        })
    }
}