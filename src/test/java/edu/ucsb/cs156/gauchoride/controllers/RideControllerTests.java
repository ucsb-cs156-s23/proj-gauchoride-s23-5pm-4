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
        public void logged_out_users_cannot_get_all_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_not_admin_cannot_get_all_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin/all"))
                                .andExpect(status().is(403)); // logged in users can't get all unless admin
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void logged_in_admin_can_get_all_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin/all"))
                                .andExpect(status().is(200)); // logged
        }

        // Authorization tests for /api/ride_request/all

        @Test
        public void logged_out_users_cannot_get_any_rides() throws Exception {
                mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_their_rides() throws Exception {
                mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().is(200)); // logged in users can't get all unless admin
        }

        // Authorization tests for /api/ride_request/admin?id={}

        @Test
        public void logged_out_users_cannot_get_by_id_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_not_admin_cannot_get_by_id_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin?id=7"))
                                .andExpect(status().is(403)); // logged in users can't get by id using admin backend
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void logged_in_admin_can_get_by_id_admin_backend() throws Exception {
                mockMvc.perform(get("/api/ride_request/admin?id=7"))
                                .andExpect(status().is(404)); // logged, but no id exists
        }

        // Authorization tests for /api/ride_request?id={}

        @Test
        public void logged_out_users_cannot_get_any_by_id() throws Exception {
                mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_cannot_get_by_id_in_simple_test() throws Exception {
                mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().is(404)); 
                // logged in users can get their rides by id unless their ride matches, no ride with id 7 exists
        }


        // Authorization tests for /api/ride_request/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ride_request/post"))
                                .andExpect(status().is(403));
        }

        // Authorization tests for delete request /api/ride_request and api/ride_request/admin
        // (Perhaps should also have these for put)

        @Test
        public void logged_out_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/ride_request?id=9"))
                                .andExpect(status().is(403));
        }

        @Test
        public void logged_out_users_cannot_delete_admin_backend() throws Exception {
                mockMvc.perform(delete("/api/ride_request/admin?id=9"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_not_admin_cannot_delete_admin_backend() throws Exception {
                mockMvc.perform(delete("/api/ride_request/admin?id=9"))
                                .andExpect(status().is(403));
        }

        // // Tests with mocks for database actions




        // GET BY ID


        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists_and_user_id_matches() throws Exception {

                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();

                Ride ride = Ride.builder()
                                .riderId(userId)
                                .day("Monday")
                                .course("CMPSC 156")
                                .startTime("2:00PM")
                                .endTime("3:15PM")
                                .dropoffLocation("South Hall")
                                .pickupLocation("Phelps Hall")
                                .room("1431")
                                .build();

                when(rideRepository.findByIdAndRiderId(eq(7L), eq(userId))).thenReturn(Optional.of(ride));

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findByIdAndRiderId(eq(7L), eq(userId));
                String expectedJson = mapper.writeValueAsString(ride);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();

                when(rideRepository.findByIdAndRiderId(eq(7L), eq(userId))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rideRepository, times(1)).findByIdAndRiderId(eq(7L), eq(userId));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Ride with id 7 not found", json.get("message"));
        }
        

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists_and_user_id_does_not_match() throws Exception {

                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();
                long otherUserId = userId + 1;

                Ride ride = Ride.builder()
                                .riderId(otherUserId)
                                .day("Monday")
                                .course("CMPSC 156")
                                .startTime("2:00PM")
                                .endTime("3:15PM")
                                .dropoffLocation("South Hall")
                                .pickupLocation("Phelps Hall")
                                .room("1431")
                                .build();

                when(rideRepository.findByIdAndRiderId(eq(7L), eq(otherUserId))).thenReturn(Optional.of(ride));

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rideRepository, times(1)).findByIdAndRiderId(eq(7L), eq(userId));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Ride with id 7 not found", json.get("message"));
        }



        @WithMockUser(roles = { "ADMIN" , "USER" })
        @Test
        public void test_that_logged_in_admin_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();
                long otherUserId = userId + 1;

                Ride ride = Ride.builder()
                                .riderId(otherUserId)
                                .day("Monday")
                                .course("CMPSC 156")
                                .startTime("2:00PM")
                                .endTime("3:15PM")
                                .dropoffLocation("South Hall")
                                .pickupLocation("Phelps Hall")
                                .room("1431")
                                .build();

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.of(ride));

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request/admin?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(ride);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" , "USER" })
        @Test
        public void test_that_logged_in_admin_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(rideRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request/admin?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(rideRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("Ride with id 7 not found", json.get("message"));
        }
        


        // GET ALL

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_their_own_rides() throws Exception {

                long userId = currentUserService.getCurrentUser().getUser().getId();

                Ride ride1 = Ride.builder()
                                .riderId(userId)
                                .day("Monday")
                                .course("CMPSC 156")
                                .startTime("2:00PM")
                                .endTime("3:15PM")
                                .dropoffLocation("South Hall")
                                .pickupLocation("Phelps Hall")
                                .room("1431")
                                .build();

                Ride ride3 = Ride.builder()
                                .riderId(userId)
                                .day("Thursday")
                                .course("MATH 111C")
                                .startTime("9:30AM")
                                .endTime("10:45AM")
                                .dropoffLocation("Phelps Hall")
                                .pickupLocation("Student Resource Building")
                                .room("3505")
                                .build();

                ArrayList<Ride> expectedRides = new ArrayList<>();
                expectedRides.addAll(Arrays.asList(ride1, ride3));

                when(rideRepository.findAllByRiderId(eq(userId))).thenReturn(expectedRides);

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findAllByRiderId(eq(userId));
                String expectedJson = mapper.writeValueAsString(expectedRides);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN" , "USER" })
        @Test
        public void logged_in_admin_can_get_all_rides() throws Exception {

                long userId = currentUserService.getCurrentUser().getUser().getId();
                long otherUserId = userId + 1;

                Ride ride1 = Ride.builder()
                                .riderId(userId)
                                .day("Monday")
                                .course("CMPSC 156")
                                .startTime("2:00PM")
                                .endTime("3:15PM")
                                .dropoffLocation("South Hall")
                                .pickupLocation("Phelps Hall")
                                .room("1431")
                                .build();

                Ride ride2 = Ride.builder()
                                .riderId(otherUserId)
                                .day("Thursday")
                                .course("MATH 118C")
                                .startTime("12:30PM")
                                .endTime("1:45PM")
                                .dropoffLocation("Phelps Hall")
                                .pickupLocation("UCen")
                                .room("3505")
                                .build();

                Ride ride3 = Ride.builder()
                                .riderId(userId)
                                .day("Thursday")
                                .course("MATH 111C")
                                .startTime("9:30AM")
                                .endTime("10:45AM")
                                .dropoffLocation("Phelps Hall")
                                .pickupLocation("Student Resource Building")
                                .room("3505")
                                .build();

                ArrayList<Ride> expectedRides = new ArrayList<>();
                expectedRides.addAll(Arrays.asList(ride1, ride2, ride3));

                when(rideRepository.findAll()).thenReturn(expectedRides);

                // act
                MvcResult response = mockMvc.perform(get("/api/ride_request/admin/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(rideRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRides);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }



        // POST



        @WithMockUser(roles = { "USER" })
        @Test
        public void a_user_can_post_a_new_ride() throws Exception {
                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();

                Ride ride1 = Ride.builder()
                        .riderId(userId)
                        .day("Monday")
                        .course("CMPSC 156")
                        .startTime("2:00PM")
                        .endTime("3:15PM")
                        .dropoffLocation("South Hall")
                        .pickupLocation("Phelps Hall")
                        .room("1431")
                        .build();

                when(rideRepository.save(eq(ride1))).thenReturn(ride1);

                String postRequesString = "day=Monday&course=CMPSC 156&startTime=2:00PM&endTime=3:15PM&pickupLocation=Phelps Hall&dropoffLocation=South Hall&room=1431";

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



        // DELETE

        @WithMockUser(roles = { "USER" })
        @Test
        public void user_can_delete_their_own_ride() throws Exception {
                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();

                Ride ride1 = Ride.builder()
                        .riderId(userId)
                        .day("Monday")
                        .course("CMPSC 156")
                        .startTime("2:00PM")
                        .endTime("3:15PM")
                        .dropoffLocation("South Hall")
                        .pickupLocation("Phelps Hall")
                        .room("1431")
                        .build();

                when(rideRepository.findByIdAndRiderId(eq(15L), eq(userId))).thenReturn(Optional.of(ride1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assertuserId
                verify(rideRepository, times(1)).findByIdAndRiderId(eq(15L), eq(userId));
                verify(rideRepository, times(1)).delete(ride1);

                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void user_tries_to_delete_other_users_ride_and_fails()
                        throws Exception {
                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();
                long otherUserId = userId + 1;

                Ride ride1 = Ride.builder()
                        .riderId(otherUserId)
                        .day("Monday")
                        .course("CMPSC 156")
                        .startTime("2:00PM")
                        .endTime("3:15PM")
                        .dropoffLocation("South Hall")
                        .pickupLocation("Phelps Hall")
                        .room("1431")
                        .build();

                when(rideRepository.findByIdAndRiderId(eq(15L), eq(otherUserId))).thenReturn(Optional.of(ride1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findByIdAndRiderId(eq(15L), eq(userId));
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void user_tries_to_delete_non_existant_ride_and_gets_right_error_message()
                        throws Exception {
                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();

                when(rideRepository.findByIdAndRiderId(eq(15L), eq(userId))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findByIdAndRiderId(eq(15L), eq(userId));
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 not found", json.get("message"));
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_any_ride() throws Exception {
                // arrange

                long userId = currentUserService.getCurrentUser().getUser().getId();
                long otherUserId = userId + 1;

                Ride ride1 = Ride.builder()
                        .riderId(otherUserId)
                        .day("Monday")
                        .course("CMPSC 156")
                        .startTime("2:00PM")
                        .endTime("3:15PM")
                        .dropoffLocation("South Hall")
                        .pickupLocation("Phelps Hall")
                        .room("1431")
                        .build();

                when(rideRepository.findById(eq(15L))).thenReturn(Optional.of(ride1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ride_request/admin?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                verify(rideRepository, times(1)).delete(ride1);

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
                                delete("/api/ride_request/admin?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(rideRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Ride with id 15 not found", json.get("message"));
        }
}