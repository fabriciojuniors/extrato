package cloud.fabriciojunior.resources;

import cloud.fabriciojunior.dtos.MovimentacaoDto;
import cloud.fabriciojunior.dtos.PageDto;
import cloud.fabriciojunior.entities.Movimentacao;
import cloud.fabriciojunior.repositories.MovimentacaoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("movimentacoes")
@Produces(MediaType.APPLICATION_JSON)
public class MovimentacaoResource {

    @Inject
    MovimentacaoRepository movimentacaoRepository;

    @GET
    public Response findAll(@DefaultValue("0") @QueryParam("page") final int page,
                            @DefaultValue("20") @QueryParam("size") final int size) {
        final PanacheQuery<Movimentacao> response = movimentacaoRepository.findAll().page(page, size);
        final List<MovimentacaoDto> instituicoes = response.list()
                .stream().map(MovimentacaoDto::from)
                .toList();

        return Response.ok(new PageDto<>(instituicoes, response.hasNextPage(), response.count())).build();
    }

}
