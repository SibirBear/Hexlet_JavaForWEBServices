package io.hexlet.exercise.res;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Iterator;
import java.util.Random;

// *my.site.com*/api/links
@Path("links")
public class LinkResources {

    private static final String ID_KEY = "id";
    private static final String URL_KEY = "url";
    private static final Response ANSWER_404
            = Response.status(Response.Status.NOT_FOUND).build();

    private static final MongoCollection<Document> LINKS_CONNECTION;
    private static final int ATTEMPT_NUMBER = 5;

    static {
        final MongoClient mongo = new MongoClient( "localhost" , 27017 );
        final MongoDatabase db = mongo.getDatabase("test");
        LINKS_CONNECTION = db.getCollection("links");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public Response getUrlById(final @PathParam("id") String id) {
        if (id == null || id.isEmpty()) {
            return ANSWER_404;
        }

        // @Iterator - because in DB each record with unique ID
        // @foreach - it can be used if there is no unique id and
        //            there can be many records of it
        final FindIterable<Document> resultsIterable
                = LINKS_CONNECTION.find(new Document(ID_KEY, id));
        final Iterator<Document> resultIterator = resultsIterable.iterator();
        if (!resultIterator.hasNext()) {
            return ANSWER_404;
        }
        final String url = resultIterator.next().getString(URL_KEY);
        if (url == null || url == "") {
            return ANSWER_404;
        }
        return Response.ok(url).build();
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response shortUrl(final String url) {
        int attempt = 0;
        while (attempt < ATTEMPT_NUMBER) {
            final String id = getRandomId();
            final Document newShortDoc = new Document(ID_KEY, id);
            newShortDoc.put(URL_KEY, url);
            try {
                LINKS_CONNECTION.insertOne(newShortDoc);
                return Response.ok(id).build();
            } catch (MongoWriteException e) {
                // attempt to write failed, ID - exists.
            }
            attempt++;
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private static String getRandomId() {
        String possibleCharacters = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder idBuilder = new StringBuilder();
        Random rnd = new Random();
        while (idBuilder.length() < 5) {
            int index = (int) (rnd.nextFloat() * possibleCharacters.length());
            idBuilder.append(possibleCharacters.charAt(index));
        }
        return idBuilder.toString();
    }

}