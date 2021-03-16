package com.distribuida.rest;


import com.distribuida.model.Author;
import com.distribuida.model.AuthorResource;
import com.distribuida.model.Book;
import com.distribuida.model.BookResource;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/authors")
public class AuthorRest {

    @Inject
    AuthorResource authorResource;


    @Inject
    BookResource bookResource;


    @GET
    @Path("{id}")
    @Produces("application/json")
    public Author get(@PathParam("id") Long id){
        Author author = authorResource.get(id);
        if (author == null) {
            throw new WebApplicationException(404);
        }
        return author;
    }

    @GET
    @Produces("application/json")
    public List<Author> list(@QueryParam("sort") List<String> sortQuery,
                             @QueryParam("page") @DefaultValue("0") int pageIndex,
                             @QueryParam("size") @DefaultValue("20") int pageSize) {
        Page page = Page.of(pageIndex, pageSize);
        Sort sort = Sort.by("id");

        return authorResource.list(page, sort);
    }

    @GET
    @Path("{id}/books")
    @Produces("application/json")
    public List<Book> getBooksByAuthor(@PathParam("id") Long id) {
        return bookResource.list(Page.of(0,20), Sort.by("id"))
                .stream().filter(s -> s.getAuthor_id().equals(id))
                .collect(Collectors.toList());
    }

    @Transactional
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(Author author) {
        authorResource.add(author);

        return Response.status(201).build();
    }

    @Transactional
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response update(@PathParam("id") Long id, Author authorToSave) {
        if (authorResource.get(id) == null) {
            Author author = authorResource.update(id, authorToSave);
            return Response.status(204).build();
        }
        Author author = authorResource.update(id, authorToSave);
        return Response.status(201).build();
    }

    @Transactional
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        if (!authorResource.delete(id)) {
            throw new WebApplicationException(404);
        }
    }
}
