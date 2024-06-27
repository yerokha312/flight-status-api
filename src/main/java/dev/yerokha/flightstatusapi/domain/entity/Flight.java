package dev.yerokha.flightstatusapi.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "flight", indexes = {
        @Index(columnList = "arrival"),
        @Index(columnList = "origin, arrival"),
        @Index(columnList = "destination, arrival"),
        @Index(columnList = "origin, destination, arrival")
})
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "origin", nullable = false, length = 256, updatable = false)
    private String origin;

    @Column(name = "destination", nullable = false, length = 256, updatable = false)
    private String destination;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
    @Column(name = "departure", nullable = false)
    private OffsetDateTime departure;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
    @Column(name = "arrival", nullable = false)
    private OffsetDateTime arrival;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightStatus flightStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public OffsetDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(OffsetDateTime departure) {
        this.departure = departure;
    }

    public OffsetDateTime getArrival() {
        return arrival;
    }

    public void setArrival(OffsetDateTime arrival) {
        this.arrival = arrival;
    }

    public FlightStatus getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(FlightStatus flightStatus) {
        this.flightStatus = flightStatus;
    }

    public Flight() {
    }

    public Flight(String origin, String destination, OffsetDateTime departure, OffsetDateTime arrival, FlightStatus flightStatus) {
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.flightStatus = flightStatus;
    }

    @Override
    public String toString() {
        return "Flight{" +
               "id=" + id +
               ", origin='" + origin + '\'' +
               ", destination='" + destination + '\'' +
               ", departure=" + departure +
               ", arrival=" + arrival +
               ", status=" + flightStatus +
               '}';
    }
}
