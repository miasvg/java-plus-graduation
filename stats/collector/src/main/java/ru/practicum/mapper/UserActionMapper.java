package ru.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.stats.avro.ActionType;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import stats.messages.collector.UserAction;


import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UserActionMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "actionType", target = "actionType", qualifiedByName = "protoToAvroActionType")
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "protoTimestampToInstant")
    UserActionAvro toAvro(UserAction.UserActionProto proto);

    @Named("protoToAvroActionType")
    default ActionType protoToAvroActionType(UserAction.ActionTypeProto actionType) {
        if (actionType == null) return null;
        return switch (actionType) {
            case ACTION_VIEW -> ActionType.VIEW;
            case ACTION_REGISTER -> ActionType.REGISTER;
            case ACTION_LIKE -> ActionType.LIKE;
            case UNRECOGNIZED -> null;
        };
    }

    @Named("protoTimestampToInstant")
    default Instant protoTimestampToInstant(Timestamp ts) {
        if (ts == null) return null;
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
