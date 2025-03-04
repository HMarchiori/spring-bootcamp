package henrique.corrales.bootcamp.integrationtests.controllers.yaml.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;

public class YAMLMapper implements ObjectMapper {

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    protected TypeFactory typeFactory;

    public YAMLMapper() {
        objectMapper = new com.fasterxml.jackson.databind.ObjectMapper(new YAMLFactory())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        typeFactory = TypeFactory.defaultInstance();
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {
        try {
            String data = context.getDataToDeserialize().asString();
            Class<?> type = (Class<?>) context.getType();
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.readValue(data, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar YAML: " + e.getMessage(), e);
        }
    }



    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        try {
            return objectMapper.writeValueAsString(context.getObjectToSerialize());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao Serializar YAML: " + e.getMessage(), e);
        }
    }
}
