package socketfactory.spi;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.SchemeSocketFactory;

public interface SocketFactory extends SchemeSocketFactory {

  boolean supports(String scheme);

  String sanitize(String uri);

  void configure(HttpClient httpClient, String sanitizedUri);
}
