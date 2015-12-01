package rpn.server.model.primary;

import io.netty.channel.ChannelPipeline;
import rpn.server.model.client.Client;

public class Primary extends Client {

    public Primary(String host, int port) {
        super(host, port);
    }

    @Override
    protected void initialisePipeline(ChannelPipeline pipeline) {

    }
}
