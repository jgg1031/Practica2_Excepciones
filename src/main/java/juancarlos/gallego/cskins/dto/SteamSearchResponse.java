package juancarlos.gallego.cskins.dto;
import lombok.Data;
import java.util.List;

@Data
public class SteamSearchResponse {
    private List<SkinResult> results;
    @Data
    public static class SkinResult {
        private String name;
        private String sell_price_text;
        private AssetDescription asset_description;
    }
    @Data
    public static class AssetDescription {
        private String icon_url;
    }
}