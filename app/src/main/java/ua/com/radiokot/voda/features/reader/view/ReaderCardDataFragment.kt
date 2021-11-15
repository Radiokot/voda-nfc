package ua.com.radiokot.voda.features.reader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_reader_card_data.*
import ua.com.radiokot.voda.BaseFragment
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource
import ua.com.radiokot.voda.util.format.AmountFormats
import java.math.BigDecimal
import java.math.RoundingMode

class ReaderCardDataFragment: BaseFragment() {
    private val uahAmountFormat = AmountFormats.uah
    private val litersFormat = AmountFormats.default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_card_data, container, false)
    }

    override fun onInitAllowed() {
        ((parentFragment ?: activity) as VodaCardsSource)
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { card ->
                val literPrice = BigDecimal("1.4")
                val liters = card.getLiters(literPrice)

                liters_text_view.text = litersFormat.format(liters)
                liters_label_text_view.text = resources.getQuantityString(R.plurals.liters, liters.setScale(0, RoundingMode.DOWN).intValueExact())

                balance_text_view.text = uahAmountFormat.format(card.balance)

                liter_price_text_view.text = uahAmountFormat.format(literPrice)
            }
            .addTo(compositeDisposable)
    }
}