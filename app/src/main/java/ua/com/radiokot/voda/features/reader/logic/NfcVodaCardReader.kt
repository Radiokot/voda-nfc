package ua.com.radiokot.voda.features.reader.logic

import android.nfc.tech.MifareClassic
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import io.reactivex.Observable
import ua.com.radiokot.voda.features.nfc.logic.NfcReader
import ua.com.radiokot.voda.features.reader.model.VodaCard

/**
 * Transforms [reader] read tags to cards
 *
 * @param vibrator optional, to vibrate on card discovery
 */
class NfcVodaCardReader(
    reader: NfcReader,
    private val mifareReader: VodaCardMifareReader,
    private val dataParser: VodaCardRawDataParser,
    private val vibrator: Vibrator? = null
) : VodaCardReader, NfcReader by reader {

    override val cards: Observable<VodaCard> =
        reader
            .tags
            .filter { MifareClassic.get(it) != null }
            .map(MifareClassic::get)
            .flatMapSingle(mifareReader::read)
            .flatMapSingle(dataParser::parse)
            .doOnNext { vibrate() }

    private fun vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                vibrator.vibrate(50)
            }
        }
    }
}