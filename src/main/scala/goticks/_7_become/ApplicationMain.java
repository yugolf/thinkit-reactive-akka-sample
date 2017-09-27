package goticks._7_become;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import goticks._7_become.BoxOffice.*;

class ApplicationMain {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());


        // イベントは「音楽」でオープン
        boxOffice.tell(new Open(new TicketSeller.EventType("Music")), ActorRef.noSender());

        // 5秒後にクローズ
        system.scheduler().scheduleOnce(Duration.create(5, "seconds"), boxOffice, new BoxOffice.Close(), system.dispatcher(), ActorRef.noSender());
        // 10秒後にイベントを「スポーツ」でオープン
        system.scheduler().scheduleOnce(Duration.create(10, "seconds"), boxOffice, new BoxOffice.Open(new TicketSeller.EventType("Sports")), system.dispatcher(), ActorRef.noSender());

        // 15秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(15, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), ActorRef.noSender());

        // 1秒毎にランダム枚のチケットを注文
        boxOffice.tell(new Order("RHCP", 4), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 1), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 2), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 3), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 5), ActorRef.noSender());

        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
