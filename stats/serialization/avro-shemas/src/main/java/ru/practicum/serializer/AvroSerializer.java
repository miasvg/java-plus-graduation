package ru.practicum.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;

public class AvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private BinaryEncoder encoder;
    @Override
    public byte[] serialize(String s, SpecificRecordBase specificRecordBase) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            if (specificRecordBase != null){
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(specificRecordBase.getSchema());
                encoder = encoderFactory.binaryEncoder(outputStream, encoder);
                writer.write(specificRecordBase, encoder);
                encoder.flush();
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro object", e);
        }
    }
}
