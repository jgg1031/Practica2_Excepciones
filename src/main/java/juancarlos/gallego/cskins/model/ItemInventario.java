package juancarlos.gallego.cskins.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String nombre;
    private String precioTexto;
    private Double precioNumerico;
    private String foto;
    private int cantidad;

    public double getSubtotal() {
        return (this.precioNumerico != null ? this.precioNumerico : 0.0) * this.cantidad;
    }
}