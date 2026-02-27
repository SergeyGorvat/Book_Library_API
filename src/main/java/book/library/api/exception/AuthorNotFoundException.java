package book.library.api.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long id) {
        super("Автор с id= " + id + " не найден");
    }
}
