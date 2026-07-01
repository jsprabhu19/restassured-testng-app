package com.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.framework.config.ConfigReader;
import com.framework.config.FrameworkConstants;

/**
 * Extent Report manager utility for configuring, writing, and accessing test metrics.
 * Utilizes a ThreadLocal pattern to ensure thread safety across parallel test execution runs.
 */
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentReportManager() {
        // Prevent instantiation
    }

    /**
     * Initializes the report instance and maps ExtentSparkReporter configuration.
     * Invoked once at suite level startup.
     */
    public static synchronized void initReports() {
        if (extent == null) {
            extent = new ExtentReports();
            ExtentSparkReporter spark = new ExtentSparkReporter(FrameworkConstants.REPORTS_PATH);
            
            // Reorder view tabs so that the colorful interactive Dashboard view (graphs and charts) loads first
            spark.viewConfigurer().viewOrder().as(new ViewName[] {
                ViewName.DASHBOARD,
                ViewName.TEST,
                ViewName.CATEGORY,
                ViewName.DEVICE,
                ViewName.LOG
            }).apply();

            // Set basic configuration metadata
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("API Test Automation Framework Execution Report");
            spark.config().setReportName("REST Assured + TestNG Integration Run");

            // Custom modern css styling injection to produce gorgeous color palettes, cards, and animations
            String customCss = "body { "
                             + "  font-family: 'Inter', -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; "
                             + "  background-color: #0b0f19; "
                             + "} "
                             + ".header.navbar { "
                             + "  background: linear-gradient(135deg, #111827 0%, #030712 100%) !important; "
                             + "  border-bottom: 1px solid #1f2937 !important; "
                             + "  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4) !important; "
                             + "} "
                             + ".side-nav { "
                             + "  background-color: #030712 !important; "
                             + "  border-right: 1px solid #1f2937 !important; "
                             + "} "
                             + ".side-nav-inner { "
                             + "  background-color: #030712 !important; "
                             + "} "
                             + ".side-nav .nav-element.active { "
                             + "  background: linear-gradient(90deg, rgba(79, 70, 229, 0.2) 0%, rgba(79, 70, 229, 0) 100%) !important; "
                             + "  border-left: 4px solid #6366f1 !important; "
                             + "} "
                             + ".side-nav .nav-element.active .icon-holder { "
                             + "  color: #818cf8 !important; "
                             + "} "
                             + ".card { "
                             + "  background: #111827 !important; "
                             + "  border: 1px solid #1f2937 !important; "
                             + "  border-radius: 14px !important; "
                             + "  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3) !important; "
                             + "  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important; "
                             + "} "
                             + ".card:hover { "
                             + "  transform: translateY(-4px) !important; "
                             + "  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.5) !important; "
                             + "  border-color: #374151 !important; "
                             + "} "
                             + ".card-header { "
                             + "  background-color: rgba(17, 24, 39, 0.8) !important; "
                             + "  border-bottom: 1px solid #1f2937 !important; "
                             + "  border-top-left-radius: 14px !important; "
                             + "  border-top-right-radius: 14px !important; "
                             + "} "
                             + ".badge-success, .status.pass { "
                             + "  background-color: #10b981 !important; " /* Emerald Pass */
                             + "  color: #ffffff !important; "
                             + "  font-weight: 600 !important; "
                             + "  border-radius: 6px !important; "
                             + "  padding: 4px 8px !important; "
                             + "} "
                             + ".badge-danger, .status.fail { "
                             + "  background-color: #f43f5e !important; " /* Rose Fail */
                             + "  color: #ffffff !important; "
                             + "  font-weight: 600 !important; "
                             + "  border-radius: 6px !important; "
                             + "  padding: 4px 8px !important; "
                             + "} "
                             + ".badge-warning, .status.skip { "
                             + "  background-color: #f59e0b !important; " /* Amber Skip */
                             + "  color: #ffffff !important; "
                             + "  font-weight: 600 !important; "
                             + "  border-radius: 6px !important; "
                             + "  padding: 4px 8px !important; "
                             + "} "
                             + ".table { "
                             + "  color: #f3f4f6 !important; "
                             + "} "
                             + ".table thead th { "
                             + "  border-bottom: 2px solid #1f2937 !important; "
                             + "  color: #9ca3af !important; "
                             + "} "
                             + ".table td, .table th { "
                             + "  border-top: 1px solid #1f2937 !important; "
                             + "} "
                             + ".chart-box { "
                             + "  background: #111827 !important; "
                             + "  border: 1px solid #1f2937 !important; "
                             + "  border-radius: 14px !important; "
                             + "}";
            
            spark.config().setCss(customCss);

            extent.attachReporter(spark);

            // Set system and testing execution metadata values
            extent.setSystemInfo("Environment", ConfigReader.getInstance().get("environment"));
            extent.setSystemInfo("ReqRes Target URL", ConfigReader.getInstance().getBaseUriReqRes());
            extent.setSystemInfo("HttpBin Target URL", ConfigReader.getInstance().getBaseUriHttpBin());
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Runtime", System.getProperty("java.version"));
            extent.setSystemInfo("Framework Version", "1.0.0");
            extent.setSystemInfo("Author/Engineer", "Antigravity Pair Programmer");
        }
    }

    /**
     * Writes all test runs out to the HTML report.
     * Invoked once at suite completion.
     */
    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Creates a test instance in the extent system and attaches it to the current running thread.
     *
     * @param testName Name of the test suite method
     */
    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    /**
     * Retrieves the ExtentTest instance bound to the executing thread context.
     *
     * @return ExtentTest thread local instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Unloads the thread-local instance after the test completes.
     * Prevents thread-local memory leakages.
     */
    public static void unload() {
        extentTest.remove();
    }

    /**
     * Logs a successful test validation message to the current test run.
     *
     * @param message Validation description
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    /**
     * Logs a failed validation description along with stack trace details.
     *
     * @param message Failure explanation
     * @param t Caught throwable/assertion stack details
     */
    public static void logFail(String message, Throwable t) {
        if (getTest() != null) {
            getTest().fail(message).fail(t);
        }
    }

    /**
     * Logs an informational status block into the report output.
     *
     * @param message Status detail description
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().info(message);
        }
    }
}
