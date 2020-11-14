import static java.lang.System.*;
import java.io.*;
import java.net.*;

public class Talk 
{
	public static void main(String[] args) {
		
		if(args.length<1){
			out.println("Talk must be started with a flag. See Talk -help");
		} else parse:{
			Mode talkMode = null;
			String hostIP = null;
			Integer portnum = null;
			int index = 0;
			
			switch (args[index]){
				case "-h": 
					talkMode = Mode.Client;
					break;
				case "-s": 
					talkMode = Mode.Server;
					break;
				case "-a": 
					talkMode = Mode.Auto;
					break;
				case "-help":
					out.println("\nTalk.java by Johnathan Alexander" +
							"\nTalk -h [hostname|IPaddress] [-p portnumber] : client mode" +
							"\nTalk -s [-p portnumber] : server mode" +
							"\nTalk -a [hostname|IPaddress] [-p portnumber] : automode");
					break parse;
				default : 
					out.println("Invalid flag. See Talk -help");
					break parse;
			}
			if(index+1<args.length){
				if(args[index+1].equals("-p")){
					index+=2;
					if(index<args.length){
						try {
							portnum = Integer.parseInt(args[index]);
						} catch (NumberFormatException e){
							out.println("Invalid portnumber. See Talk -help");
							break parse;
						}
					}
					else{
						out.println("Missing portnumber. See Talk -help");
						break parse;
					}
					
				} else if (talkMode == Mode.Client || talkMode == Mode.Auto) {
					index++;
					hostIP = args[1];
					if(index+1<args.length && args[index+1].equals("-p")){
						index+=2;
						if(index<args.length){
							try {
								portnum = Integer.parseInt(args[index]);
							} catch (NumberFormatException e){
								out.println("Invalid portnumber. See Talk -help");
								break parse;
							}
						}
						else{
							out.println("Missing portnumber. See Talk -help");
							break parse;
						}	
					}
				} 
			}
			if(index+1<args.length){
				out.println("Invalid input. See Talk -help");
			}
			else{
				if(portnum==null)
					portnum = 12987;
				
				switch (talkMode){
					case Client: 
						if(!talk(Mode.Client, portnum, hostIP))
							out.println("Client unable to communicate with server");
						break;
					case Server: 
						if(!talk(Mode.Server, portnum, hostIP))
							out.println("Server unable to listen on the specified port");
						break;
					case Auto: 
						if(!talk(Mode.Client, portnum, hostIP))
							if(!talk(Mode.Server, portnum, hostIP))
								out.println("Server unable to listen on the specified port");
				}
			}
		}
	}
	
	enum Mode{
		Client, Server, Auto
	}
	
	public static boolean talk(Mode mode, Integer serverPortNumber, String serverName){
		String message = null;
		try {
			Socket socket;
			if(mode==Mode.Server){
				ServerSocket server = new ServerSocket(serverPortNumber);
				socket = server.accept();
			}else if (mode==Mode.Client){
				socket = new Socket(serverName, serverPortNumber);
			}else{
				return false;
			}
			BufferedReader netReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedReader keyReader = new BufferedReader(new InputStreamReader(in));
			PrintWriter netWriter = new PrintWriter(socket.getOutputStream(), true);
			while(true){
				if(netReader.ready()){
					message = netReader.readLine();
					out.println("[remote]"+message);
				} else if (keyReader.ready()){
					message = keyReader.readLine();
					if(message.equals("STATUS")){						
						out.println("local address:port "+socket.getLocalSocketAddress());
						out.println("remote address:port "+ socket.getRemoteSocketAddress());
					} else
						netWriter.println(message);
				}
			}
		} catch(UnknownHostException e){
			return false;
		} catch(IOException e){
			return false;
		}
	}
}