package ru.practicum.location.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.NewLocationRequest;
import ru.practicum.location.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }

    public static Location mapToLocationNew(NewLocationRequest request) {
        return Location.builder()
                .lat(request.getLat())
                .lon(request.getLon())
                .build();
    }
}
