package ru.practicum.location.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.NewLocationRequest;
import ru.practicum.location.model.Location;

@Component
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
