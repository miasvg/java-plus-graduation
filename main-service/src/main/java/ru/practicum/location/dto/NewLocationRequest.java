package ru.practicum.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewLocationRequest {
    @DecimalMin(value = "-90.00000000", message = "Широта должна быть от -90 до 90")
    @DecimalMax(value = "90.00000000", message = "Широта должна быть от -90 до 90")
    @NotNull
    Double lat;

    @DecimalMin(value = "-180.00000000", message = "Долгота должна быть от -180 до 180")
    @DecimalMax(value = "180.00000000", message = "Долгота должна быть от -180 до 180")
    @NotNull
    Double lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewLocationRequest that = (NewLocationRequest) o;
        return Objects.equals(lat, that.lat) && Objects.equals(lon, that.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
