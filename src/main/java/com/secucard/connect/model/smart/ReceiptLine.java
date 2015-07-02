package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReceiptLine {

  private String type;

  private Value value;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }


  @JsonIgnore
  public Type getLineType() {
    return Type.fromCode(type);
  }

  @Override
  public String toString() {
    return "ReceiptLine{" +
        "type='" + type + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

  /**
   * Enumeration of all possible text decorations.
   */
  public static enum Decoration {
    IMPORTANT("important", "Emphasize the text."),
    ALIGN_LEFT("align_left", "Left align the text."),
    ALIGN_RIGHT("align_right", "right align the text.");

    private String code;
    private String description;

    Decoration(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

    public static List<Decoration> fromCode(String[] code) {
      if (code == null) {
        return null;
      }
      ArrayList<Decoration> decorations = new ArrayList<>(code.length);
      for (String s : code) {
        for (Decoration value : values()) {
          if (value.code.equals(s)) {
            decorations.add(value);
          }
        }
      }
      return decorations;
    }

    @Override
    public String toString() {
      return "Decoration{" +
          "code='" + code + '\'' +
          ", description='" + description + '\'' +
          "} " + super.toString();
    }
  }

  /**
   * Enumeration of all possible line types.
   */
  public static enum Type {
    SEPARATOR("separator", "A horizontal separator like a line. May have caption."),
    SPACE("space", "Empty line. No text."),
    TEXT_LINE("textline", "Normal line of text. "),
    NAME_VALUE("name-value", "A name:value pair, may displayed as 2 columns.");
    private String code;
    private String description;

    Type(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

    public static Type fromCode(String code) {
      for (Type type : values()) {
        if (type.code.equals(code)) {
          return type;
        }
      }

      return null;
    }

    @Override
    public String toString() {
      return "Type{" +
          "code='" + code + '\'' +
          ", description='" + description + '\'' +
          "} " + super.toString();
    }
  }

  public static class Value {
    private String caption;
    private String text;
    private String name;
    private String value;
    private String[] decoration;

    public String getCaption() {
      return caption;
    }

    public void setCaption(String caption) {
      this.caption = caption;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String[] getDecoration() {
      return decoration;
    }

    public void setDecoration(String[] decoration) {
      this.decoration = decoration;
    }

    @JsonIgnore
    public List<Decoration> getDecorations() {
      return Decoration.fromCode(getDecoration());
    }

    @Override
    public String toString() {
      return "Value{" +
          "caption='" + caption + '\'' +
          ", text='" + text + '\'' +
          ", name='" + name + '\'' +
          ", value='" + value + '\'' +
          ", decoration=" + Arrays.toString(decoration) +
          '}';
    }
  }
}
