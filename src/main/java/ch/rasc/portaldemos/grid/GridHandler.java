package ch.rasc.portaldemos.grid;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.Fn;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Reply;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Bean
public class GridHandler {

	@Wire("grid")
	Room room;

	@On.close
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

	@On("bookRead")
	public void bookCreate(@Data StoreReadRequest readRequest, @Reply Fn.Callback1<Collection<Book>> reply) {
		Collection<Book> list = BookDb.list();

		Ordering<Book> ordering = PropertyOrderingFactory.createOrderingFromSorters(readRequest.getSorters());
		if (ordering != null) {
			reply.call(ordering.sortedCopy(list));
		} else {
			reply.call(list);
		}
	}

	@On("bookCreate")
	public void bookCreate(Socket socket, @Data Book[] books, @Reply Fn.Callback1<List<Book>> reply) {
		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.create(book);
			result.add(book);
		}

		room.out(socket).send("bookCreated", result);
		reply.call(result);
	}

	@On("bookUpdate")
	public void bookUpdate(Socket socket, @Data Book[] books, @Reply Fn.Callback1<List<Book>> reply) {
		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.update(book);
			result.add(book);
		}

		room.out(socket).send("bookUpdated", result);
		reply.call(result);
	}

	@On("bookDestroy")
	public void bookDestroy(Socket socket, @Data Integer[] bookIds, @Reply Fn.Callback1<Boolean> reply) {
		BookDb.delete(Arrays.asList(bookIds));
		room.out(socket).send("bookDestroyed", bookIds);
		reply.call(true);
	}

}
