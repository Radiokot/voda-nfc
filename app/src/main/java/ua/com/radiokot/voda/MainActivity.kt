package ua.com.radiokot.voda

import android.nfc.tech.MifareClassic
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardMifareReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardRawDataParser

class MainActivity : BaseActivity() {
    private val nfcReader by lazy {
        SimpleNfcReader(this)
    }

    private val cardReader by lazy {
        VodaCardMifareReader(BuildConfig.CARD_KEY_HEX.decodeHex())
    }

    private val cardRawDataParser = VodaCardRawDataParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcReader
            .tags
            .observeOn(AndroidSchedulers.mainThread())
            .filter { MifareClassic.get(it) != null }
            .map(MifareClassic::get)
            .flatMapSingle(cardReader::read)
            .flatMapSingle(cardRawDataParser::parse)
            .subscribe { vodaCard ->
                main_text_view.text = vodaCard.balanceUah.toPlainString()
            }
            .addTo(compositeDisposable)
    }

    override fun onResume() {
        super.onResume()
        nfcReader.start()
        if (nfcReader.isActive) {
            main_text_view.text = "Reading NFC tags..."
        } else {
            main_text_view.text = "NFC is not active"
        }
    }

    override fun onPause() {
        super.onPause()
        nfcReader.stop()
    }
}