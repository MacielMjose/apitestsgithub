package testesApi;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

import static io.restassured.RestAssured.baseURI;
import static org.testng.Assert.assertEquals;

import org.testng.ITestResult;

public class GithubApiTests {

	private RequestSpecification httprequest;
	private Response response;
	private JsonPath extractor; 
	private String current_url;
	ExtentHtmlReporter htmlReport; 
    ExtentReports extent;
    ExtentTest test;

	@BeforeTest
	public void setup() {
		//reporting server initializing
        htmlReport = new ExtentHtmlReporter(System.getProperty("user.dir") +"/test-output/testReport.html");
        //initialize ExtentReports and attach the HtmlReporter
        extent = new ExtentReports();
        extent.attachReporter(htmlReport);
        //configuration items to change the look and feel
        //add content, manage tests etc
        htmlReport.config().setChartVisibilityOnOpen(true);
        htmlReport.config().setDocumentTitle("Api Testes - Report");
        htmlReport.config().setReportName("Github Api Tests");
        htmlReport.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReport.config().setTheme(Theme.STANDARD);
        htmlReport.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
	}
	
	@Test
	public void GET_github_api() {	
		
		test = extent.createTest("Test - Go to Current URL");
		RestAssured.baseURI = "https://api.github.com/";
		httprequest = RestAssured.given();
		response = httprequest.request(Method.GET);
			response.prettyPrint();
		test = extent.createTest("Test - Get Github Current URL");
		extractor = response.jsonPath();
		current_url = extractor.get("current_user_url");
			baseURI = current_url;
		httprequest = RestAssured.given();			
		response = httprequest.request(Method.GET);
			extractor = response.jsonPath();
		String message = extractor.get("message").toString();
		String documentation_url = extractor.getString("documentation_url").toString();
		assertEquals(message, "Requires authentication");
		assertEquals(documentation_url, "https://developer.github.com/v3/users/#get-the-authenticated-user");
		response.prettyPrint();
	}
	
	@AfterMethod
	public void getResult(ITestResult result) {
		if(result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" FAILED ", ExtentColor.RED));
            test.fail(result.getThrowable());
        }
        else if(result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" PASSED ", ExtentColor.GREEN));
        }
        else {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" SKIPPED ", ExtentColor.ORANGE));
            test.skip(result.getThrowable());
        }
	}
	@AfterTest
    public void tearDown() {
    	//to write or update test information to reporter
        extent.flush();
    }
}
