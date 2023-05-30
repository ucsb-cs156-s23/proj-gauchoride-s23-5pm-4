package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.errors.EntityNotFoundException;
import edu.ucsb.cs156.gauchoride.repositories.RideRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(description = "Ride Request")
@RequestMapping("/api/ride_request")
@RestController
@Slf4j
public class RideController extends ApiController {

    @Autowired
    RideRepository rideRepository;

    @ApiOperation(value = "List all rides")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Ride> allRides() {
        Iterable<Ride> rides = rideRepository.findAll();
        return rides;
    }

    @ApiOperation(value = "Get a single ride")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Ride getById(
            @ApiParam("id") @RequestParam Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));

        return ride;
    }

    @ApiOperation(value = "Create a new ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Ride postRide(
        @ApiParam("day") @RequestParam String day,

        @ApiParam("student") @RequestParam String student,
        @ApiParam("course") @RequestParam String course,

        @ApiParam("time_start") @RequestParam String time_start,
        @ApiParam("time_stop") @RequestParam String time_stop,
        @ApiParam("building") @RequestParam String building,
        @ApiParam("room") @RequestParam String room,
        @ApiParam("pick_up") @RequestParam String pick_up
        )
        {

        Ride ride = new Ride();
        ride.setDay(day);
        ride.setStudent(student);
        ride.setCourse(course);
        ride.setTime_start(time_start);
        ride.setTime_stop(time_stop);
        ride.setBuilding(building);
        ride.setRoom(room);
        ride.setPick_up(pick_up);

        Ride savedRide = rideRepository.save(ride);

        return savedRide;
    }

    @ApiOperation(value = "Delete a Ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRide(
            @ApiParam("id") @RequestParam Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));

        rideRepository.delete(ride);
        return genericMessage("Ride with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single ride")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Ride updateRide(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Ride incoming) {

        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ride.class, id));


        ride.setDay(incoming.getDay());
        ride.setStudent(incoming.getStudent());
        ride.setCourse(incoming.getCourse());
        ride.setTime_start(incoming.getTime_start());
        ride.setTime_stop(incoming.getTime_stop());
        ride.setBuilding(incoming.getBuilding());
        ride.setRoom(incoming.getRoom());
        ride.setPick_up(incoming.getPick_up());

        rideRepository.save(ride);

        return ride;
    }
}