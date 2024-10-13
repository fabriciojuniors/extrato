package cloud.fabriciojunior.jobs;

import cloud.fabriciojunior.entities.Extrato;
import cloud.fabriciojunior.entities.enums.Status;
import cloud.fabriciojunior.repositories.ExtratoRepository;
import cloud.fabriciojunior.services.ExtratoService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

public class MovimentacaoScheduler {

    @Inject
    Logger logger;

    @Inject
    ExtratoRepository extratoRepository;

    @Inject
    ExtratoService service;

    @Scheduled(every = "{encerra-eventos-scheduler.time}")
    public void execute() {
        final List<Extrato> extratos = extratoRepository.find("status = :status", Parameters.with("status", Status.PENDENTE)).list();
        logger.info(String.format("Iniciando processamento do extrato. (Itens pendentes: %d)", extratos.size()));
        extratos.forEach(service::processar);
        logger.info("Finalizado processamento do extrato.");
    }


}
