import model.HitEntity;
import model.StatEntity;
import org.springframework.stereotype.Component;

@Component
public class HitMapper {
    public HitEntity toEntity(RequestHitDto dto) {
        HitEntity entity = new HitEntity();
        entity.setApp(dto.getApp());
        entity.setUri(dto.getUri());
        entity.setIp(dto.getIp());
        entity.setTimestamp(dto.getTimestamp());
        return entity;
    }

    public ResponseDto toResponseDto(StatEntity stat) {
        return new ResponseDto(stat.getApp(), stat.getUri(), stat.getHits());
    }
}

