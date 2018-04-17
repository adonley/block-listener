package io.block16.ethlistener.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.block16.ethlistener.dto.BlockWorkDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

public class BlockWorkMessageConverter extends AbstractMessageConverter {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Message createMessage(Object object, MessageProperties messageProperties) {
        throw new UnsupportedOperationException("Conversion from object not supported for BlockWorkMessage");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            return objectMapper.readValue(message.getBody(), BlockWorkDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessageConversionException(e.getMessage());
        }
    }
}
