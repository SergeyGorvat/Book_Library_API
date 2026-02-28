package book.library.api.service;

import book.library.api.dto.CreateBookDto;
import book.library.api.dto.UpdateBookDto;
import book.library.api.entity.Author;
import book.library.api.entity.Book;
import book.library.api.exception.BookNotFoundException;
import book.library.api.repository.AuthorRepository;
import book.library.api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Author author;

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .firstName("Александр")
                .lastName("Пушкин")
                .build();

        book = Book.builder()
                .id(1L)
                .title("Евгений Онегин")
                .genre("Роман в стихах")
                .year(1833)
                .author(author)
                .build();
    }

    @Test
    @DisplayName("findAll — возвращает Page с книгами")
    void findAll_ReturnsPage() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<Book> page = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<Book> result = bookService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Евгений Онегин");
    }

    @Test
    @DisplayName("findAll — пагинация: возвращает нужную страницу")
    void findAll_ReturnsCorrectPage() {
        PageRequest firstPage = PageRequest.of(0, 2);
        PageRequest secondPage = PageRequest.of(1, 2);

        Book book2 = Book.builder()
                .id(2L)
                .title("Анна Каренина")
                .genre("Роман")
                .year(1877)
                .author(author)
                .build();

        Book book3 = Book.builder()
                .id(3L)
                .title("Воскресение")
                .genre("Роман")
                .year(1899)
                .author(author)
                .build();

        when(bookRepository.findAll(firstPage)).thenReturn(new PageImpl<>(List.of(book, book2), firstPage, 3));
        when(bookRepository.findAll(secondPage)).thenReturn(new PageImpl<>(List.of(book3), secondPage, 3));

        Page<Book> page1 = bookService.findAll(firstPage);
        Page<Book> page2 = bookService.findAll(secondPage);

        assertThat(page1.getContent()).hasSize(2);
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(page2.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("findById — книга найдена")
    void findById_BookExists_ReturnsBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Евгений Онегин");
    }

    @Test
    @DisplayName("findById — книга не найдена → BookNotFoundException")
    void findById_BookNotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create — успешное создание книги")
    void create_ValidDto_ReturnsBook() {
        CreateBookDto dto = new CreateBookDto("Евгений Онегин", "Роман в стихах", 1869, null, 1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.create(dto);

        assertThat(result.getTitle()).isEqualTo("Евгений Онегин");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("update — успешное обновление названия")
    void update_ValidDto_UpdatesBook() {
        UpdateBookDto dto = new UpdateBookDto("Новое название", null, null, null, null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Book result = bookService.update(1L, dto);

        assertThat(result.getTitle()).isEqualTo("Новое название");
        assertThat(result.getGenre()).isEqualTo(null);
    }

    @Test
    @DisplayName("delete — успешное удаление")
    void delete_BookExists_Deletes() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.delete(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete — книга не найдена → BookNotFoundException")
    void delete_BookNotFound_ThrowsException() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).deleteById(any());
    }
}