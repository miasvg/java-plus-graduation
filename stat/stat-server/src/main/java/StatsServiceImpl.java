import lombok.RequiredArgsConstructor;
import model.StatEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository repository;
    private final HitMapper mapper;

    @Override
    public List<ResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<StatEntity> stats;

        boolean filterByUris = uris != null && !uris.isEmpty();

        if (unique) {
            stats = filterByUris
                    ? repository.getUniqueStatsByUris(start, end, uris)
                    : repository.getUniqueStats(start, end);
        } else {
            stats = filterByUris
                    ? repository.getAllStatsByUris(start, end, uris)
                    : repository.getAllStats(start, end);
        }

        return stats.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveHit(RequestHitDto hitDto) {
        repository.save(mapper.toEntity(hitDto));
    }
}
