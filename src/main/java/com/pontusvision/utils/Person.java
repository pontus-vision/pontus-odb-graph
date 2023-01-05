package com.pontusvision.utils;

public class Person {

  private Person() {
  }

  public static String formatName(String firstName, String middleName, String lastName) {

    StringBuilder sb = new StringBuilder();

    boolean firstNameBool = firstName != null && !firstName.isEmpty();

    if (firstNameBool) {
      sb.append(firstName.trim().toUpperCase());
    }

    boolean middleNameBool = middleName != null && !middleName.isEmpty();

    if (middleNameBool && firstNameBool) {
      sb.append(" ").append(middleName.trim().toUpperCase());
    } else if (middleNameBool && !firstNameBool) {
      sb.append(middleName.trim().toUpperCase());
    }

    boolean lastNameBool = lastName != null && !lastName.isEmpty();

    if (lastNameBool && (firstNameBool || middleNameBool)) {
      sb.append(" ").append(lastName.trim().toUpperCase());
    } else if (lastNameBool && !(firstNameBool || middleNameBool)) {
      sb.append(lastName.trim().toUpperCase());
    }

    return sb.toString();

  }

}
