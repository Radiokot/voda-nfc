package ua.com.radiokot.voda.features.reader.view

import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.databinding.ActivityReaderBinding
import ua.com.radiokot.voda.extensions.moderateSmoothScrollToPosition
import ua.com.radiokot.voda.features.card.model.VodaCard
import ua.com.radiokot.voda.features.card.view.VodaCardsSource
import ua.com.radiokot.voda.features.nfc.logic.NfcReader
import ua.com.radiokot.voda.features.nfc.logic.SimpleNfcReader
import ua.com.radiokot.voda.features.reader.logic.VodaCardReader
import ua.com.radiokot.voda.view.base.BaseActivity


class ReaderActivity : BaseActivity(), VodaCardsSource {
    private lateinit var view: ActivityReaderBinding

    private val nfcReader: NfcReader by lazy {
        SimpleNfcReader(this)
    }

    private val cardReader: VodaCardReader by inject {
        parametersOf(nfcReader.tags)
    }

    private val fragmentsAdapter = ReaderFragmentsAdapter(supportFragmentManager, lifecycle)

    private val cardsSubject = BehaviorSubject.create<VodaCard>()
    override val cards = cardsSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(view.root)

        initPager()
        initReader()

        @Suppress("DEPRECATION")
        savedInstanceState?.getSerializable(CARD_DATA_STATE_KEY)?.also {
            if (it is VodaCard) {
                cardsSubject.onNext(it)
            }
        }

        (0..29).forEach {
            Log.i("Oleg", "$it ${resources.getQuantityString(R.plurals.liters, it)}")
        }
    }

    private fun initPager() {
        view.pager.adapter = fragmentsAdapter
        view.pager.isUserInputEnabled = false
    }

    private fun initReader() {
        cardReader
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onCardRead)
            .addTo(compositeDisposable)

        cardReader
            .errors
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                toastManager.short(R.string.error_failed_to_read_card_try_again)
            }
            .addTo(compositeDisposable)
    }

    private fun onCardRead(card: VodaCard) {
        cardsSubject.onNext(card)
        if (view.pager.currentItem != ReaderFragmentsAdapter.CARD_DATA_POSITION) {
            view.pager.moderateSmoothScrollToPosition(ReaderFragmentsAdapter.CARD_DATA_POSITION)
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
