package juancarlos.gallego.cskins.controller;

import juancarlos.gallego.cskins.dto.SkinGroup;
import juancarlos.gallego.cskins.model.ItemInventario;
import juancarlos.gallego.cskins.model.Usuario;
import juancarlos.gallego.cskins.repository.InventarioRepository;
import juancarlos.gallego.cskins.repository.UsuarioRepository;
import juancarlos.gallego.cskins.service.PriceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private final PriceService priceService;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;

    public WebController(PriceService priceService, InventarioRepository inventarioRepository, UsuarioRepository usuarioRepository) {
        this.priceService = priceService;
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index(@RequestParam(name = "query", defaultValue = "") String query,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        Model model) {

        List<SkinGroup> todasLasSkins = priceService.buscarArmasCatalogo(query);
        int pageSize = 8;
        int totalSkins = todasLasSkins.size();
        int totalPages = (int) Math.ceil((double) totalSkins / pageSize);

        page = Math.max(1, Math.min(page, totalPages == 0 ? 1 : totalPages));

        List<SkinGroup> skinsPaginadas = new ArrayList<>();
        if (totalSkins > 0) {
            int startItem = (page - 1) * pageSize;
            int endItem = Math.min(startItem + pageSize, totalSkins);
            skinsPaginadas = todasLasSkins.subList(startItem, endItem);
        }

        model.addAttribute("skins", skinsPaginadas);
        model.addAttribute("busqueda", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "index";
    }

    @GetMapping("/skin")
    public String detalleSkin(@RequestParam("nombre") String nombre,
                              @RequestParam("foto") String foto,
                              Model model) {

        SkinGroup precios = priceService.obtenerPreciosArma(nombre, foto);

        model.addAttribute("nombre", nombre);
        model.addAttribute("foto", foto);
        model.addAttribute("fn", precios.getFnPrice() != null ? precios.getFnPrice() : "-");
        model.addAttribute("mw", precios.getMwPrice() != null ? precios.getMwPrice() : "-");
        model.addAttribute("ft", precios.getFtPrice() != null ? precios.getFtPrice() : "-");
        model.addAttribute("ww", precios.getWwPrice() != null ? precios.getWwPrice() : "-");
        model.addAttribute("bs", precios.getBsPrice() != null ? precios.getBsPrice() : "-");

        return "detalle";
    }

    @PostMapping("/add-inventory")
    public String añadirAlInventario(@RequestParam("nombre") String nombre,
                                     @RequestParam("calidad") String calidad,
                                     @RequestParam("precio") String precio,
                                     @RequestParam("foto") String foto,
                                     Principal principal) {

        if (precio.equals("-") || principal == null) return "redirect:/";

        Usuario usuarioLogueado = usuarioRepository.findByUsername(principal.getName());
        String nombreCompleto = nombre + " (" + calidad + ")";

        ItemInventario itemExistente = inventarioRepository.findByUsuarioAndNombre(usuarioLogueado, nombreCompleto);

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + 1);
            inventarioRepository.save(itemExistente);
        } else {
            ItemInventario nuevoItem = new ItemInventario();
            nuevoItem.setUsuario(usuarioLogueado);
            nuevoItem.setNombre(nombreCompleto);
            nuevoItem.setPrecioTexto(precio);
            nuevoItem.setFoto(foto);
            nuevoItem.setCantidad(1);

            try {
                String precioLimpio = precio.replaceAll("[^0-9,.]", "").replace(",", ".");
                int lastDot = precioLimpio.lastIndexOf(".");
                if (lastDot != -1) {
                    precioLimpio = precioLimpio.substring(0, lastDot).replace(".", "") + "." + precioLimpio.substring(lastDot + 1);
                }
                nuevoItem.setPrecioNumerico(Double.parseDouble(precioLimpio));
            } catch (Exception e) {
                nuevoItem.setPrecioNumerico(0.0);
            }

            inventarioRepository.save(nuevoItem);
        }

        return "redirect:/inventory";
    }

    @PostMapping("/remove-inventory")
    public String eliminarDelInventario(@RequestParam("nombre") String nombre,
                                        @RequestParam(value = "cantidadRemover", defaultValue = "1") int cantidadRemover,
                                        Principal principal) {

        if (principal == null) return "redirect:/login";

        Usuario usuarioLogueado = usuarioRepository.findByUsername(principal.getName());
        ItemInventario item = inventarioRepository.findByUsuarioAndNombre(usuarioLogueado, nombre);

        if (item != null) {
            if (cantidadRemover >= item.getCantidad()) {
                inventarioRepository.delete(item);
            } else {
                item.setCantidad(item.getCantidad() - cantidadRemover);
                inventarioRepository.save(item);
            }
        }
        return "redirect:/inventory";
    }

    @GetMapping("/inventory")
    public String verInventario(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";

        Usuario usuarioLogueado = usuarioRepository.findByUsername(principal.getName());
        List<ItemInventario> inventario = inventarioRepository.findByUsuario(usuarioLogueado);

        double valorTotal = inventario.stream().mapToDouble(ItemInventario::getSubtotal).sum();

        model.addAttribute("inventario", inventario);
        model.addAttribute("valorTotal", String.format("%.2f", valorTotal));

        return "inventory";
    }
}