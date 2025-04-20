package org.example.ibb_ecodation_javafx.model.response;

import lombok.Builder;

@Builder
public record MailResponse(boolean isSuccess,String message) {
}
