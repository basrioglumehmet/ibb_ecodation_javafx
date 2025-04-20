package org.example.ibb_ecodation_javafx.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
public class SendGridResponse {
    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<SendGridErrorResponse> errors;


}