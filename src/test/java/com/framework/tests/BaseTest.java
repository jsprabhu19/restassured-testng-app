package com.framework.tests;

import com.framework.config.ConfigReader;
import org.testng.annotations.BeforeSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite
    public void setupSuite() {
        // Output configuration metadata before executing tests
        log.info("----------------------------------------------------------------");
        log.info("Starting REST Assured Framework Suite Execution");
        log.info("Target Testing Environment: {}", ConfigReader.getInstance().get("environment"));
        log.info("ReqRes URI Base URL       : {}", ConfigReader.getInstance().getBaseUriReqRes());
        log.info("HttpBin URI Base URL      : {}", ConfigReader.getInstance().getBaseUriHttpBin());
        log.info("----------------------------------------------------------------");
    }
}
