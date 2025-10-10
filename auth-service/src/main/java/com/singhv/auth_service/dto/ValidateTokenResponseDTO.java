package com.singhv.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ValidateTokenResponseDTO {

    String emailId;
    boolean isValid;

}
