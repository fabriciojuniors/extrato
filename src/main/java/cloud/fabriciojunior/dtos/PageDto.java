package cloud.fabriciojunior.dtos;

import java.util.List;

public record PageDto<T> (List<T> content,
                          boolean hasNext,
                          long totalItens) {

}
