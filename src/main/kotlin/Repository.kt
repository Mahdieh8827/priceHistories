import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

class Repository {
    companion object {
        @JvmStatic
        var instrumentMap: ConcurrentHashMap<String, ArrayList<SlideTime>> = ConcurrentHashMap()

        fun add(isin: String) {
            instrumentMap[isin] = ArrayList()
        }

        fun delete(isin: String) {
            instrumentMap.remove(isin)
        }

        fun update(isin: String, data: Quote) {
            instrumentMap[isin]?.add(SlideTime(data, Util.getCurrentEpochSecond()))
        }
    }
}