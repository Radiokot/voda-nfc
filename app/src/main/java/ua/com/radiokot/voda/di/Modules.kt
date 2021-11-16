package ua.com.radiokot.voda.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferences
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferencesImpl
import ua.com.radiokot.voda.util.format.AmountFormats

object Modules {
    val amountFormats = module {
        single(named("UAH")) { AmountFormats.uah }
        single { AmountFormats.default }
    }

    val persistence = module {
        single<ReaderPreferences> {
            ReaderPreferencesImpl(
                sharedPreferences = androidContext().getSharedPreferences(
                    "reader",
                    Context.MODE_PRIVATE
                )
            )
        }
    }
}