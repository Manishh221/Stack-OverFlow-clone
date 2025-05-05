package io.mountblue.StackOverflow.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientReputationException.class)
    public String handleInsufficientReputation(InsufficientReputationException ex,
                                               RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("reputationError", ex.getMessage());
        return "redirect:/questions";
    }
}