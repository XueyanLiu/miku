package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.model.VerticalTypefaceAttribute;

import java.io.Serializable;
import java.util.List;

public class RunManAttribute implements Serializable {
  private static final long serialVersionUID = 1986680193675151402L;
  private final RunManSoundAttribute runManSoundAttribute;
  private final RunManTextAttribute runManFirstCountTextAttribute;
  private final RunManTextAttribute runManSecondCountTextAttribute;
  private final RunManBackgroundAttribute runManBackgroundAttribute;
  private final List<RunManIconAttribute> runManIconAttributes;
  private final VerticalTypefaceAttribute vertaicalTypefaceAttribute;
  private final RunManPreviewAttribute runManPreviewAttribute;
  private final String content;

  public RunManAttribute(RunManSoundAttribute runManSoundAttribute,
                         RunManTextAttribute runManFirstCountTextAttribute,
                         RunManTextAttribute runManSecondCountTextAttribute,
                         RunManBackgroundAttribute runManBackgroundAttribute,
                         List<RunManIconAttribute> runManIconAttributes,
                         RunManPreviewAttribute runManPreviewAttribute,
                         String content,
                         VerticalTypefaceAttribute vertaicalTypefaceAttribute) {
    this.runManSoundAttribute = runManSoundAttribute;
    this.runManFirstCountTextAttribute = runManFirstCountTextAttribute;
    this.runManSecondCountTextAttribute = runManSecondCountTextAttribute;
    this.runManIconAttributes = runManIconAttributes;
    this.runManBackgroundAttribute = runManBackgroundAttribute;
    this.runManPreviewAttribute = runManPreviewAttribute;
    this.content = content;
    this.vertaicalTypefaceAttribute = vertaicalTypefaceAttribute;
  }

  public RunManSoundAttribute getRunManSoundAttribute() {
    return runManSoundAttribute;
  }

  public RunManTextAttribute getRunManFirstCountTextAttribute() {
    return runManFirstCountTextAttribute;
  }

  public List<RunManIconAttribute> getRunManIconAttributes() {
    return runManIconAttributes;
  }

  public RunManTextAttribute getRunManSecondCountTextAttribute() {
    return runManSecondCountTextAttribute;
  }

  public RunManBackgroundAttribute getRunManBackgroundAttribute() {
    return runManBackgroundAttribute;
  }

  public RunManPreviewAttribute getRunManPreviewAttribute() {
    return runManPreviewAttribute;
  }

  public String getContent() {
    return content;
  }

  public VerticalTypefaceAttribute getVertaicalTypefaceAttribute() {
    return vertaicalTypefaceAttribute;
  }

  public static class Builder {
    private RunManSoundAttribute runManSoundAttribute;
    private RunManTextAttribute runManFirstCountTextAttribute;
    private RunManTextAttribute runManSecondCountTextAttribute;
    private RunManBackgroundAttribute runManBackgroundAttribute;
    private List<RunManIconAttribute> runManIconAttributes;
    private RunManPreviewAttribute runManPreviewAttribute;
    private String content;
    private VerticalTypefaceAttribute vertaicalTypefaceAttribute;

    public Builder setRunManSoundAttribute(RunManSoundAttribute runManSoundAttribute) {
      this.runManSoundAttribute = runManSoundAttribute;
      return this;
    }

    public Builder setRunManFirstCountTextAttribute(
        RunManTextAttribute runManFirstCountTextAttribute) {
      this.runManFirstCountTextAttribute = runManFirstCountTextAttribute;
      return this;
    }

    public Builder setRunManSecondCountTextAttribute(
        RunManTextAttribute runManSecondCountTextAttribute) {
      this.runManSecondCountTextAttribute = runManSecondCountTextAttribute;
      return this;
    }

    public Builder setRunManBackgroundAttribute(
        RunManBackgroundAttribute runManBackgroundAttribute) {
      this.runManBackgroundAttribute = runManBackgroundAttribute;
      return this;
    }

    public Builder setRunManIconAttributes(List<RunManIconAttribute> runManIconAttributes) {
      this.runManIconAttributes = runManIconAttributes;
      return this;
    }

    public Builder setRunManPreviewAttribute(RunManPreviewAttribute runManPreviewAttribute) {
      this.runManPreviewAttribute = runManPreviewAttribute;
      return this;
    }

    public Builder setContent(String content) {
      this.content = content;
      return this;
    }

    public Builder setVertaicalTypefaceAttribute(VerticalTypefaceAttribute vertaicalTypefaceAttribute) {
      this.vertaicalTypefaceAttribute = vertaicalTypefaceAttribute;
      return this;
    }

    public RunManAttribute build() {
      return new RunManAttribute(runManSoundAttribute, runManFirstCountTextAttribute,
          runManSecondCountTextAttribute, runManBackgroundAttribute, runManIconAttributes,
          runManPreviewAttribute, content,vertaicalTypefaceAttribute);
    }
  }
}
