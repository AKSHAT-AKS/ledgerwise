package com.akshat.ledgerwise.model;
public enum Category {
 FOOD("Food & Dining"), TRANSPORT("Transport"), SHOPPING("Shopping"), BILLS("Bills & Utilities"), HEALTH("Health"), EDUCATION("Education"), ENTERTAINMENT("Entertainment"), SALARY("Salary"), FREELANCE("Freelance"), INVESTMENT("Investment"), OTHER("Other");
 private final String label; Category(String label) { this.label = label; } public String getLabel() { return label; }
}