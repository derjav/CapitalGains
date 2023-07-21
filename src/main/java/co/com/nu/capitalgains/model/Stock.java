package co.com.nu.capitalgains.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Stock implements Serializable {
  
  private static final long serialVersionUID = 5234207105571584717L;
  public static final String BUY_OPERATION = "buy";
  public static final String SELL_OPERATION = "sell";

  private String operation;
  @JsonProperty("unit-cost")
  private double unitCost;
  private int quantity;
}
