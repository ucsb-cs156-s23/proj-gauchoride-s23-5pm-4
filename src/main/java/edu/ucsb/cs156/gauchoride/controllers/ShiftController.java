package edu.ucsb.cs156.gauchoride.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.gauchoride.entities.Shift;
import edu.ucsb.cs156.gauchoride.repositories.ShiftRepository;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import edu.ucsb.cs156.gauchoride.models.CurrentUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(description = "Shift information")
@RequestMapping("/api/shift")
@RestController
public class ShiftController extends ApiController {
    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "Get a list of all shifts")
    @GetMapping("")
    public ResponseEntity<String> shifts()
            throws JsonProcessingException {
        Iterable<Shift> shifts = shiftRepository.findAll();
        String body = mapper.writeValueAsString(shifts);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Get shift by id")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/get")
    public Shift shifts(
            @ApiParam(name = "id", type = "Long", value = "id number of shift to get", example = "1", required = true) @RequestParam Long id)
            throws JsonProcessingException {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Shift.class, id));
        return shift;
    }

    @ApiOperation(value = "Delete a shift (driver or admin)")
    @PreAuthorize("hasRole('ROLE_DRIVER')")
    @DeleteMapping("/delete")
    public Object deleteShift_Driver_Admin(
            @ApiParam(name = "id", type = "Long", value = "id number of shift to delete", example = "1", required = true) @RequestParam Long id) {
                Shift shift = shiftRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Shift.class, id));

            CurrentUser user = user.getUser();

            if (hasRole("ROLE_ADMIN")) {
                shiftRepository.delete(shift);
            }

            else if (shift.driverID == user.id) {
                shiftRepository.delete(shift);
            }

            else {
                return genericMessage("Unable to delete Shift with id %s".formatted(id));
            }

        return genericMessage("Shift with id %s deleted".formatted(id));
    }

    
    @ApiOperation(value = "Toggle the admin field")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/toggleAdmin")
    public Object toggleAdmin( @ApiParam(name = "id", type = "Long", value = "id number of user to toggle their admin field", example = "1", required = true) @RequestParam Long id){
        User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(User.class, id));

        user.setAdmin(!user.getAdmin());
        userRepository.save(user);
        return genericMessage("User with id %s has toggled admin status".formatted(id));
    }

    @ApiOperation(value = "Toggle the driver field")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/toggleDriver")
    public Object toggleDriver( @ApiParam(name = "id", type = "Long", value = "id number of user to toggle their driver field", example = "1", required = true) @RequestParam Long id){
        User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(User.class, id));

        user.setDriver(!user.getDriver());
        userRepository.save(user);
        return genericMessage("User with id %s has toggled driver status".formatted(id));
    }

}