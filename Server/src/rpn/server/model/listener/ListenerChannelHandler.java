package rpn.server.model.listener;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.server.Server;
import rpn.server.model.replica.Replica;
import rpn.server.model.replica.ReplicaHandler;
import rpn.server.model.responses.ResponseHandler;

public class ListenerChannelHandler extends ChannelHandlerAdapter{

    private Replica replica = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.replica = new Replica(ctx.channel());

        replica.update(Server.getInstance().getService().toString());

        ctx.pipeline().addBefore("handler", "decoder", new ListenerConnectionDecoder(replica));

        Server.LOGGER.info("Replica (ID: " + replica.getId() + ") - Status: Initialising");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (replica != null) {
            ReplicaHandler.getInstance().deregisterReplica(replica);
            ResponseHandler.getInstance().acknowledge(replica.getId());
            Server.LOGGER.info("Replica (ID: " + replica.getId() + ") - Status: Disconnected");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Boolean) {
            if (Boolean.TRUE.equals(msg)) {
                ReplicaHandler.getInstance().registerReplica(this.replica);
                Server.LOGGER.info("Replica Registration (ID: " + replica.getId() + ") - Status: Connected");
            } else {
                ctx.close();
                Server.LOGGER.warning("Replica Registration (ID: " + replica.getId() + ") - Status: Rejected");
            }
        } else if (msg instanceof Ack) {
            ResponseHandler.getInstance().acknowledge((Ack) msg);
            Server.LOGGER.info("Received Ack (ID: " + ((Ack) msg).getId() + ") from Replica (ID: " + replica.getId() + ")");
        }
    }
}
