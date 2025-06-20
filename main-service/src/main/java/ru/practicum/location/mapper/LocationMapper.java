package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.NewLocationRequest;
import ru.practicum.location.model.Location;

public class LocationMapper {
    public static LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .lon(location.getLon())
                .lan(location.getLan())
                .build();
    }

    public static Location mapToLocationNew(NewLocationRequest request) {
        return Location.builder()
                .lan(request.getLan())
                .lon(request.getLon())
                .build();
    }
}
