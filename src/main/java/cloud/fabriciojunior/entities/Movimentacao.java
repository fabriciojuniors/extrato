package cloud.fabriciojunior.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "movimentacoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "identificador", unique = true)
    private UUID identificador;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "descricao")
    private String descricao;

    @JoinColumn(name = "id_extrato")
    @ManyToOne(fetch = FetchType.LAZY)
    private Extrato extrato;

}
