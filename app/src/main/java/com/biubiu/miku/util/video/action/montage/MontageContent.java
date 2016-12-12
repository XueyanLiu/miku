package com.biubiu.miku.util.video.action.montage;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class MontageContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = 978502488332380194L;
  private MontageType montageActionType;

  public MontageContent(MontageType montageActionType) {
    super(ActionType.MONTAGE);
    this.montageActionType = montageActionType;
  }


  public MontageType getMontageType() {
    return montageActionType;
  }

}
