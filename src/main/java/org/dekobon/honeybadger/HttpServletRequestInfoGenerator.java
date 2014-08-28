package org.dekobon.honeybadger;

import com.google.gson.JsonObject;
import org.apache.http.HttpHeaders;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Parses the properties of a {@link javax.servlet.http.HttpServletRequest}
 * object and turns it into the Honeybadger JSON format.
 *
 * @author <a href="https://github.com/dekobon">dekobon</a>
 * @since 1.0.0
 */
public class HttpServletRequestInfoGenerator
        implements RequestInfoGenerator<HttpServletRequest> {
    @Override
    public JsonObject generateRequest(HttpServletRequest request) {
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("url", request.getRequestURI());
        jsonRequest.add("cgi_data", cgiData(request));
        jsonRequest.add("params", params(request));

        return jsonRequest;
    }

    @Override
    public JsonObject routeRequest(Object requestSource) {
        if (!(requestSource instanceof HttpServletRequest)) {
            throw new HoneybadgerException("Request object is not instance " +
                    "of HttpServletRequest");
        }
        return generateRequest((HttpServletRequest) requestSource);
    }

    protected JsonObject cgiData(HttpServletRequest request) {
        JsonObject cgiData = new JsonObject();

        cgiData.addProperty("REQUEST_METHOD", request.getMethod());
        cgiData.addProperty("HTTP_ACCEPT", request.getHeader(HttpHeaders.ACCEPT));
        cgiData.addProperty("HTTP_USER_AGENT", request.getHeader(HttpHeaders.USER_AGENT));
        cgiData.addProperty("HTTP_ACCEPT_ENCODING", request.getHeader(HttpHeaders.ACCEPT_ENCODING));
        cgiData.addProperty("HTTP_ACCEPT_LANGUAGE", request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
        cgiData.addProperty("HTTP_ACCEPT_CHARSET", request.getHeader(HttpHeaders.ACCEPT_CHARSET));
        cgiData.addProperty("SERVER_NAME", request.getServerName());
        cgiData.addProperty("SERVER_PORT", request.getServerPort());
        cgiData.addProperty("CONTENT_TYPE", request.getContentType());
        cgiData.addProperty("CONTENT_LENGTH", request.getContentLength());
        cgiData.addProperty("REMOTE_ADDR", request.getRemoteAddr());
        cgiData.addProperty("REMOTE_PORT", request.getRemotePort());
        cgiData.addProperty("QUERY_STRING", request.getQueryString());
        cgiData.addProperty("PATH_INFO", request.getPathInfo());

        return cgiData;
    }

    protected JsonObject params(HttpServletRequest request) {
        JsonObject jsonParams = new JsonObject();

        {
            JsonObject requestParams = new JsonObject();

            try {
                for (Part part : request.getParts()) {
                    requestParams.addProperty(part.getName(), part.toString());
                }
            } catch (IOException | ServletException e) {
                throw new HoneybadgerException(
                        "Unable to parse servlet request parts", e);
            }

            jsonParams.add("request_params", requestParams);
        }

        jsonParams.add("request_headers", httpHeaders(request));

        return jsonParams;
    }

    protected JsonObject httpHeaders(HttpServletRequest request) {
        JsonObject jsonHeaders = new JsonObject();

        Enumeration<String> headers = request.getHeaderNames();

        if (headers != null) {
            while (headers.hasMoreElements()) {
                String name = headers.nextElement();
                jsonHeaders.addProperty(name, request.getHeader(name));
            }
        }

        return jsonHeaders;
    }
}