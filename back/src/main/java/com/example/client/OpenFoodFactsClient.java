package com.example.client;

import com.example.entity.AlimentEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenFoodFactsClient {

    private static final String BASE_URL = "https://world.openfoodfacts.org";
    private static final String USER_AGENT = "AegerHub/1.0 (contact@example.com)";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<AlimentEntity> searchByName(String query, int pageSize) throws Exception {
        String url = BASE_URL + "/cgi/search.pl?search_terms="
                + query.replace(" ", "+")
                + "&search_simple=1&action=process&json=1&page_size=" + pageSize
                + "&fields=id,product_name,categories_tags,nutriments";

        JsonNode root = get(url);
        JsonNode products = root.path("products");
        List<AlimentEntity> results = new ArrayList<>();
        for (JsonNode p : products) {
            AlimentEntity a = map(p);
            if (a != null) results.add(a);
        }
        return results;
    }

    public List<AlimentEntity> searchByCategory(String categoryTag, int pageSize) throws Exception {
        String url = BASE_URL + "/category/" + categoryTag + ".json?page_size=" + pageSize
                + "&fields=id,product_name,categories_tags,nutriments";

        JsonNode root = get(url);
        JsonNode products = root.path("products");
        List<AlimentEntity> results = new ArrayList<>();
        for (JsonNode p : products) {
            AlimentEntity a = map(p);
            if (a != null) results.add(a);
        }
        return results;
    }

    private JsonNode get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(res.body());
    }

    private AlimentEntity map(JsonNode p) {
        String name = p.path("product_name").asText("").trim();
        if (name.isEmpty()) return null;

        AlimentEntity a = new AlimentEntity();
        a.setOffId(p.path("id").asText(null));
        a.setName(name);

        JsonNode tags = p.path("categories_tags");
        if (tags.isArray()) {
            for (JsonNode t : tags) {
                String tag = t.asText();
                if (tag.startsWith("en:")) {
                    a.setCategory(tag.substring(3));
                    break;
                }
            }
        }

        JsonNode n = p.path("nutriments");
        a.setCalories(nullable(n, "energy-kcal_100g"));
        a.setProteinG(nullable(n, "proteins_100g"));
        a.setCarbohydratesG(nullable(n, "carbohydrates_100g"));
        a.setFatG(nullable(n, "fat_100g"));
        a.setFiberG(nullable(n, "fiber_100g"));
        a.setSugarG(nullable(n, "sugars_100g"));
        a.setSodiumMg(toMg(nullable(n, "sodium_100g"))); 
        a.setCholesterolMg(nullable(n, "cholesterol_100g"));

        a.setVitaminAUg(nullable(n, "vitamin-a_100g"));
        a.setVitaminB1Mg(nullable(n, "vitamin-b1_100g"));
        a.setVitaminB2Mg(nullable(n, "vitamin-b2_100g"));
        a.setVitaminB3Mg(nullable(n, "vitamin-pp_100g")); 
        a.setVitaminB6Mg(nullable(n, "vitamin-b6_100g"));
        a.setVitaminB9Ug(nullable(n, "vitamin-b9_100g"));
        a.setVitaminB12Ug(nullable(n, "vitamin-b12_100g"));
        a.setVitaminCMg(nullable(n, "vitamin-c_100g"));
        a.setVitaminDUg(nullable(n, "vitamin-d_100g"));
        a.setVitaminEMg(nullable(n, "vitamin-e_100g"));
        a.setVitaminKUg(nullable(n, "vitamin-k_100g"));

        a.setCalciumMg(nullable(n, "calcium_100g"));
        a.setIronMg(nullable(n, "iron_100g"));
        a.setMagnesiumMg(nullable(n, "magnesium_100g"));
        a.setPhosphorusMg(nullable(n, "phosphorus_100g"));
        a.setPotassiumMg(nullable(n, "potassium_100g"));
        a.setZincMg(nullable(n, "zinc_100g"));

        return a;
    }

    private Double nullable(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asDouble();
    }

    private Double toMg(Double grams) {
        return grams == null ? null : grams * 1000.0;
    }
}
