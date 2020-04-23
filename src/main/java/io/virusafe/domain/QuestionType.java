package io.virusafe.domain;

import lombok.Getter;

/**
 * Enum representing allowed question types.
 */
public enum QuestionType {
  BOOLEAN("Bool"),
  OPEN_ANSWER("Open answer");

  @Getter
  private String type;

  /**
   * Construct a QuestionType based on the passed case-sensitive type.
   *
   * @param type the type to construct QuestionType from
   */
  QuestionType(final String type) {
    this.type = type;
  }
}
