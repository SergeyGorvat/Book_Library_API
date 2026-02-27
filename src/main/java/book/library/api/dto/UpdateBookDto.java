package book.library.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDto {

    private String title;

    private String genre;

    @Min(value = 1, message = "Год должен быть положительным")
    private Integer year;

    private String description;

    private Long authorId;
}