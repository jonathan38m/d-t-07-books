package com.distribuida.rest;

import com.distribuida.model.Book;
import com.distribuida.model.BookResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/books")
public class BookRest {

    @Inject
    BookResource bookResource;

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Book get(@PathParam("id") Long id){
        Book book = bookResource.get(id);
        if (book == null) {
            throw new WebApplicationException(404);
        }
        return book;
    }

    @GET
    @Produces("application/json")
    public List<Book> list(@QueryParam("sort") List<String> sortQuery,
                             @QueryParam("page") @DefaultValue("0") int pageIndex,
                             @QueryParam("size") @DefaultValue("20") int pageSize) {
        Page page = Page.of(pageIndex, pageSize);
        Sort sort = Sort.by("id");

        return bookResource.list(page, sort);
    }


    @Transactional
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(Book book) {
        bookResource.add(book);

        return Response.status(201).build();
    }

    @Transactional
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response update(@PathParam("id") Long id, Book bookSave) {
        if (bookResource.get(id) == null) {
            Book book = bookResource.update(id, bookSave);
            return Response.status(204).build();
        }
        Book book = bookResource.update(id, bookSave);
        return Response.status(201).build();
    }

    @Transactional
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        if (!bookResource.delete(id)) {
            throw new WebApplicationException(404);
        }
    }
}
