package ua.com.radiokot.voda.features.reader.logic

import android.nfc.tech.MifareClassic
import io.reactivex.Observable
import ua.com.radiokot.voda.features.nfc.logic.NfcReader
import ua.com.radiokot.voda.features.reader.model.VodaCard

/**
 * Transforms [reader] read tags to cards
 */
class NfcVodaCardReader(
    reader: NfcReader,
    private val mifareReader: VodaCardMifareReader,
    private val dataParser: VodaCardRawDataParser,
) : VodaCardReader, NfcReader by reader {

    override val cards: Observable<VodaCard> =
        reader
            .tags
            .filter { MifareClassic.get(it) != null }
            .map(MifareClassic::get)
            .flatMapSingle(mifareReader::read)
            .flatMapSingle(dataParser::parse)
}