package ua.com.radiokot.voda

import android.nfc.Tag
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import ua.com.radiokot.voda.di.InjectedCardReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardReader

@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest : KoinTest {
    @Test
    fun applicationModules() {
        getKoin().checkModules {
            create<VodaCardReader> {
                parametersOf(Observable.empty<Tag>())
            }

            create<VodaCardReader>(named(InjectedCardReader.REAL)) {
                parametersOf(Observable.empty<Tag>())
            }
        }
    }
}