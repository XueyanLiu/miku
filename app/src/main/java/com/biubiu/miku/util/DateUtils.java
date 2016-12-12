package com.biubiu.miku.util;

public class DateUtils {
  private static float TIME_SECOND_TO_MS_SIZE = 1000f;
  private static float TIME_MINUTE_TO_MS_SIZE = 60 * TIME_SECOND_TO_MS_SIZE;
  private static float TIME_HOUR_TO_MS_SIZE = 60 * TIME_MINUTE_TO_MS_SIZE;

  public static String getFfmpegDurationString(int durationMs) {
    StringBuilder durationStringBuilder = new StringBuilder();
    int hourSize = (int) (durationMs / TIME_HOUR_TO_MS_SIZE);
    durationStringBuilder.append(hourSize + ":");
    int minuteSize =
        (int) ((durationMs - hourSize * TIME_HOUR_TO_MS_SIZE) / TIME_MINUTE_TO_MS_SIZE);
    if (minuteSize >= 10) {
      durationStringBuilder.append(minuteSize + ":");
    } else {
      durationStringBuilder.append("0" + minuteSize + ":");
    }
    int secondSize =
        (int) ((durationMs - hourSize * TIME_HOUR_TO_MS_SIZE - minuteSize * TIME_MINUTE_TO_MS_SIZE)
            / TIME_SECOND_TO_MS_SIZE);
    if (secondSize >= 10) {
      durationStringBuilder.append(secondSize);
    } else {
      durationStringBuilder.append("0" + secondSize);
    }
    int msSize =
        (int) (durationMs - hourSize * TIME_HOUR_TO_MS_SIZE - minuteSize * TIME_MINUTE_TO_MS_SIZE
            - secondSize * TIME_SECOND_TO_MS_SIZE);
    if (msSize > 100) {
      durationStringBuilder.append("." + msSize);
    } else if (msSize >= 10) {
      durationStringBuilder.append(".0" + msSize);
    } else if (msSize > 0) {
      durationStringBuilder.append(".00" + msSize);
    } else {
      durationStringBuilder.append(".000");
    }
    return durationStringBuilder.toString();
  }
}
