package socketfactory.spi

import org.apache.http.conn.scheme.SchemeSocketFactory

interface SocketFactory extends SchemeSocketFactory {

  def supports(scheme)

  def sanitize(uri)

  def configure(httpClient, String sanitizedUri)
}
