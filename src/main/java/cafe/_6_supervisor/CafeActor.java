package cafe._6_supervisor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// カフェアクター
class CafeActor extends AbstractActor {
    static public Props props() {
        return Props.create(CafeActor.class, () -> new CafeActor());
    }

    // メッセージプロトコルの定義
    public static class Initialize {
        public Initialize() {
        }
    }

    public static class Shutdown {
        public Shutdown() {
        }
    }

    public static class Order {
        private final Product product;
        private final int count;

        public Order(Product product, int count) {
            this.product = product;
            this.count = count;
        }

        public Product getProduct() {
            return product;
        }

        public int getCount() {
            return count;
        }
    }

    // 商品リスト
    public interface Product {
    }

    public static class Coffee implements Product {
        public Coffee() {
        }
    }

    public static class Cake implements Product {
        public Cake() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public CafeActor() {
    }

    private ActorRef kitchen = getContext().actorOf(KitchenActor.props(0), "kitchenActor");
    private ActorRef cashier = getContext().actorOf(CashierActor.props(kitchen), "cashierActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting akka cafe"))
                .match(Order.class, order -> order.getProduct() instanceof Coffee, order -> cashier.forward(new CashierActor.OrderCoffee(order.getCount()), getContext()))
                .match(Order.class, order -> order.getProduct() instanceof Cake, order -> cashier.forward(new CashierActor.OrderCake(order.getCount()), getContext()))
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}


//class CafeActor extends Actor with ActorLogging {
//
//  import CafeActor._

//  val kitchen: ActorRef = context.actorOf(KitchenActor.props(0), "kitchenActor")
//  val cashier: ActorRef = context.actorOf(CashierActor.props(kitchen), "cashierActor")

//  override def receive: Receive = {
//    case Initialize =>
//      log.info("starting akka cafe")
//    case Order(Coffee,count)=>
//            cashier forward CashierActor.OrderCoffee(count)
//            case Order(Cake,count)=>
//            cashier forward CashierActor.OrderCake(count)
//    case Shutdown =>
//      log.info("terminating akka cafe")
//      context.system.terminate()
//  }
//}

// カフェアクターのコンパニオンオブジェクト
//object CafeActor {
//  val props: Props = Props[CafeActor]
//
//  // メッセージプロトコルの定義
//  case object Initialize
//  case object Shutdown
//  case class Order(product: Product, count: Int)
//
//  // 商品リスト
//  sealed trait Products
//  case object Coffee extends Products
//  case object Cake extends Products
//}
