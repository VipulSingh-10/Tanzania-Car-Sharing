package com.singhv.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ValidateTokenRequestDTO {

    String token;
}
