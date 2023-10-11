import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.collections.ArrayList
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.assertEquals
import Repository.Companion.instrumentMap
import Util.Companion.getCalenderTime
import Util.Companion.getEpochSecondByLastMinutesForTest

class MainTest {

    @MockK
    val service: Service = Service()

    @BeforeTest
    fun `before test`() {
        service.lastMinutes = getEpochSecondByLastMinutesForTest(30)
    }

    @AfterEach
    fun `after each test`() {
        instrumentMap.clear()
    }

    @Test
    @Ignore
    fun `dummy test`() {
        assertEquals(4, 2 + 1)
    }

    @Test
    fun `when an instrument is not exist then return empty list`() {
        val isis = "VEO12J826263"
        val instrumentList: ArrayList<SlideTime> = ArrayList()
        instrumentList.add(SlideTime(Quote(isis, 772.0588), Util.getCurrentEpochSecond()))
        instrumentList.add(SlideTime(Quote(isis, 1138.0851), Util.getCurrentEpochSecond()))
        instrumentMap[isis] = instrumentList

        val list = service.createCandlesticks("VEO12J826200")
        assertAll("candlestick",
            { assertEquals(0, list.size) }
        )
    }

    @Test
    fun `when do not receive quests just one minute then normal flow`() {
        val instrumentList1: ArrayList<SlideTime> = ArrayList()
        val isin1 = "VEO12J826263"
        val isin2 = "HL844633D758"

        instrumentList1.add(SlideTime(Quote(isin1, 772.0588), getCalenderTime(2022, 5, 15, 13, 15, 5).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1134.0851), getCalenderTime(2022, 5, 15, 13, 15, 13).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 192.6667), getCalenderTime(2022, 5, 15, 13, 15, 39).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1138.7578), getCalenderTime(2022, 5, 15, 13, 16, 0).epochSecond))

        instrumentList1.add(SlideTime(Quote(isin1, 595.6264), getCalenderTime(2022, 5, 15, 13, 16, 14).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1440.1522), getCalenderTime(2022, 5, 15, 13, 16, 39).epochSecond))

        instrumentList1.add(SlideTime(Quote(isin1, 1630.6221), getCalenderTime(2022, 5, 15, 13, 18, 0).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1253.6481), getCalenderTime(2022, 5, 15, 13, 18, 13).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 540.5971), getCalenderTime(2022, 5, 15, 13, 18, 39).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1580.2286), getCalenderTime(2022, 5, 15, 13, 18, 44).epochSecond))
        instrumentList1.add(SlideTime(Quote(isin1, 1592.9714), getCalenderTime(2022, 5, 15, 13, 18, 46).epochSecond))

        val instrumentList2: ArrayList<SlideTime> = ArrayList()
        instrumentList2.add(SlideTime(Quote(isin2, 1364.7123), Util.getCurrentEpochSecond()))
        instrumentList2.add(SlideTime(Quote(isin2, 82.3089), Util.getCurrentEpochSecond()))

        instrumentMap[isin1] = instrumentList1
        instrumentMap[isin2] = instrumentList2
        val list = service.createCandlesticks(isin1)

        assertAll(
            "candlestick",
            { assertEquals(192.6667, list[0].lowPrice) },
            { assertEquals(1134.0851, list[0].highPrice) },
            { assertEquals(192.6667, list[0].closingPrice) },
            { assertEquals(772.0588, list[0].openPrice) },

            { assertEquals(595.6264, list[1].lowPrice) },
            { assertEquals(1440.1522, list[1].highPrice) },
            { assertEquals(1440.1522, list[1].closingPrice) },
            { assertEquals(1138.7578, list[1].openPrice) },

            { assertEquals(540.5971, list[2].lowPrice) },
            { assertEquals(1630.6221, list[2].highPrice) },
            { assertEquals(1592.9714, list[2].closingPrice) },
            { assertEquals(1630.6221, list[2].openPrice) },
        )
    }

    @Test
    fun `when quests were out of the last 30 minutes window then are missed`() {
        val instrumentList: ArrayList<SlideTime> = ArrayList()
        val isin = "VEO12J826263"
        instrumentList.add(SlideTime(Quote(isin, 772.0588), getCalenderTime(2022, 5, 15, 13, 11, 5).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1134.0851), getCalenderTime(2022, 5, 15, 13, 11, 13).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 192.6667), getCalenderTime(2022, 5, 15, 13, 11, 39).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 1630.6221), getCalenderTime(2022, 5, 15, 13, 18, 0).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1253.6481), getCalenderTime(2022, 5, 15, 13, 18, 13).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 540.5971), getCalenderTime(2022, 5, 15, 13, 18, 39).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1580.2286), getCalenderTime(2022, 5, 15, 13, 18, 44).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1592.9714), getCalenderTime(2022, 5, 15, 13, 18, 46).epochSecond))
        instrumentMap[isin] = instrumentList
        val list = service.createCandlesticks(isin)

        assertAll("candlestick",
            { assertEquals(540.5971, list[0].lowPrice) },
            { assertEquals(1630.6221, list[0].highPrice) },
            { assertEquals(1592.9714, list[0].closingPrice) },
            { assertEquals(1630.6221, list[0].openPrice) }
        )
    }

    @Test
    fun `when change the value of quotes history window then new window slots`() {
        val instrumentList: ArrayList<SlideTime> = ArrayList()
        val isin = "VEO12J826263"
        service.minutesCount = 5
        service.lastMinutes = getEpochSecondByLastMinutesForTest(5)

        instrumentList.add(SlideTime(Quote(isin, 772.0588), getCalenderTime(2022, 5, 15, 13, 38, 5).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1134.0851), getCalenderTime(2022, 5, 15, 13, 38, 13).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 192.6667), getCalenderTime(2022, 5, 15, 13, 38, 39).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 1630.6221), getCalenderTime(2022, 5, 15, 13, 42, 0).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1253.6481), getCalenderTime(2022, 5, 15, 13, 42, 15).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 540.5971), getCalenderTime(2022, 5, 15, 13, 42, 59).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 539.5891), getCalenderTime(2022, 5, 15, 13, 43, 0).epochSecond))
        instrumentMap[isin] = instrumentList
        val list = service.createCandlesticks(isin)

        assertAll("candlestick",
            { assertEquals(540.5971, list[0].lowPrice) },
            { assertEquals(1630.6221, list[0].highPrice) },
            { assertEquals(540.5971, list[0].closingPrice) },
            { assertEquals(1630.6221, list[0].openPrice) }
        )
    }

    @Test
    fun `when do not receive any quotes more than a minute then repead the last quote`() {
        val instrumentList: ArrayList<SlideTime> = ArrayList()
        val isin = "VEO12J826263"
        instrumentList.add(SlideTime(Quote(isin, 771.0588), getCalenderTime(2022, 5, 15, 13, 20, 7).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 772.0588), getCalenderTime(2022, 5, 15, 13, 20, 12).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 773.0588), getCalenderTime(2022, 5, 15, 13, 20, 32).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 1133.0851), getCalenderTime(2022, 5, 15, 13, 23, 8).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1134.0851), getCalenderTime(2022, 5, 15, 13, 23, 16).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 1253.6481), getCalenderTime(2022, 5, 15, 13, 25, 0).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1254.6481), getCalenderTime(2022, 5, 15, 13, 25, 15).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1255.6481), getCalenderTime(2022, 5, 15, 13, 25, 56).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1256.6481), getCalenderTime(2022, 5, 15, 13, 25, 59).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 772.0588), getCalenderTime(2022, 5, 15, 13, 38, 5).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1134.0851), getCalenderTime(2022, 5, 15, 13, 38, 13).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 192.6667), getCalenderTime(2022, 5, 15, 13, 38, 39).epochSecond))

        instrumentList.add(SlideTime(Quote(isin, 1630.6221), getCalenderTime(2022, 5, 15, 13, 42, 0).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 1253.6481), getCalenderTime(2022, 5, 15, 13, 42, 15).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 540.5971), getCalenderTime(2022, 5, 15, 13, 42, 59).epochSecond))
        instrumentList.add(SlideTime(Quote(isin, 539.5891), getCalenderTime(2022, 5, 15, 13, 43, 0).epochSecond))

        instrumentMap[isin] = instrumentList
        val list = service.createCandlesticks(isin)

        assertAll("candlestick",
            { assertEquals(771.0588, list[0].lowPrice) },
            { assertEquals(773.0588, list[0].highPrice) },
            { assertEquals(773.0588, list[0].closingPrice) },
            { assertEquals(771.0588, list[0].openPrice) },

            { assertEquals(1133.0851, list[3].lowPrice) },
            { assertEquals(1134.0851, list[3].highPrice) },
            { assertEquals(1134.0851, list[3].closingPrice) },
            { assertEquals(1133.0851, list[3].openPrice) },

            { assertEquals(540.5971, list[21].lowPrice) },
            { assertEquals(1630.6221, list[21].highPrice) },
            { assertEquals(540.5971, list[21].closingPrice) },
            { assertEquals(1630.6221, list[21].openPrice) },

            { assertEquals(539.5891, list[22].lowPrice) },
            { assertEquals(539.5891, list[22].highPrice) },
            { assertEquals(539.5891, list[22].closingPrice) },
            { assertEquals(539.5891, list[22].openPrice) }
        )
    }
}