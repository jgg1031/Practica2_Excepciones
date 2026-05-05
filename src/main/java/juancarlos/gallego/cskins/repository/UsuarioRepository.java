package juancarlos.gallego.cskins.repository;

import juancarlos.gallego.cskins.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByUsername(String username);
}