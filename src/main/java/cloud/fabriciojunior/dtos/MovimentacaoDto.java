package cloud.fabriciojunior.dtos;

import cloud.fabriciojunior.entities.Movimentacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoDto(UUID identificador,
                              LocalDate data,
                              BigDecimal valor,
                              String descricao) {

    public static MovimentacaoDto from(final Movimentacao movimentacao) {
        return new MovimentacaoDto(movimentacao.getIdentificador(), movimentacao.getData(),
                movimentacao.getValor(), movimentacao.getDescricao());
    }

}
