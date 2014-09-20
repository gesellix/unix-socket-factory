package socketfactory

import socketfactory.spi.SocketFactory

class SocketFactoryService {

  private static SocketFactoryService service
  private ServiceLoader<SocketFactory> loader

  private SocketFactoryService() {
    loader = ServiceLoader.load(SocketFactory)
  }

  static synchronized SocketFactoryService getInstance() {
    if (service == null) {
      service = new SocketFactoryService()
    }
    return service
  }

  def getSchemeSocketFactory(scheme) {
    def socketFactory = null

    try {
      Iterator<SocketFactory> socketFactories = loader.iterator()
      while (socketFactory == null && socketFactories.hasNext()) {
        SocketFactory candidate = socketFactories.next()
        if (candidate.supports(scheme)) {
          socketFactory = candidate
        }
      }
    }
    catch (ServiceConfigurationError serviceError) {
      socketFactory = null
      serviceError.printStackTrace()
    }
    return socketFactory
  }
}
