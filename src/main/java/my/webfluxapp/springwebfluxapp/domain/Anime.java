package my.webfluxapp.springwebfluxapp.domain;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author
 * @return
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("anime")
public class Anime {
    @Id
    private Long id;
    @NotNull
    @NotEmpty(message = "The name of this anime cannot by empty")
    private String name;
}
