package cloud.fabriciojunior.dtos;

import cloud.fabriciojunior.entities.Movimentacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoDto(UUID identificador,
                              LocalDate data,
                              BigDecimal valor,
                              String descricao,
                              ExtratoDto extratoDto) {

    public static MovimentacaoDto from(final Movimentacao movimentacao) {
        var extrato = new ExtratoDto(movimentacao.getExtrato().getCodigoBanco(), movimentacao.getExtrato().getNumeroConta());
        return new MovimentacaoDto(movimentacao.getIdentificador(), movimentacao.getData(),
                movimentacao.getValor(), movimentacao.getDescricao(), extrato);
    }

}
