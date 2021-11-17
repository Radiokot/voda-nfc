package ua.com.radiokot.voda.features.reader.view

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_reader.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.extensions.moderateSmoothScrollToPosition
import ua.com.radiokot.voda.features.nfc.logic.NfcReader
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardReader
import ua.com.radiokot.voda.features.card.model.VodaCard
import ua.com.radiokot.voda.features.card.view.VodaCardsSource
import ua.com.radiokot.voda.view.base.BaseActivity


class ReaderActivity : BaseActivity(), VodaCardsSource {
    private val nfcReader: NfcReader by lazy {
        SimpleNfcReader(this)
    }

    private val tagsSubject: PublishSubject<Tag> by lazy {
        PublishSubject
            .create<Tag>()
            .also(nfcReader.tags::subscribe)
    }

    private val cardReader: VodaCardReader by inject { parametersOf(tagsSubject) }

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

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                ?.also { intentTag ->
                    tagsSubject.onNext(intentTag)
                }
        }
    }

    private fun initPager() {
        pager.adapter = fragmentsAdapter
        pager.isUserInputEnabled = false
    }

    private fun initReader() {
        cardReader
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = this::onCardRead,
                onError = { fatalError ->
                    fatalError.printStackTrace()
                    toastManager.short(getString(
                        R.string.template_error_fatal_message,
                        fatalError.localizedMessage ?: fatalError.message
                    ))
                }
            )
            .addTo(compositeDisposable)

        cardReader
            .errors
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                error.printStackTrace()
                toastManager.short(R.string.error_failed_to_read_card_try_again)
            }
            .addTo(compositeDisposable)
    }

    private fun onCardRead(card: VodaCard) {
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