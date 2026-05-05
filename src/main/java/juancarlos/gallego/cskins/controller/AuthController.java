package juancarlos.gallego.cskins.controller;

import juancarlos.gallego.cskins.model.Usuario;
import juancarlos.gallego.cskins.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String showRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String doRegistro(@RequestParam String username, @RequestParam String password, Model model) {
        if (usuarioRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Ese nombre de agente ya está en uso.");
            return "registro";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);

        nuevoUsuario.setPassword(passwordEncoder.encode(password));

        usuarioRepository.save(nuevoUsuario);

        model.addAttribute("success", "Cuenta creada con éxito. ¡Ya puedes acceder!");
        return "login";
    }

}