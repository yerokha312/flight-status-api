package dev.yerokha.flightstatusapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
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
    @Column(name = "departure", nullable = false)
    private OffsetDateTime departure;
    @Column(name = "arrival", nullable = false)
    private OffsetDateTime arrival;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightStatus flightStatus;

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
