package de.gesellix.socketfactory.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import socketfactory.spi.SocketFactory;

public class UnixSocketFactory implements SocketFactory {

  private File socketFile;

  public UnixSocketFactory() {
  }

  @Override
  public boolean supports(String scheme) {
    return "unix" == scheme;
  }

  @Override
  public String sanitize(String dockerHost) {
    return dockerHost.replaceAll("^unix://", "unix://localhost");
  }

  @Override
  public void configure(HttpClient httpClient, String dockerHost) {
    this.socketFile = new File(dockerHost.replaceAll("unix://localhost", ""));
    Scheme unixScheme = new Scheme("unix", 0xffff, this);
    httpClient.getConnectionManager().getSchemeRegistry().register(unixScheme);
  }

  @Override
  public Socket createSocket(HttpParams params) throws IOException {
    AFUNIXSocket socket = AFUNIXSocket.newInstance();
    return socket;
  }

  @Override
  public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
    int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
//    int soTimeout = HttpConnectionParams.getSoTimeout(params)

    try {
//      socket.setSoTimeout(soTimeout)
      socket.connect(new AFUNIXSocketAddress(socketFile), connTimeout);
//      socket.connect(new AFUNIXSocketAddress(socketFile))
    }
    catch (SocketTimeoutException e) {
      throw new ConnectTimeoutException("Connect to '" + socketFile + "' timed out");
    }

    return socket;
  }

  @Override
  public boolean isSecure(Socket sock) throws IllegalArgumentException {
    return false;
  }
}
