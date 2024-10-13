package cloud.fabriciojunior.repositories;

import cloud.fabriciojunior.entities.Movimentacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MovimentacaoRepository implements PanacheRepository<Movimentacao> {
}
