package com.website.blogs.controllers;

import com.website.blogs.dtos.RegistrationUserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final Validator validator;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/register")
    public String registrationPage(@ModelAttribute("registrationUserDTO") RegistrationUserDTO registrationUserDTO){
        return "registration";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid RegistrationUserDTO registrationUserDTO, Errors errors){

        if(errors.hasErrors()){
            logger.info("VALIDATION SRABOTAL");
            return "registration";
        }

        logger.info(registrationUserDTO.toString());
        return "registration";
    }
}
