package com.rotomer.stanford_corenlp_wrapper;

public class Main {
    @SuppressWarnings("FieldCanBeLocal")
    private static OpenIEInvoker _openIEInvoker;

    public static void main(final String[] args) {
        _openIEInvoker = new OpenIEInvoker();
        final HttpServer httpServer = new HttpServer(_openIEInvoker);
        httpServer.startHttpServer();
    }
}
