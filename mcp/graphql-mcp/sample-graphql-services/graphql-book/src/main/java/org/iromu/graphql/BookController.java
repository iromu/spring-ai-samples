package org.iromu.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookController {

	private final List<Book> mockBooks = List.of(new Book("1", "1984", "George Orwell"),
			new Book("2", "Animal Farm", "George Orwell"), new Book("3", "Brave New World", "Aldous Huxley"),
			new Book("4", "Island", "Aldous Huxley"), new Book("5", "Fahrenheit 451", "Ray Bradbury"),
			new Book("6", "The Martian Chronicles", "Ray Bradbury"));

	@QueryMapping
	public Book bookById(@Argument String id) {
		return mockBooks.stream().filter(book -> book.id().equals(id)).findFirst().orElse(null);
	}

	@QueryMapping
	public List<Book> allBooks() {
		return mockBooks;
	}

	@QueryMapping
	public List<Book> booksByAuthor(@Argument String author) {
		return mockBooks.stream().filter(book -> book.author().toLowerCase().contains(author.toLowerCase())).toList();
	}

}
