package cloud.fabriciojunior.services;

import cloud.fabriciojunior.config.RegraNegocioException;
import cloud.fabriciojunior.entities.Extrato;
import cloud.fabriciojunior.entities.Movimentacao;
import cloud.fabriciojunior.entities.enums.Status;
import cloud.fabriciojunior.repositories.ExtratoRepository;
import cloud.fabriciojunior.repositories.MovimentacaoRepository;
import com.webcohesion.ofx4j.domain.data.MessageSetType;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

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
        try(var inputStream = new FileInputStream(extrato.getNomeArquivo())) {
            var aggregateUnmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
            var response = aggregateUnmarshaller.unmarshal(inputStream);
            var message = response.getMessageSet(MessageSetType.banking);

            if (message == null) {
                throw  new RegraNegocioException("Não foi possível ler as transações do extrato");
            }

            var responses = ((BankingResponseMessageSet) message).getStatementResponses();
            var bankTransaction = responses.stream().findFirst();

            if (bankTransaction.isPresent()) {
                var bank = bankTransaction.get();
                var transactions = bank.getMessage().getTransactionList().getTransactions();
                for (Transaction transaction : transactions) {
                    createMovimentacaoFrom(transaction, extrato);
                }

                finalizaProcessamentoExtrato(extrato.getId(), bank.getMessage().getAccount());
            }
        } catch (Exception ex) {
            atualizaStatusExtrato(extrato.getId(), Status.FALHA);
            logger.error("Erro ao processar extrato. " + ex.getMessage());
        }
    }

    private void createMovimentacaoFrom(Transaction transaction, Extrato extrato) {
        try {
            var descricao = new String(transaction.getMemo().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            final Movimentacao movimentacao = new Movimentacao();

            movimentacao.setExtrato(extrato);
            movimentacao.setData(LocalDate.ofInstant(transaction.getDatePosted().toInstant(), ZoneId.systemDefault()));
            movimentacao.setValor(transaction.getBigDecimalAmount());
            movimentacao.setIdentificador(UUID.randomUUID());
            movimentacao.setDescricao(descricao);

            movimentacaoRepository.persist(movimentacao);
        } catch (Exception ex) {
            logger.error("Erro ao inserir movimentacação. " + ex.getMessage());
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void atualizaStatusExtrato(final UUID id, final Status status) {
        final Extrato extrato = extratoRepository.findById(id);
        extrato.setStatus(status);
        extrato.setDataProcessamento(LocalDate.now());
        extratoRepository.persist(extrato);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void finalizaProcessamentoExtrato(final UUID id, final BankAccountDetails conta) {
        final Extrato extrato = extratoRepository.findById(id);
        extrato.setStatus(Status.SUCESSO);
        extrato.setNumeroConta(conta.getAccountNumber());
        extrato.setCodigoBanco(conta.getBankId());
        extrato.setDataProcessamento(LocalDate.now());
        extratoRepository.persist(extrato);
    }

}
