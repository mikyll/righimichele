package unibo.actor22comm.udp;

import java.net.InetAddress;
import java.util.Objects;

public class UdpEndpoint {
	private InetAddress address;
	private int port;
	
	public UdpEndpoint(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UdpEndpoint other = (UdpEndpoint) obj;
		return Objects.equals(address, other.address) && port == other.port;
	}
	
	@Override
	public String toString() {
		return address.toString() + ":" + port;
	}
}
