package com.rotomer.stanford_corenlp_wrapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.Map;

import static spark.Spark.port;
import static spark.Spark.post;

class HttpServer {

    private static final int PORT = 9500;

    private final OpenIEInvoker _openIEInvoker;
    private final ObjectMapper _jsonParser;

    HttpServer(final OpenIEInvoker openIEInvoker) {
        _openIEInvoker = openIEInvoker;
        _jsonParser = new ObjectMapper();
    }

    void startHttpServer() {
        System.out.println("HTTP Server - initializing... port:" + PORT);

        port(PORT);
        post("/indexRelations", this::handleRequest);

        System.out.println("HTTP Server - finished initialization . port:" + PORT);
    }

    private String handleRequest(final Request request, final Response response) {

        final Map<String, String> params = parseParams(request);
        final String inputFilePathStr = params.get("inputFilePath");
        final String outputFolderPathStr = params.get("outputFolderPath");

        _openIEInvoker.indexRelations(inputFilePathStr, outputFolderPathStr);

        return "OK";
    }

    private Map<String, String> parseParams(final Request request) {
        final String json = request.body();

        try {
            return _jsonParser.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (final IOException e) {
            throw new RuntimeException("Failed to parse request params.", e);
        }
    }
}
