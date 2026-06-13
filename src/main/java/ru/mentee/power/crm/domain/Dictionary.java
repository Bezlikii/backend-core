package ru.mentee.power.crm.domain;

import java.util.List;

public interface Dictionary<T> {
  List<T> getActiveItems();

  boolean isActive(T item);
}
