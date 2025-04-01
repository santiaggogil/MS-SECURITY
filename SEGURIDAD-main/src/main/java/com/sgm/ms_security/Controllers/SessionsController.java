package com.sgm.ms_security.Controllers;

import com.sgm.ms_security.Models.Session;
import com.sgm.ms_security.Models.User;
import com.sgm.ms_security.Repositories.SessionRepository;
import com.sgm.ms_security.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/sessions")
public class SessionsController {

    @Autowired
    private SessionRepository theSessionRepository;

    @Autowired
    private UserRepository theUserRepository;

    @GetMapping("")
    public List<Session> findAll() {
        return this.theSessionRepository.findAll();
    }

    @GetMapping("{id}")
    public Session findById(@PathVariable String id) {
        return this.theSessionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Session create(@RequestBody Session newSession) {
        return this.theSessionRepository.save(newSession);
    }

    @PutMapping("{id}")
    public Session update(@PathVariable String id, @RequestBody Session newSession) {
        Session actualSession = this.theSessionRepository.findById(id).orElse(null);
        if (actualSession != null) {
            actualSession.setToken(newSession.getToken());
            actualSession.setExpiration(newSession.getExpiration());
            actualSession.setCode2FA(newSession.getCode2FA());
            return this.theSessionRepository.save(actualSession);
        }
        return null;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Session theSession = this.theSessionRepository.findById(id).orElse(null);
        if (theSession != null) {
            this.theSessionRepository.delete(theSession);
        }
    }

    //endPoint para crear el match de las sesiones con usuario
    @PutMapping("{session_id}/user/{user_id}") // Configuración de la ruta.
    public Session matchSessionUser(@PathVariable String session_id, @PathVariable String user_id) {
        Session theActualSession = this.theSessionRepository.findById(session_id).orElse(null);
        User theActualUser = this.theUserRepository.findById(user_id).orElse(null); // Eliminado .getUser()

        if (theActualSession != null && theActualUser != null) {
            theActualSession.setUser(theActualUser);
            return this.theSessionRepository.save(theActualSession);
        } else {
            return null;
        }
    }

    //desAsociar sesion
    @PutMapping("{session_id}/user") // Configuración de la ruta.
    public Session unMatchSessionUser(@PathVariable String session_id) {
        Session theActualSession = this.theSessionRepository.findById(session_id).orElse(null);
        if (theActualSession != null) {
            theActualSession.setUser(null);
            return this.theSessionRepository.save(theActualSession);
        } else {
            return null;
        }
    }
}
