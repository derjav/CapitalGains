package co.com.nu.capitalgains.cli;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CapitalGainsRunnerTest {

  private StringBuilder builder;

  @BeforeEach
  void setUp() throws Exception {
    builder = new StringBuilder();
  }

  @Test
  void testMainCase0WithMultipleArrayInput() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\r\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 5000}]\r\n")
        .append("[{\"operation\":\"buy\", \"unit-cost\":20.00, \"quantity\": 10000},\r\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":10.00, \"quantity\": 5000}]").append("\n");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":10000.00}]\n" + "[{\"tax\":0.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase1With0Taxes() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 100},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 50},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 50}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase2WithTaxesAndNoPreviousLosses() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":5.00, \"quantity\": 5000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":10000.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase1AndCase2With0TaxesAndTaxesAndPreviousLosses() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 100},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 50},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 50}]\n")
        .append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":5.00, \"quantity\": 5000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals(
        "[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00}]\n"
            + "[{\"tax\":0.00},{\"tax\":10000.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase3WithLossesAndDeductions() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":5.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 3000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":1000.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase4WithChangingAveragePriceNoProfitNoLoss() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\":25.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 10000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase5WithChangingAveragePriceAndProfits() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\":25.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":25.00, \"quantity\": 5000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":10000.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase6WithLossesDeductionAndProfits() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":2.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 2000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 2000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":25.00, \"quantity\": 1000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":3000.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase7WithTaxesLossesDeductionAndProfitsAndChangingAveragePrice() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":2.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 2000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":20.00, \"quantity\": 2000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":25.00, \"quantity\": 1000},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\":20.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":15.00, \"quantity\": 5000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":30.00, \"quantity\": 4350},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":30.00, \"quantity\": 650}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals(
        "[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":3000.00},"
            + "{\"tax\":0.00},{\"tax\":0.00},{\"tax\":3700.00},{\"tax\":0.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase8WithBigProfitsPayingTaxes() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\":10.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":50.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\":20.00, \"quantity\": 10000},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\":50.00, \"quantity\": 10000}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals("[{\"tax\":0.00},{\"tax\":80000.00},{\"tax\":0.00},{\"tax\":60000.00}]",
        new String(output.toByteArray()));
  }

  @Test
  void testMainCase9WithManyBoughtsLossesDeductionsAndProfit() {
    builder = new StringBuilder();
    builder.append("[{\"operation\":\"buy\", \"unit-cost\": 5000.00, \"quantity\": 10},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\": 4000.00, \"quantity\": 5},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\": 15000.00, \"quantity\": 5},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\": 4000.00, \"quantity\": 2},\n")
        .append("{\"operation\":\"buy\", \"unit-cost\": 23000.00, \"quantity\": 2},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\": 20000.00, \"quantity\": 1},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\": 12000.00, \"quantity\": 10},\n")
        .append("{\"operation\":\"sell\", \"unit-cost\": 15000.00, \"quantity\": 3}]");
    ByteArrayInputStream input = new ByteArrayInputStream(builder.toString().getBytes());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printer = new PrintStream(output);
    CapitalGainsRunner.runApplication(input, printer);
    assertEquals(
        "[{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":0.00},{\"tax\":1000.00},{\"tax\":2400.00}]",
        new String(output.toByteArray()));
  }
}
