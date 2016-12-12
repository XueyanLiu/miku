package com.biubiu.miku.util.video;

import com.biubiu.miku.constant.EditState;
import com.biubiu.miku.util.video.action.ActionType;

public class ChangeElementState {
    private static Object object;
    private static ActionType actionType;
    private static EditState editState = EditState.ADDTEXT_INPUT;

    public ChangeElementState(ActionType actionType, Object object) {
        this.object = object;
        this.actionType = actionType;
    }

    public static Object getObject() {
        return object;
    }

    public static ActionType getActionType() {
        return actionType;
    }

    public static void setObject(Object object1) {
        object = object1;
    }

    public static void setActionType(ActionType actionType1) {
        actionType = actionType1;
    }

    public static void setEditState(EditState editState1) {
        editState = editState1;
    }

    public static EditState getEditState() {
        return editState;
    }
}
