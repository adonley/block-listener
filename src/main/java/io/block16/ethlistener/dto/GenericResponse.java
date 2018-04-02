package io.block16.ethlistener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {

    public GenericResponse(T data) {
        this.setData(data);
        this.setError(false);
        this.setMessage(message);
    }

    private String message;
    private boolean error;
    private T Data;
}
