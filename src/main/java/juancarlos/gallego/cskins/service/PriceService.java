package juancarlos.gallego.cskins.service;

import juancarlos.gallego.cskins.dto.SkinGroup;
import juancarlos.gallego.cskins.dto.SteamSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PriceService {

    private final RestClient restClient;
    private final String PYTHON_API_URL = "http://localhost:5000/api/cs2";

    public PriceService() {
        this.restClient = RestClient.create();
    }

    public List<SkinGroup> buscarArmasCatalogo(String query) {
        Map<String, SkinGroup> agrupados = new LinkedHashMap<>();

        String url = PYTHON_API_URL + "/catalog?query=" + query;

        try {
            SteamSearchResponse response = restClient.get().uri(url).retrieve().body(SteamSearchResponse.class);

            if (response != null && response.getResults() != null) {
                for (SteamSearchResponse.SkinResult item : response.getResults()) {
                    String rawName = item.getName();
                    int bracketIndex = rawName.lastIndexOf("(");

                    if (bracketIndex != -1) {
                        String baseName = rawName.substring(0, bracketIndex).trim();
                        agrupados.computeIfAbsent(baseName, k -> {
                            SkinGroup newGroup = new SkinGroup();
                            newGroup.setBaseName(baseName);
                            if (item.getAsset_description() != null) {
                                newGroup.setIconUrl(item.getAsset_description().getIcon_url());
                            }
                            return newGroup;
                        });
                    }
                }
            }
        } catch (RestClientResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado en el catálogo", e);
        }
        return new ArrayList<>(agrupados.values());
    }

    public SkinGroup obtenerPreciosArma(String baseName, String fotoUrl) {
        SkinGroup group = new SkinGroup();
        group.setBaseName(baseName);
        group.setIconUrl(fotoUrl);

        try {
            String queryCodificada = URLEncoder.encode(baseName, StandardCharsets.UTF_8);

            String url = PYTHON_API_URL + "/details?baseName=" + queryCodificada;

            SteamSearchResponse response = restClient.get().uri(url).retrieve().body(SteamSearchResponse.class);

            if (response != null && response.getResults() != null) {
                for (SteamSearchResponse.SkinResult item : response.getResults()) {
                    if (item.getName().contains(baseName)) {
                        String rawName = item.getName();
                        int bracketIndex = rawName.lastIndexOf("(");

                        if (bracketIndex != -1) {
                            String wear = rawName.substring(bracketIndex + 1, rawName.length() - 1);
                            String price = item.getSell_price_text();

                            if (price != null) {
                                switch (wear) {
                                    case "Factory New" -> group.setFnPrice(price);
                                    case "Minimal Wear" -> group.setMwPrice(price);
                                    case "Field-Tested" -> group.setFtPrice(price);
                                    case "Well-Worn" -> group.setWwPrice(price);
                                    case "Battle-Scarred" -> group.setBsPrice(price);
                                }
                            }
                        }
                    }
                }
            }
        } catch (RestClientResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado obteniendo precios", e);
        }
        return group;
    }
}