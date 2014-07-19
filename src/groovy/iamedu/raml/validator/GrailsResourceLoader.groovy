package iamedu.raml.validator

import org.raml.parser.loader.*
import java.io.InputStream

public class GrailsResourceLoader implements ResourceLoader
{
  private ResourceLoader resourceLoader

  public GrailsResourceLoader() {
    resourceLoader = new CompositeResourceLoader(
      new FileResourceLoader("grails-app/conf/"),
      new UrlResourceLoader(),
      new ClassPathResourceLoader()
    )
  }

  @Override
  public InputStream fetchResource(String resourceName) {
    return resourceLoader.fetchResource(resourceName)
  }

}
