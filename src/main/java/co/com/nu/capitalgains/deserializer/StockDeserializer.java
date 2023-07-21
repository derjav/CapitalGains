package co.com.nu.capitalgains.deserializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import co.com.nu.capitalgains.model.Stock;

public class StockDeserializer extends StdDeserializer<Stock> {

  private static final long serialVersionUID = -5287122190025022851L;

  protected StockDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Stock deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException, JacksonException {
    
    return null;
  }

}
