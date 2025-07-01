package ru.practicum.location.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @DecimalMin(value = "-90.00000000", message = "Широта должна быть от -90 до 90")
    @DecimalMax(value = "90.00000000", message = "Широта должна быть от -90 до 90")
    @NotNull
    @Column(name = "lat", nullable = false)
    Double lat;

    @DecimalMin(value = "-180.00000000", message = "Долгота должна быть от -180 до 180")
    @DecimalMax(value = "180.00000000", message = "Долгота должна быть от -180 до 180")
    @NotNull
    @Column(name = "lon", nullable = false)
    Double lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) && Objects.equals(lat, location.lat) && Objects.equals(lon, location.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lat, lon);
    }
}
