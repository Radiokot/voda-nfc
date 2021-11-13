package ua.com.radiokot.voda

import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.extensions.encodeHex
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader

class MainActivity : BaseActivity() {
    private val nfcReader by lazy {
        SimpleNfcReader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcReader
            .tags
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tag ->
                MifareClassic.get(tag).use { mifare ->
                    mifare.connect()

                    val balanceSectorI = 9
                    val balanceBlockI = 0

                    mifare.authenticateSectorWithKeyB(balanceSectorI, BuildConfig.CARD_KEY_HEX.decodeHex())
                    val balanceBlock =
                        mifare.readBlock(mifare.sectorToBlock(balanceSectorI) + balanceBlockI)
                    Log.i("Oleg", "Balance block: ${balanceBlock.encodeHex()}")
                }
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