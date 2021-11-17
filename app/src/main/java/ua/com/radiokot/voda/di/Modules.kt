package ua.com.radiokot.voda.di

import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.card.storage.CardPreferences
import ua.com.radiokot.voda.features.card.storage.CardPreferencesImpl
import ua.com.radiokot.voda.features.reader.logic.*
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
        single<CardPreferences> {
            CardPreferencesImpl(
                sharedPreferences = androidContext().getSharedPreferences(
                    "card",
                    Context.MODE_PRIVATE
                )
            )
        }
    },

    // Card reader.
    module {
        factory<VodaCardReader>(named(InjectedCardReader.REAL)) { definitionParameters ->
            NfcVodaCardReader(
                tags = definitionParameters[0],
                resources = androidApplication().resources,
                mifareReader = MifareVodaCardRawDataReader(
                    cardKey = BuildConfig.CARD_KEY_HEX.decodeHex()
                ),
                dataParser = VodaCardRawDataParser(),
                vibrator = androidApplication()
                    .getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            )
        }

        single<VodaCardReader>(named(InjectedCardReader.DUMMY)) {
            DummyVodaCardReader()
        }

        factory<VodaCardReader> { definitionParameters ->
            if (BuildConfig.DUMMY_READER)
                get(named(InjectedCardReader.DUMMY))
            else
                get(named(InjectedCardReader.REAL)) { definitionParameters }
        }
    },

    // Toast manager.
    module {
        single {
            ToastManager(androidApplication())
        }
    },
)