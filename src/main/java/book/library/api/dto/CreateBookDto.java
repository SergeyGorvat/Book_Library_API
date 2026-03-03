package book.library.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookDto {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Жанр обязателен")
    private String genre;

    @NotNull(message = "Год обязателен")
    @Min(value = 1, message = "Год должен быть положительным")
    private Integer year;

    private String description;

    @NotNull(message = "Автор обязателен")
    private Long authorId;
}
