package org.wangyichen.anynote.utils

import java.text.SimpleDateFormat
import java.util.*


class TimeUtils {
  companion object {
    fun getTime() = System.currentTimeMillis()

    //    年月日时分秒
    fun time2Date(time: Long): List<Int> {
      val dataFormater = SimpleDateFormat("yyyy MM dd HH mm ss", Locale.CHINA)
      val date = dataFormater.format(time)
      val list = date.split(" ").toList().map(String::toInt)
      return list
    }

    fun time2String(time: Long): String {
      val dataFormater = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
      val date = dataFormater.format(time)
      return date
    }

    fun data2time(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
      val dataFormater = SimpleDateFormat("yyyy MM dd HH mm ss", Locale.CHINA)
      val date = dataFormater.parse("$year $month $day $hour $minute 00")
      return date.time
    }
  }
}
