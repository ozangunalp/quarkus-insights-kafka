package io.quarkus.insights;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Channel;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerdeConfig;
import io.apicurio.registry.serde.avro.AvroSerde;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@ApplicationScoped
@Path("/clicks-count")
public class ClicksAggregator {

    @ConfigProperty(name = "mp.messaging.connector.smallrye-kafka.apicurio.registry.url")
    String registryUrl;

    @Produces
    public Topology topology() {
        StreamsBuilder builder = new StreamsBuilder();
        AvroSerde<Click> clickSerde = new AvroSerde<>();

        Map<String, Object> config = new HashMap<>();
        config.put(SerdeConfig.REGISTRY_URL, registryUrl);
        config.put(AvroKafkaSerdeConfig.USE_SPECIFIC_AVRO_READER, true);
        config.put(SerdeConfig.EXPLICIT_ARTIFACT_ID, "clicks-value");
        clickSerde.configure(config, false);

        builder.stream("clicks", Consumed.with(Serdes.String(), clickSerde))
                .groupBy((key, click) -> click.getXpath(), Grouped.with(Serdes.String(), clickSerde))
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("clicks-per-element-store")
                        .withCachingDisabled())
                .toStream()
                .to("clicks-per-element", Produced.with(Serdes.String(), Serdes.Long()));
        return builder.build();
    }

    @Channel("clicks-per-element")
    Multi<KafkaRecord<String, Long>> clickCount;

    @GET
    @javax.ws.rs.Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<Tuple2<String, Long>> stream() {
        return clickCount.onItem()
                .transformToUniAndConcatenate(r -> Uni.createFrom().completionStage(r.ack())
                        .replaceWith(Tuple2.of(r.getKey(), r.getPayload())));
    }

}
