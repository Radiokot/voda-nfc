package ua.com.radiokot.voda.features.reader.view

import android.nfc.tech.MifareClassic
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_reader.*
import org.koin.android.ext.android.inject
import ua.com.radiokot.voda.BaseActivity
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.extensions.moderateSmoothScrollToPosition
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardMifareReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardRawDataParser
import ua.com.radiokot.voda.features.reader.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource


class ReaderActivity : BaseActivity(), VodaCardsSource {
    private val nfcReader by lazy {
        SimpleNfcReader(this)
    }

    private val cardReader: VodaCardMifareReader by inject()

    private val cardRawDataParser = VodaCardRawDataParser()

    private val fragmentsAdapter = ReaderFragmentsAdapter(supportFragmentManager, lifecycle)

    private val cardsSubject = BehaviorSubject.create<VodaCard>()
    override val cards = cardsSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        initPager()
        initReader()

        savedInstanceState?.getSerializable(CARD_DATA_STATE_KEY)?.also {
            if (it is VodaCard) {
                cardsSubject.onNext(it)
            }
        }
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
            pager.moderateSmoothScrollToPosition(ReaderFragmentsAdapter.CARD_DATA_POSITION)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Enable the reader e.g. after status bar is pulled.
        if (hasFocus && !nfcReader.isActive) {
            nfcReader.start()
        }
    }

    override fun onPause() {
        super.onPause()
        nfcReader.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(CARD_DATA_STATE_KEY, cardsSubject.value)
    }

    private companion object {
        private const val CARD_DATA_STATE_KEY = "card"
    }
}