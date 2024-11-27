package az.ingress.model.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum ObjectMapperFactory {
    OBJECT_MAPPER;

    public ObjectMapper getInstance() {
        return new ObjectMapper();
    }
}