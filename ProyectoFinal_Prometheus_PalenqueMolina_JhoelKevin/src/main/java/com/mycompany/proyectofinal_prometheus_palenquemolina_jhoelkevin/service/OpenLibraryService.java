/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mycompany.proyectofinal_prometheus_palenquemolina_jhoelkevin.model.Libro;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author isard
 */
public class OpenLibraryService {
    private static final String SEARCH_URL = "https://openlibrary.org/search.json?title=";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public List<Libro> buscarLibros(String titulo) throws IOException, InterruptedException {
        String q = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(SEARCH_URL + q))
            .GET()
            .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        SearchResponse sr = gson.fromJson(resp.body(), SearchResponse.class);

        List<Libro> resultados = new ArrayList<>();
        if (sr.docs != null) {
            for (Doc d : sr.docs) {
                Libro l = new Libro();
                l.setTitulo(d.title);
                l.setAutor(d.authorName != null && !d.authorName.isEmpty()
                    ? d.authorName.get(0) : "Autor desconocido");
                if (d.coverI != null) {
                    l.setPortadaURL("https://covers.openlibrary.org/b/id/"
                        + d.coverI + "-M.jpg");
                }
                l.setSinopsis(d.firstSentence != null 
                    ? d.firstSentence : "");
                l.setContenido("");
                resultados.add(l);
            }
        }
        return resultados;
    }

    // Clases internas para mapear json
    private static class SearchResponse {
        List<Doc> docs;
    }
    private static class Doc {
        String title;
        @SerializedName("author_name")
        List<String> authorName;
        @SerializedName("cover_i")
        Integer coverI;
        @SerializedName("first_sentence")
        String firstSentence;
    }
}
