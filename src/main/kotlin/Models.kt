import java.time.Instant

data class InstrumentEvent(val type: Type, val data: Instrument) {
    enum class Type {
        ADD,
        DELETE
    }
}

data class QuoteEvent(val data: Quote)

data class Instrument(val isin: ISIN, val description: String)
typealias ISIN = String

data class Quote(val isin: ISIN, val price: Price)
typealias Price = Double

data class SlideTime(val quote: Quote, val time: Long) : Comparable<SlideTime> {
    override fun compareTo(slideTime: SlideTime): Int {
        return (time - slideTime.time).toInt()
    }
}

interface CandlestickManager {
    fun getCandlesticks(isin: String): List<Candlestick>
}

data class Candlestick(
    val openPrice: Price,
    var highPrice: Price,
    var lowPrice: Price,
    var closingPrice: Price,
    val openDateTime: String,
    var closeTime: String,
    val openTimestamp: Instant,
    var closeTimestamp: Instant
)