package io.block16.ethlistener.config;

import io.block16.ethlistener.dto.GenericResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

@ControllerAdvice
public class ResponseAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !GenericResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer,
                                           MediaType contentType,
                                           MethodParameter returnType,
                                           ServerHttpRequest request,
                                           ServerHttpResponse response) {
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        genericResponse.setData(bodyContainer.getValue());
        genericResponse.setError(false);
        genericResponse.setMessage("OK");
        response.setStatusCode(HttpStatus.OK);
    }
}
