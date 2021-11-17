package ua.com.radiokot.voda.features.reader.logic

import io.reactivex.Observable
import ua.com.radiokot.voda.features.card.model.VodaCard

interface VodaCardReader {
    /**
     * Read cards.
     */
    val cards: Observable<VodaCard>

    /**
     * Errors occurred during reading.
     */
    val errors: Observable<Throwable>
}