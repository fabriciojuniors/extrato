package cloud.fabriciojunior.repositories;

import cloud.fabriciojunior.entities.Extrato;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

import java.util.UUID;

@RequestScoped
public class ExtratoRepository implements PanacheRepositoryBase<Extrato, UUID> {
}
