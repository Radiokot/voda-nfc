package ua.com.radiokot.voda.di

import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.reader.logic.*
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferences
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferencesImpl
import ua.com.radiokot.voda.util.format.AmountFormats
import ua.com.radiokot.voda.view.ToastManager

val injectionModules: List<Module> = listOf(
    // AmountFormat.
    module {
        single(named(InjectedAmountFormat.UAH)) { AmountFormats.uah }
        single { AmountFormats.default }
    },

    // Persistence.
    module {
        single<ReaderPreferences> {
            ReaderPreferencesImpl(
                sharedPreferences = androidContext().getSharedPreferences(
                    "reader",
                    Context.MODE_PRIVATE
                )
            )
        }
    },

    // Card reader.
    module {
        if (!BuildConfig.DUMMY_READER) {
            factory<VodaCardReader> { definitionParameters ->
                NfcVodaCardReader(
                    reader = definitionParameters[0],
                    mifareReader = VodaCardMifareReader(
                        cardKey = BuildConfig.CARD_KEY_HEX.decodeHex()
                    ),
                    dataParser = VodaCardRawDataParser()
                )
            }
        } else {
            single<VodaCardReader> {
                DummyVodaCardReader()
            }
        }
    },

    // Toast manager.
    module {
        single {
            ToastManager(androidApplication())
        }
    },
)