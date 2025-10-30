package ai.flexgalaxy.cql2.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.io.IOException;

public class CustomGeometrySerializer extends JsonSerializer<Geometry> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeoJsonWriter writer = new GeoJsonWriter();

    public CustomGeometrySerializer() {
        writer.setEncodeCRS(false);
    }

    @Override
    public void serialize(Geometry geometry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (geometry == null)
            gen.writeNull();
        else
            gen.writeTree(objectMapper.readTree(writer.write(geometry)));
    }
}