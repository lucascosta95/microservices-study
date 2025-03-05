package br.com.lucascosta.orderserviceapi.decoders;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import models.exceptions.GenericFeignException;

import java.io.InputStream;
import java.util.Map;

@Slf4j
public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();

            final var error = mapper.readValue(bodyIs, Map.class);
            final var status = (Integer) error.get("status");

            return new GenericFeignException(status, error);
        } catch (Exception e) {
            log.error("Error decoding response", e);
        }

        return null;
    }
}
