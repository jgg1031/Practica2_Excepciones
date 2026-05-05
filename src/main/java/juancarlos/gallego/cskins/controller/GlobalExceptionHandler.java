package juancarlos.gallego.cskins.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleResourceNotFound(NoResourceFoundException ex) {
    }

    @ExceptionHandler(RestClientResponseException.class)
    public String handleRestClientException(RestClientResponseException ex, Model model) {
        model.addAttribute("errorTitulo", "Error de Conexión Externa");
        model.addAttribute("errorMensaje", "Problemas con los servidores de Steam.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        model.addAttribute("errorTitulo", "¡Ups! Algo ha salido mal");
        model.addAttribute("errorMensaje", "Error interno del servidor.");
        return "error";
    }
}