package de.reondo.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import de.reondo.akka.cluster.actor.ClusterListenerActor;
import de.reondo.akka.cluster.actor.ProducerActor;

public class ClusterProducerApp
{
    public static void main( String[] args )
    {
        ActorSystem actorSystem = ActorSystem.create("ClusterSystem");
        actorSystem.actorOf(Props.create(ClusterListenerActor.class), "ClusterListenerActor");
        actorSystem.actorOf(Props.create(ProducerActor.class), "ProducerActor");
    }
}
