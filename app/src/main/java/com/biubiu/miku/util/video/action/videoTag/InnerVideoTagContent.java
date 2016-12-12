package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * video tag 输入框在图中间
 */
public class InnerVideoTagContent extends VideoTagContent implements Serializable {
  private static final long serialVersionUID = 5246560092367474353L;
  private static final String[] labels = new String[]{"_head", "_mid", "_tail", "_up", "_down"};
  private transient int componentResIds[];
  private transient InnerEditTextParams innerEditTextParams;

  public InnerVideoTagContent(SourceType sourceType, int resId, String resPrefix) {
    super(sourceType, resId);
    this.componentResIds = getResIdsArray(resPrefix);
    setResPrefix(resPrefix);
  }

  public int[] getComponentResIds() {
    return componentResIds;
  }

  public InnerEditTextParams getInnerEditTextParams() {
    return innerEditTextParams;
  }

  public void setInnerEditTextParams(InnerEditTextParams innerEditTextParams) {
    this.innerEditTextParams = innerEditTextParams;
  }

  private static int[] getResIdsArray(String str) {
    int[] ids = new int[5];
    for (int i = 0; i < labels.length; i++) {
      try {
        Field field = R.drawable.class.getField(str + labels[i]);
        ids[i] = field.getInt(new R.drawable());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return ids;
  }
}
