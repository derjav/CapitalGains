package co.com.nu.capitalgains.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import co.com.nu.capitalgains.model.Stock;
import co.com.nu.capitalgains.model.Tax;

public class CapitalGainsRunner {

  private static final Logger logger = LoggerFactory.getLogger(CapitalGainsRunner.class);
  private static final String CLOSING_BRACKET = "]";
  private static final String BUY_OPERATION = "buy";
  private static final String SELL_OPERATION = "sell";
  
  public static void main(String[] args) {
    
    CapitalGainsRunner.runApplication(System.in, System.out);
  }

  public static void runApplication(InputStream in, PrintStream out) {
    CapitalGainsRunner runner = new CapitalGainsRunner();
    List<List<Stock>> stocks = runner.readStocks(in);
    List<List<Tax>> taxes = calculateTaxes(stocks);
    writeTaxes(out, taxes);
  }

  private List<List<Stock>> readStocks(InputStream in) {
    System.out.println(
        "Enter the array of stock market operations and press enter for each line. \nPress enter again to finish. ");
    StringBuilder buffer = new StringBuilder();
    String line = "";
    try (Scanner scanner = new Scanner(in)) {
      
      line = scanner.nextLine();
      while (!line.isEmpty()) {
        buffer.append(line).append("\n");
        line = scanner.nextLine();
      }
    } catch (NoSuchElementException nsee) {
      logger.warn("Try to read non-existen line. ");
    }
    return convertJsonToStockList(buffer.toString());
  }

  private List<List<Stock>> convertJsonToStockList(String json) {
    List<List<Stock>> stocks = new ArrayList<>();
    JsonMapper mapper = new JsonMapper();
    try {
      int start = 0;
      int end = 0;
      while (true) {
        end = json.indexOf(CLOSING_BRACKET, start);
        if (end == -1) {
          break;
        }
        stocks.add(Arrays.asList(mapper.readValue(json.substring(start, end + 1), Stock[].class)));
        start = ++end;
      }

    } catch (JsonProcessingException jme) {
      logger.error("The input doesn't have the right format to be processed.", jme);
    }
    return stocks;
  }

  private static List<List<Tax>> calculateTaxes(List<List<Stock>> stocks) {

    List<List<Tax>> taxes = new ArrayList<>();
    for (List<Stock> list : stocks) {
      List<Tax> taxByStocks = new ArrayList<>();
      double weightedAvgPrice = 0.0;
      int currentQuantity = 0;
      double overallProfit = 0.0;
      for (Stock operation : list) {
        double taxValue = 0.0;
        if (BUY_OPERATION.equals(operation.getOperation())) {
          weightedAvgPrice = ((currentQuantity * weightedAvgPrice)
              + (operation.getQuantity() * operation.getUnitCost()))
              / (currentQuantity + operation.getQuantity());
          currentQuantity += operation.getQuantity();
        } else if (SELL_OPERATION.equals(operation.getOperation())) {
          double profit = (operation.getUnitCost() * operation.getQuantity())
              - (weightedAvgPrice * operation.getQuantity());
          overallProfit += profit;
          if (operation.getUnitCost() > weightedAvgPrice
              && (operation.getQuantity() * operation.getUnitCost()) > 20000 && overallProfit > 0
              && currentQuantity > 0) {
            taxValue = overallProfit * 0.2;
            overallProfit = 0.0;
          }
          currentQuantity -= operation.getQuantity();
        }
        taxByStocks.add(new Tax(taxValue));
      }
      taxes.add(taxByStocks);
    }
    return taxes;
  }

  private static void writeTaxes(PrintStream out, List<List<Tax>> taxes) {
    JsonMapper mapper = new JsonMapper();
    StringBuilder output = new StringBuilder();
    taxes.forEach(list -> {
      try {
        output.append(mapper.writeValueAsString(list)).append("\n");
      } catch (JsonProcessingException e) {
        logger.error("There was an error writing the taxes values + " + list + ".", e);
      }
    });
    out.print(output.toString().substring(0, output.length()-1));
  }

}
