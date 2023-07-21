package co.com.nu.capitalgains.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Tax implements Serializable {

  private static final long serialVersionUID = -7509794729375529728L;
  private BigDecimal tax;
  
  public Tax() {
    super();
  }
  
  public Tax(double value) {
    this.tax = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
  
  public Tax(BigDecimal tax) {
    this.tax = tax;
  }
  
  public BigDecimal getTax() {
    return tax;
  }
  public void setTax(BigDecimal tax) {
    this.tax = tax.setScale(2, RoundingMode.HALF_UP);
  }

  
}
