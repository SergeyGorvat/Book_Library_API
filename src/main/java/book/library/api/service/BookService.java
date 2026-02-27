package book.library.api.service;

import book.library.api.dto.CreateBookDto;
import book.library.api.dto.UpdateBookDto;
import book.library.api.entity.Author;
import book.library.api.entity.Book;
import book.library.api.exception.AuthorNotFoundException;
import book.library.api.exception.BookNotFoundException;
import book.library.api.repository.AuthorRepository;
import book.library.api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book create(CreateBookDto dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(dto.getAuthorId()));

        Book book = Book.builder()
                .title(dto.getTitle())
                .genre(dto.getGenre())
                .year(dto.getYear())
                .description(dto.getDescription())
                .author(author)
                .build();

        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, UpdateBookDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setYear(dto.getYear());
        book.setDescription(dto.getDescription());

        if (dto.getAuthorId() != null) {
            Author author = authorRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new AuthorNotFoundException(dto.getAuthorId()));
            book.setAuthor(author);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        bookRepository.deleteById(id);
    }
}
