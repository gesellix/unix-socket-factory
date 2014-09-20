package de.gesellix.socketfactory.unix

import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.scheme.Scheme
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import socketfactory.spi.SocketFactory

class UnixSocketFactory implements SocketFactory {

  File socketFile

  def UnixSocketFactory() {
  }

  @Override
  def supports(scheme) {
    "unix" == scheme
  }

  @Override
  def sanitize(dockerHost) {
    dockerHost.replaceAll("^unix://", "unix://localhost")
  }

  @Override
  def configure(httpClient, String dockerHost) {
    if (socketFile) {
      throw new UnsupportedOperationException("you can only configure once")
    }
    this.socketFile = new File(dockerHost.replaceAll("unix://localhost", ""))
    def unixScheme = new Scheme("unix", 0xffff, this)
    httpClient.getConnectionManager().getSchemeRegistry().register(unixScheme)
  }

  @Override
  Socket createSocket(HttpParams params) throws IOException {
    AFUNIXSocket socket = AFUNIXSocket.newInstance();
    return socket
  }

  @Override
  Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
    int connTimeout = HttpConnectionParams.getConnectionTimeout(params)
//    int soTimeout = HttpConnectionParams.getSoTimeout(params)

    try {
//      socket.setSoTimeout(soTimeout)
      socket.connect(new AFUNIXSocketAddress(socketFile), connTimeout)
//      socket.connect(new AFUNIXSocketAddress(socketFile))
    }
    catch (SocketTimeoutException e) {
      throw new ConnectTimeoutException("Connect to '" + socketFile + "' timed out")
    }

    return socket
  }

  @Override
  boolean isSecure(Socket sock) throws IllegalArgumentException {
    return false
  }
}
