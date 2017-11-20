package com.zenika.zencontact.resource;

import com.google.appengine.api.blobstore.BlobKey;
import com.zenika.zencontact.domain.blob.PhotoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Clément Garbay
 */
@WebServlet(name = "PhotoResource", value = "/api/v0/photo/*")
public class PhotoResource extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = getId(request);
        if (id == null) {
            response.setStatus(404);
            return;
        }

        PhotoService.getInstance().updatePhoto(id, request);

        response.setContentType("text/plain");
        response.getWriter().println("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = getId(request);
        if (id == null) {
            response.setStatus(404);
            return;
        }

        PhotoService.getInstance().serve(new BlobKey(getBlobKey(request)), response);

        response.setContentType("text/plain");
        response.getWriter().println("");
    }

    private Optional<String[]> getPathParts(HttpServletRequest request) {
        String pathInfo = request.getPathInfo(); // /{id}
        String[] pathParts = pathInfo.split("/");
        return (pathParts.length == 0) ? Optional.empty() : Optional.of(pathParts);
    }

    private Long getId(HttpServletRequest request) {
        return Long.valueOf(getPathParts(request).map(parts -> parts[1]).orElse(null));
    }

    private String getBlobKey(HttpServletRequest request) {
        return getPathParts(request).map(parts -> parts[2]).orElse(null);
    }
}
