package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.singhv.common.dto.RequestDTO;
import com.singhv.common.dto.ResponseDTO;
@RestController
public class HealthController {
@GetMapping("/health")
    public ResponseDTO<String> healthCheck() {
        ResponseDTO<String> response = new ResponseDTO();
        response.setSuccess(true);
        response.setErrorMessage(null);
        response.setResponseContent("Eureka is up and running!");
        return response;
    }
}
