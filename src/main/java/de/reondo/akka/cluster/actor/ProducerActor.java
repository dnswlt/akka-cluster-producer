package de.reondo.akka.cluster.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import de.reondo.akka.cluster.proto.product.ProductProtos;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by denni on 17/12/2017.
 */
public class ProducerActor extends AbstractActor {

    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private List<ActorRef> consumers = new ArrayList<>();
    private int productCount;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .matchEquals("produce", this::onProduce)
            .matchEquals("consumerRegistration", this::onConsumerRegistration)
            .match(Terminated.class, this::onTerminated)
            .build();
    }

    private void onTerminated(Terminated terminated) {
        log.info("Consumer terminated: {}", terminated.getActor());
        consumers.remove(terminated.getActor());
    }

    private void onConsumerRegistration(String consumerRegistration) {
        log.info("Consumer registered: {}", getSender());
        getContext().watch(getSender());
        consumers.add(getSender());
    }

    private void onProduce(String msg) {
        for (ActorRef consumer : consumers) {
            consumer.tell(getProduct(), getSelf());
        }
        getContext().getSystem().scheduler()
            .scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS),
                getSelf(), "produce", getContext().dispatcher(), getSelf());
    }

    @Override
    public void preStart() throws Exception {
        getSelf().tell("produce", getSelf());
    }

    public ProductProtos.Product getProduct() {
        return ProductProtos.Product.newBuilder()
            .setId(productCount++)
            .setName("Panasonic G81")
            .setPrice(899)
            .build();
    }
}
