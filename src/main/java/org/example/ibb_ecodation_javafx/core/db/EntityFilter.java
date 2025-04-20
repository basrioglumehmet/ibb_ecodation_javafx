package org.example.ibb_ecodation_javafx.core.db;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityFilter {
    private String column;
    private String operator; // e.g., "=", ">", "<", "LIKE"
    private Object value;
}
