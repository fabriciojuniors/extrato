package cloud.fabriciojunior.entities;

import cloud.fabriciojunior.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "extratos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Extrato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "data_recepcao", nullable = false)
    private LocalDate dataRecepcao = LocalDate.now();

    @Column(name = "data_processamento")
    private LocalDate dataProcessamento;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDENTE;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "codigo_banco")
    private String codigoBanco;

    @Column(name = "numero_conta")
    private String numeroConta;

    @OneToMany(mappedBy = "extrato")
    private List<Movimentacao> movimentacoes = new ArrayList<>();

}
