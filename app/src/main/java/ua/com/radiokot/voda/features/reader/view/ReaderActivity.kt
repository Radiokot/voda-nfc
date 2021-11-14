package ua.com.radiokot.voda.features.reader.view

import android.nfc.tech.MifareClassic
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_reader.*
import ua.com.radiokot.voda.BaseActivity
import ua.com.radiokot.voda.BuildConfig
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.extensions.decodeHex
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardMifareReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardRawDataParser
import ua.com.radiokot.voda.features.reader.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource

class ReaderActivity : BaseActivity(), VodaCardsSource {
    private val nfcReader by lazy {
        SimpleNfcReader(this)
    }

    private val cardReader by lazy {
        VodaCardMifareReader(BuildConfig.CARD_KEY_HEX.decodeHex())
    }

    private val cardRawDataParser = VodaCardRawDataParser()

    private val fragmentsAdapter = ReaderFragmentsAdapter(supportFragmentManager, lifecycle)

    private val cardsSubject = BehaviorSubject.create<VodaCard>()
    override val cards = cardsSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        initPager()
        initReader()
    }

    private fun initPager() {
        pager.adapter = fragmentsAdapter
        pager.isUserInputEnabled = false
    }

    private fun initReader() {
        nfcReader
            .tags
            .observeOn(AndroidSchedulers.mainThread())
            .filter { MifareClassic.get(it) != null }
            .map(MifareClassic::get)
            .flatMapSingle(cardReader::read)
            .flatMapSingle(cardRawDataParser::parse)
            .subscribeBy(
                onNext = this::onNewCard,
                onError = {}
            )
            .addTo(compositeDisposable)
    }

    private fun onNewCard(card: VodaCard) {
        cardsSubject.onNext(card)
        if (pager.currentItem != ReaderFragmentsAdapter.CARD_DATA_POSITION) {
            pager.setCurrentItem(ReaderFragmentsAdapter.CARD_DATA_POSITION, true)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcReader.start()
    }

    override fun onPause() {
        super.onPause()
        nfcReader.stop()
    }
}