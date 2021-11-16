package ua.com.radiokot.voda.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.reader.logic.VodaCardMifareReader
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferences
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferencesImpl
import ua.com.radiokot.voda.util.format.AmountFormats

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

    // Keystore.
    module {
        single(named(InjectedKey.VODA_CARD)) {
            BuildConfig.CARD_KEY_HEX.decodeHex()
        }
    },

    // Card reader.
    module {
        single {
            VodaCardMifareReader(
                cardKey = get(named(InjectedKey.VODA_CARD))
            )
        }
    },
)