package com.zbdigit.myapp.domain;

import java.util.Map;

public interface Owner {
    String getOwnerEntityName();
    Long getId();
    Map<String, Object> getExtData();
}
