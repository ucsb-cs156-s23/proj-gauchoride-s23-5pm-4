package edu.ucsb.cs156.gauchoride.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ride")
public class Ride {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String student;

//   @ApiModelProperty(allowableValues = "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday")
  private String day;
  
  private String start;
  private String end;

  private String pickup;
  private String dropoff;
  
  private String room;
  private String course;
}