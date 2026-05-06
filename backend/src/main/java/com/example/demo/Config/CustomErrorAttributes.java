package com.example.demo.Config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        // Obtenemos la excepción real si existe
        Throwable error = getError(webRequest);

        if (error instanceof ResourceNotFoundException) {
            errorAttributes.put("status", 404);
            errorAttributes.put("error", "Not Found");
            errorAttributes.put("message", error.getMessage());
        } else if (error != null) {
            errorAttributes.put("status", 400);
            errorAttributes.put("error", "Bad Request");
            errorAttributes.put("message", error.getMessage());
        }

        return errorAttributes;
    }
}
