package no.hvl.dat110.rpc;

import java.util.HashMap;

import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

	private MessagingServer msgserver;
	private MessageConnection connection;
	
	// hashmap to register RPC methods which are required to extend RPCRemoteImpl
	// the key in the hashmap is the RPC identifier of the method
	private HashMap<Byte,RPCRemoteImpl> services;
	
	public RPCServer(int port) {
		
		this.msgserver = new MessagingServer(port);
		this.services = new HashMap<Byte,RPCRemoteImpl>();
		
	}

	public void run() {
		// the stop RPC method is built into the server
		RPCRemoteImpl rpcstop = new RPCServerStopImpl(RPCCommon.RPIDSTOP,this);
		register(RPCCommon.RPIDSTOP, rpcstop);

		System.out.println("RPC SERVER RUN - Services: " + services.size());

		connection = msgserver.accept();

		System.out.println("RPC SERVER ACCEPTED");

		boolean stop = false;
		try {
			while (!stop) {

				Message requestmsg = connection.receive();
				byte[] requestData = requestmsg.getData();

				byte rpcid = requestData[0];

				byte[] params = RPCUtils.decapsulate(requestData);

				RPCRemoteImpl service = services.get(rpcid);

				if (service == null) {
					System.out.println("RPC SERVICE NOT FOUND");
					byte[] responseData = RPCUtils.encapsulate(rpcid,new byte[0]);
					connection.send(new Message(responseData));
					continue;
				}
				System.out.println("RPC SERVICE FOUND");
				byte[] reply = service.invoke(params);
				reply = RPCUtils.encapsulate(rpcid, reply);
				connection.send(new Message(reply));

				// stop the server if it was stop methods that was called
				if (rpcid == RPCCommon.RPIDSTOP) {
					stop = true;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	// used by server side method implementations to register themselves in the RPC server
	public void register(byte rpcid, RPCRemoteImpl impl) {
		services.put(rpcid, impl);
	}
	
	public void stop() {

		if (connection != null) {
			connection.close();
		} else {
			System.out.println("RPCServer.stop - connection was null");
		}
		
		if (msgserver != null) {
			msgserver.stop();
		} else {
			System.out.println("RPCServer.stop - msgserver was null");
		}
		
	}
}
