package ua.com.radiokot.voda.features.reader.logic

import android.content.res.Resources
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.features.card.model.VodaCard

/**
 * Reads voda cards from NFC tags.
 *
 * @param tags sequence of NFC tags to read
 * @param resources required to load [R.xml.required_nfc_techs]
 * @param vibrator optional, to vibrate on card discovery
 */
class NfcVodaCardReader(
    tags: Observable<Tag>,
    resources: Resources,
    private val mifareReader: MifareVodaCardRawDataReader,
    private val dataParser: VodaCardRawDataParser,
    private val vibrator: Vibrator? = null
) : VodaCardReader {

    private val errorsSubject: PublishSubject<Throwable> = PublishSubject.create()
    override val errors: Observable<Throwable> = errorsSubject

    private val requiredTechLists = resources.getXml(R.xml.required_nfc_techs)
        .let(NfcTechListsXmlParser()::parse)

    override val cards: Observable<VodaCard> =
        tags
            .filter { tag ->
                val tagTechList = tag.techList.toHashSet()
                requiredTechLists.any { requiredTechList ->
                    requiredTechList.all(tagTechList::contains)
                }
            }
            .map(MifareClassic::get)
            .flatMapSingle(mifareReader::read)
            .map(dataParser::parse)
            // Redirect errors to the separate sequence.
            .retry { error ->
                Log.e(LOG_TAG, "cards: read_error, error = ${error.stackTraceToString()}")

                errorsSubject.onNext(error)
                true
            }
            .doOnNext {
                vibrate()

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "cards: card_read, card = $it")
                }
            }

    private fun vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                vibrator.vibrate(50)
            }
        }
    }

    private companion object {
        private const val LOG_TAG = "NfcVCReader"
    }
}