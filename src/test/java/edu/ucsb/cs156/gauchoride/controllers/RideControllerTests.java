package edu.ucsb.cs156.gauchoride.controllers;

import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.testconfig.TestConfig;
import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.entities.Ride;
import edu.ucsb.cs156.gauchoride.repositories.RideRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

// import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RideController.class)
@Import(TestConfig.class)
public class RideControllerTests extends ControllerTestCase {

        @MockBean
        RideRepository rideRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/ride_request/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/ride_request/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ride_request/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ride_request/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                Ride ride = Ride.builder()
                                .day("Monday")
                                .student("CGaucho")
                                .course("CMPSC 156")
                                .start("2:00PM")
                                .end("3:15PM")
                                .dropoff("South Hall")
                                .room("1431")
                                .pickup("Phelps Hall")
                                .build();

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.of(ride));

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(ride);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Ride with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ride() throws Exception {

                Ride ride1 = Ride.builder()
                                .day("Monday")
                                .student("CGaucho")
                                .course("CMPSC 156")
                                .start("2:00PM")
                                .end("3:15PM")
                                .dropoff("South Hall")
                                .room("1431")
                                .pickup("Phelps Hall")
                                .build();

                Ride ride2 = Ride.builder()
                                .day("Thursday")
                                .student("DGaucho")
                                .course("MATH 118C")
                                .start("12:30PM")
                                .end("1:45PM")
                                .dropoff("Phelps Hall")
                                .room("3505")
                                .pickup("UCen")
                                .build();

                ArrayList<Ride> expectedRide = new ArrayList<>();
                expectedRide.addAll(Arrays.asList(ride1, ride2));

                when(rideRepository.findAll()).thenReturn(expectedRide);

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRide);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ride() throws Exception {
                // arrange

                Ride ride1 = Ride.builder()
                        .day("Monday")
                        .student("CGaucho")
                        .course("CMPSC 156")
                        .start("2:00PM")
                        .end("3:15PM")
                        .dropoff("South Hall")
                        .room("1431")
                        .pickup("Phelps Hall")
                        .build();

                when(rideRepository.save(eq(ride1))).thenReturn(ride1);

                String postRequesString = "day=Monday&student=CGaucho&course=CMPSC 156&start=2:00PM&end=3:15PM&dropoff=South Hall&room=1431&pickup=Phelps Hall";

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ride_request/post?" + postRequesString)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).save(ride1);
                String expectedJson = mapper.writeValueAsString(ride1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_ride() throws Exception {
                // arrange

                Ride ride1 = Ride.builder()
                        .day("Monday")
                        .student("CGaucho")
                        .course("CMPSC 156")
                        .start("2:00PM")
                        .end("3:15PM")
                        .dropoff("South Hall")
                        .room("1431")
                        .pickup("Phelps Hall")
                        .build();

                when(rideRepository.findById(eq(15L))).thenReturn(Optional.of(ride1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                verify(rideRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ride_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(rideRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ride() throws Exception {
                // arrange

                Ride ride_original = Ride.builder()
                                .day("Monday")
                                .student("CGaucho")
                                .course("CMPSC 156")
                                .start("2:00PM")
                                .end("3:15PM")
                                .dropoff("South Hall")
                                .room("1431")
                                .pickup("Phelps Hall")
                                .build();

                Ride ride_edited = Ride.builder()
                                .day("Thursday")
                                .student("DGaucho")
                                .course("MATH 118C")
                                .start("12:30PM")
                                .end("1:45PM")
                                .dropoff("Phelps Hall")
                                .room("3505")
                                .pickup("UCen")
                                .build();

                String requestBody = mapper.writeValueAsString(ride_edited);

                when(rideRepository.findById(eq(67L))).thenReturn(Optional.of(ride_original));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ride_request?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(67L);
                verify(rideRepository, times(1)).save(ride_edited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ride_that_does_not_exist() throws Exception {
                // arrange

                Ride ride_edited = Ride.builder()
                                .day("Thursday")
                                .student("DGaucho")
                                .course("MATH 118C")
                                .start("12:30PM")
                                .end("1:45PM")
                                .dropoff("Phelps Hall")
                                .room("3505")
                                .pickup("UCen")
                                .build();


                String requestBody = mapper.writeValueAsString(ride_edited);

                when(rideRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ride_request?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 67 not found", json.get("message"));

        }
}