package juancarlos.gallego.cskins.dto;
import lombok.Data;

@Data
public class InventoryItem {
    private String nombre;
    private String precioTexto;
    private double precioNumerico;
    private String foto;
    private int cantidad;
    public double getSubtotal() { return this.precioNumerico * this.cantidad; }
}