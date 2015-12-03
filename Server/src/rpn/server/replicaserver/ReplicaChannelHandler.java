package rpn.server.replicaserver;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.server.model.connection.Replica;
import rpn.server.model.connection.ReplicaHandler;

public class ReplicaChannelHandler extends ChannelHandlerAdapter{

    private Replica connection = null;

    public void channelInactive(ChannelHandlerContext ctx) {
        if (connection != null) {
            ReplicaHandler.getInstance().deregisterReplica(connection);
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Replica) {
            this.connection = (Replica) msg;
            ReplicaHandler.getInstance().registerReplica(this.connection);
        }
    }



}
