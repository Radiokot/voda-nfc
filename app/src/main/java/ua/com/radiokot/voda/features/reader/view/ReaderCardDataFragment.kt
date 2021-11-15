package ua.com.radiokot.voda.features.reader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_reader_card_data.*
import ua.com.radiokot.voda.BaseFragment
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.extensions.isInteger
import ua.com.radiokot.voda.features.reader.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource
import ua.com.radiokot.voda.util.format.AmountFormats
import java.math.BigDecimal

class ReaderCardDataFragment : BaseFragment() {
    private val uahAmountFormat = AmountFormats.uah
    private val litersFormat = AmountFormats.default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_card_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToCardSource()

        initLabels()
    }

    private fun subscribeToCardSource() {
        ((parentFragment ?: activity) as VodaCardsSource)
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onNewCard)
            .addTo(compositeDisposable)
    }

    private fun initLabels() {
        val bottomLabelsColor =
            if (background_image_view.visibility == View.VISIBLE)
                ContextCompat.getColor(requireContext(), R.color.primary_text_inverse)
            else
                ContextCompat.getColor(requireContext(), R.color.primary_text)

        listOf(balance_label_text_view, balance_text_view,
            liter_price_label_text_view, liter_price_text_view).forEach {
            it.setTextColor(bottomLabelsColor)
        }
    }

    private fun onNewCard(card: VodaCard) {
        val literPrice = BigDecimal("1.4")
        val liters = card.getLiters(literPrice)

        liters_text_view.text = litersFormat.format(liters)
        liters_label_text_view.text =
                // 1 liter but 1.5 liters, 3 літри але 3.5 літра.
            if (liters.isInteger)
                resources.getQuantityString(R.plurals.liters, liters.intValueExact())
            else
                resources.getString(R.string.liters_when_fractional)

        balance_text_view.text = uahAmountFormat.format(card.balance)

        liter_price_text_view.text = uahAmountFormat.format(literPrice)
    }
}