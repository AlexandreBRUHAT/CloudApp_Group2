package fr.polytech.cloud.userapp.controllers;

import fr.polytech.cloud.userapp.dtos.UserDTO;
import fr.polytech.cloud.userapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getUsers(@PageableDefault(size = 100) Pageable pageable) {
        return new ResponseEntity<>(userService.mapToDTOs(userService.getUsers(pageable)), HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<List<UserDTO>> putUsers(@RequestBody List<UserDTO> users) {
        return new ResponseEntity(userService.mapToDTOs(userService.putUsers(userService.mapToEntities(users))), HttpStatus.CREATED);
    }

    @DeleteMapping("/user")
    public ResponseEntity deleteUsers() {
        userService.deleteUsers();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        try {
            return new ResponseEntity<>(userService.getUser(id).mapToDTO(), HttpStatus.OK);
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDTO> putUser(@PathVariable String id, @RequestBody UserDTO user) {
        try {
            return new ResponseEntity(userService.putUser(id, user.mapToEntity()).mapToDTO(), HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (EntityNotFoundException enfe) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<UserDTO> postUser(@RequestBody UserDTO userDTO) {
        return new ResponseEntity(userService.postUser(userDTO.mapToEntity()).mapToDTO(), HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/user/age", params = {"gt"})
    public ResponseEntity<List<UserDTO>> getAgeGreaterThan(@RequestParam(value = "gt") int age, @PageableDefault(size = 100) Pageable pageable) {

        if (age < 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userService.mapToDTOs(userService.getByAgeGreaterThan(age, pageable)), HttpStatus.OK);
    }
    @GetMapping(path = "/user/age", params = {"eq"})
    public ResponseEntity<List<UserDTO>> getAgeEquals(@RequestParam(value = "eq") int eq, @PageableDefault(size = 100) Pageable pageable) {

        if (eq < 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userService.mapToDTOs(userService.getByAgeEquals(eq, pageable)), HttpStatus.OK);
    }

    @GetMapping(path = "/user/search", params = {"term"})
    public ResponseEntity<List<UserDTO>> getNameEquals(@RequestParam(value = "term") String name, @PageableDefault(size = 10) Pageable pageable) {

        return new ResponseEntity<>(userService.mapToDTOs(userService.getByName(name, pageable)), HttpStatus.OK);
    }

    @GetMapping("/user/nearest")
    public ResponseEntity<List<UserDTO>> getNearest(@RequestParam double lat, @RequestParam double lon, @PageableDefault(size = 10) Pageable pageable) {
        return new ResponseEntity<>(userService.mapToDTOs(userService.getPositionNear(lat, lon, pageable)), HttpStatus.OK);
    }
}
