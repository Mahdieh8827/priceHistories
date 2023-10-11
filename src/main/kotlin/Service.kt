import Repository.Companion.instrumentMap
import Util.Companion.getZoneDate
import Util.Companion.getZoneTime
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class Service : CandlestickManager {
    private val timeInterval: Long = 60
    var minutesCount = 30L
    var lastMinutes: Long = 0

    override fun getCandlesticks(isin: String): List<Candlestick> {
        lastMinutes = Util.getEpochSecondByLastMinutes(minutesCount)
        return createCandlesticks(isin)
    }

    fun createCandlesticks(isin: String): List<Candlestick> {
        val oneMinutesSlotMap: TreeMap<Long, PriorityQueue<SlideTime>?> = TreeMap()
        val candlesticks: ArrayList<Candlestick> = arrayListOf()
        synchronized(this) {
            var counter = lastMinutes + timeInterval // example 11:50 + 1 min = 11:51
            val quoteList = instrumentMap[isin]?.filter { entry -> entry.time >= lastMinutes } // extract all quot after the
            // last minutes (after 11:50)

            var counterEmpty = 0 // counter for calc empty 1 min slot time
            var tempPriorityQueue: PriorityQueue<SlideTime> = PriorityQueue() // for keep last fill slot that use for fill after empty slots
            if (quoteList != null) {
                for (i in 1..minutesCount) { // break down the last 30 minutes quot to 1 min slots (11:50 - 12:20)
                    val priorityQueue: PriorityQueue<SlideTime> = PriorityQueue<SlideTime>() // keep all quot for each one minute slot
                    priorityQueue.addAll(quoteList.filter { entry -> entry.time >= (counter - timeInterval) && entry.time <= counter })// add all quot for one min (11:50 - 11:51)
                    if ((priorityQueue.size == 1 && priorityQueue.peek().time != counter) || priorityQueue.size > 1) { // check if a 1 min quot have more than 1 element or haven't just one overlap close time
                        oneMinutesSlotMap[counter] = priorityQueue // add one min slot quot to tree map (just for 11:50 - 11:51)
                        tempPriorityQueue = priorityQueue // keep current slot into empty queue for next one min slot that is empty or not
                        counterEmpty = 0 // rest the number of empty slot
                    } else if (counterEmpty > 1 && tempPriorityQueue.size > 0) { // go to this if when we have more than 1 empty slot
                        oneMinutesSlotMap[counter] = tempPriorityQueue // fill empty slot with previous candle values
                        oneMinutesSlotMap[counter - timeInterval] = tempPriorityQueue // fill second candle with previous candle values
                        // don't reset 'counterEmpty' because we want to fill another empty candle
                    }
                    counterEmpty++
                    counter += timeInterval // add 1 minutes to current time (11:50 -> 11:51 -> 11:52)
                }
            }
            oneMinutesSlotMap.forEach { entry ->
                run {
                    entry.value?.let { calculateCandlesticks(entry.key, it)?.let { candlesticks.add(it) } }
                }
            }
        }
        return candlesticks
    }

    private fun calculateCandlesticks(key: Long, queue: PriorityQueue<SlideTime>): Candlestick? { // produce 1 min candle
        if ((queue.size) > 0 && ((queue.last()?.time == key && (queue.size) > 1) || queue.last()?.time != key)
        ) { // queue have value AND if have an exception close time then should have more than 1 values OR have nor exception close time
            val first = queue.first()
            val last = queue.last()
            val openTimestamp = Instant.ofEpochSecond(first.time)
            val openPrice = first.quote.price
            val closeTimestamp = Instant.ofEpochSecond(last.time)
            val openDatetime = getZoneDate(openTimestamp)
            val closeTime = getZoneTime(closeTimestamp)
            val closePrice: Price
            val highPrice: Price
            val lowPrice: Price
            if (last?.time != key) { // if have not exception close time
                closePrice = last.quote.price
                highPrice = queue.maxByOrNull { price -> price.quote.price }!!.quote.price
                lowPrice = queue.minByOrNull { price -> price.quote.price }!!.quote.price
            } else { // if have exception close time
                closePrice = queue.elementAt(queue.size - 2).quote.price
                highPrice = queue.take(queue.size - 1)
                    .maxByOrNull { price -> price.quote.price }!!.quote.price
                lowPrice = queue.take(queue.size - 1)
                    .minByOrNull { price -> price.quote.price }!!.quote.price
            }

            return Candlestick(
                openPrice, highPrice, lowPrice, closePrice,
                openDatetime, closeTime, openTimestamp, closeTimestamp,
            )
        }
        return null
    }
}