package com.biubiu.miku.util.video.action;

import java.io.Serializable;

public class ActionContent implements Serializable {
  private static final long serialVersionUID = 7622206043854692123L;

  private ActionType actionType;

  public ActionContent(ActionType actionType) {
    this.actionType = actionType;
  }

  public ActionType getActionType() {
    return actionType;
  }
}
