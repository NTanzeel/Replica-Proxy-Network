package rpn.server.model.gateway;

import io.netty.channel.ChannelPipeline;
import rpn.server.model.client.Client;

public class Gateway extends Client {

    private String serverHost;
    private int serverPort;

    public Gateway(String host, int port, String serverHost, int serverPort) {
        super(host, port);
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    protected void initialisePipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", new GatewayConnectionDecoder())
                .addLast("handler", new GatewayChannelHandler(serverHost, serverPort));
    }
}