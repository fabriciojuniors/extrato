package cloud.fabriciojunior.config;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class RegraNegocioExceptionMapper implements ExceptionMapper<RegraNegocioException> {

    @Override
    public Response toResponse(final RegraNegocioException e) {
        return Response.ok(Map
                .of("mensagem", e.getMessage()))
                .build();
    }
}
