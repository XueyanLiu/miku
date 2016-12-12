package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class RunManContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = 5922178366122468167L;
  private final RunManAttribute runManAttribute;
  private String textContent;
  private ActionImageData actionImageData;

  public RunManContent(RunManAttribute runManAttribute) {
    super(ActionType.RUN_MAN);
    this.runManAttribute = runManAttribute;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }

  public String getTextContent() {
    return textContent;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    this.actionImageData = actionImageData;
  }

  public ActionImageData getActionImageData() {
    return actionImageData;
  }

  public RunManAttribute getRunManAttribute() {
    return runManAttribute;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof RunManContent)) {
      return false;
    }
    RunManContent runManContent = (RunManContent) o;
    return runManContent.getRunManAttribute() == getRunManAttribute()
        && runManContent.getTextContent() == getTextContent();
  }

}
