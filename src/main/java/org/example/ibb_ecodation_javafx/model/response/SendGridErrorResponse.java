package org.example.ibb_ecodation_javafx.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendGridErrorResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("field")
    private String field;

    @JsonProperty("help")
    private String help;
}