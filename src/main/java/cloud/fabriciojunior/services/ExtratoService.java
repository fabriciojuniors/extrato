package cloud.fabriciojunior.services;

import cloud.fabriciojunior.entities.Extrato;
import cloud.fabriciojunior.entities.Movimentacao;
import cloud.fabriciojunior.entities.enums.Status;
import cloud.fabriciojunior.repositories.ExtratoRepository;
import cloud.fabriciojunior.repositories.MovimentacaoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Stream;

@RequestScoped
public class ExtratoService {

    @Inject
    Logger logger;

    @Inject
    MovimentacaoRepository movimentacaoRepository;

    @Inject
    ExtratoRepository extratoRepository;

    @Transactional
    public void processar(final Extrato extrato) {
        try(final Stream<String> linhas = Files.lines(Path.of(extrato.getNomeArquivo()))) {
            linhas.skip(1)
                    .forEach(this::createMovimentacaoFrom);

            atualizaStatusExtrato(extrato.getId(), Status.SUCESSO);
        } catch (Exception ex) {
            atualizaStatusExtrato(extrato.getId(), Status.FALHA);
            logger.error("Erro ao processar extrato. " + ex.getMessage());
        }
    }

    private void createMovimentacaoFrom(final String linha) {
        try {
            final String[] colunas = linha.split(",");

            if (colunas.length < 4) {
                logger.info("Linha inválida.");
                return;
            }

            final Movimentacao movimentacao = new Movimentacao();
            movimentacao.setData(LocalDate.parse(colunas[0], DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            movimentacao.setValor(new BigDecimal(colunas[1]));
            movimentacao.setIdentificador(UUID.fromString(colunas[2]));
            movimentacao.setDescricao(colunas[3]);
            movimentacaoRepository.persist(movimentacao);
        } catch (Exception ex) {
            logger.error("Erro ao inserir movimentacação. " + ex.getMessage());
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void atualizaStatusExtrato(final UUID id, final Status status) {
        final Extrato extrato = extratoRepository.findById(id);
        extrato.setStatus(status);
        extratoRepository.persist(extrato);
    }

}
