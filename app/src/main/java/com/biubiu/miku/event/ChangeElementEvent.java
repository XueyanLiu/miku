package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.ActionType;

public class ChangeElementEvent {
  private final Object object;
  private final ActionType actionType;

  public ChangeElementEvent(ActionType actionType, Object object){
    this.object = object;
    this.actionType = actionType;
  }

  public Object getObject() {
    return object;
  }

  public ActionType getActionType() {
    return actionType;
  }
}
