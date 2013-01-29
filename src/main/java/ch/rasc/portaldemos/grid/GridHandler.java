package ch.rasc.portaldemos.grid;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Reply;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Bean
public class GridHandler {

	@Wire
	Room room;

	@On
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
	}

	@On
	public void open(Socket socket) {
		room.add(socket);
	}

	@On
	@Reply
	public Collection<Book> bookRead(@Data StoreReadRequest readRequest) {
		Collection<Book> list = BookDb.list();
		Ordering<Book> ordering = PropertyOrderingFactory.createOrderingFromSorters(readRequest.getSorters());

		return ordering != null ? ordering.sortedCopy(list) : list;
	}

	@On
	@Reply
	public List<Book> bookCreate(Socket socket, @Data Book[] books) {
		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.create(book);
			result.add(book);
		}

		room.out(socket).send("bookCreated", result);
		return result;
	}

	@On
	@Reply
	public List<Book> bookUpdate(Socket socket, @Data Book[] books) {
		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.update(book);
			result.add(book);
		}

		room.out(socket).send("bookUpdated", result);
		return result;
	}

	@On
	@Reply
	public boolean bookDestroy(Socket socket, @Data Integer[] bookIds) {
		BookDb.delete(Arrays.asList(bookIds));
		room.out(socket).send("bookDestroyed", bookIds);
		return true;
	}

}
