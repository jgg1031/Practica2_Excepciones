package juancarlos.gallego.cskins.repository;

import juancarlos.gallego.cskins.model.ItemInventario;
import juancarlos.gallego.cskins.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventarioRepository extends JpaRepository<ItemInventario, Long> {
    List<ItemInventario> findByUsuario(Usuario usuario);

    ItemInventario findByUsuarioAndNombre(Usuario usuario, String nombre);
}