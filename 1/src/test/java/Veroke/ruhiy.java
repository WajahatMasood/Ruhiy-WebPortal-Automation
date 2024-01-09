package Veroke;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.github.dockerjava.api.model.ServiceUpdateState;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeTest;

import java.sql.Time;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;

public class ruhiy {
	ExtentReports extend = new ExtentReports();
	ExtentSparkReporter spark = new ExtentSparkReporter("ruhiyreport.html");
	private WebDriver driver;
	public String ruhiy_url = "https://ruhiy-admin-stg.veroke.com/#/auth/login";

	@BeforeTest
	public void chrome_setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get(ruhiy_url);
		driver.manage().window().maximize();
		extend.attachReporter(spark);
	}

	@Test(priority = 1)
	public void login() throws InterruptedException {
		ExtentTest test = extend.createTest("Login Cases of Ruhiy");
		WebElement emailField = driver.findElement(By.xpath("//*[@id=\"mat-input-0\"]"));
		WebElement passwordField = driver.findElement(By.xpath("//*[@id=\"mat-input-1\"]"));

		WebElement loginButton = driver
				.findElement(By.xpath("/html/body/app-root/app-login/div/div[2]/div[3]/form/button"));
		// ---------------------------------------------------------------------------------------
		// Negative Test Case -- In-Correct email and Password
		// ---------------------------------------------------------------------------------------
		String email = "admin@ru4hiy.com";
		String pasco = "Super@123";
		emailField.sendKeys(email);
		passwordField.sendKeys(pasco);
		loginButton.click();
		Thread.sleep(3000);
		WebElement emailErrormsg = driver.findElement(
				By.xpath("/html/body/div/div[2]/div/mat-dialog-container/div/div/app-alert/div/div/div[2]/div[2]"));

		// Expected error message
		String expectedErrorMessage = "Invalid email or password!";

		// Checking if the error message contains the expected text
		if (emailErrormsg.getText().contains(expectedErrorMessage)) {
			System.out.println("Message: " + emailErrormsg.getText());
			test.log(Status.PASS, "Check login If user enters Incorrect email or password: " + emailErrormsg.getText());
			Actions action = new Actions(driver);
			action.moveByOffset(10, 10).click().build().perform();

		} else {
			System.out.println("Actual Message: " + emailErrormsg.getText());
			test.log(Status.FAIL, "Check login If user enters Incorrect email or password: " + emailErrormsg.getText());
		}
		// ---------------------------------------------------------------------------------------
		// Positive Test Case -- Correct email and Password
		// ---------------------------------------------------------------------------------------
		emailField.clear();
		passwordField.clear();
		String validEmail = "admin@ruhiy.com";
		String validPasco = "123123";
		emailField.sendKeys(validEmail);
		passwordField.sendKeys(validPasco);
		loginButton.click();
		Thread.sleep(3000);
		// for OTP code

		String currentURL = driver.getCurrentUrl();
		if (currentURL.equals("https://ruhiy-admin-stg.veroke.com/#/auth/verification")) {
			Thread.sleep(1500);
			Actions action = new Actions(driver);
			action.moveByOffset(10, 10).click().build().perform();
			test.log(Status.PASS, "User Looged in and promted to OTP Screen");
			// Check for wrong OTP
			String wrong_code = "120000";
			for (int i = 0; i < wrong_code.length(); i++) {
				char digit = wrong_code.charAt(i);
				String digitAsString = String.valueOf(digit);
				String inputXPath = "/html/body/app-root/app-login/div/div[2]/div[3]/form/ng-otp-input/div/input["
						+ (i + 1) + "]";
				WebElement otp_Input = driver.findElement(By.xpath(inputXPath));
				otp_Input.sendKeys(digitAsString);
			}
			WebElement Confirm_Code = driver
					.findElement(By.xpath("/html/body/app-root/app-login/div/div[2]/div[3]/form/button"));
			Confirm_Code.click();
			Thread.sleep(1500);
			WebElement Incorrectotp = driver.findElement(
					By.xpath("/html/body/div/div[2]/div/mat-dialog-container/div/div/app-alert/div/div/div[2]/div[2]"));
			// Expected error message
			String expectedErrorMessage_Otp = "Incorrect OTP";

			// Checking if the error message contains the expected text
			if (Incorrectotp.getText().contains(expectedErrorMessage_Otp)) {
				System.out.println("Message of wrong OTP is: " + Incorrectotp.getText());
				test.log(Status.PASS, "After Adding Wrong OTP warning ': " + Incorrectotp.getText() + "' Appears");
				action.moveByOffset(10, 10).click().build().perform();
			} else {
				System.out.println("Actual Message: " + Incorrectotp.getText());
				test.log(Status.FAIL, "Message not appear after adding wrong OTP: " + Incorrectotp.getText());
			}

			// Tapping on *******"Resend Code"***********
			Thread.sleep(60000);
			WebElement resend_code = driver
					.findElement(By.xpath("/html/body/app-root/app-login/div/div[2]/div[3]/form/div[4]/span"));
			resend_code.click();
			Thread.sleep(2000);
			WebElement Correctotp_resend = driver.findElement(
					By.xpath("/html/body/div/div[2]/div/mat-dialog-container/div/div/app-alert/div/div/div[2]/div[2]"));
			action.moveByOffset(10, 10).click().build().perform();
			String message = Correctotp_resend.getText();
			System.out.println(message);
			// Check for wrong OTP
			String code = "";
			Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				code = matcher.group();
				System.out.println("Extracted Code:" + code); // Display the extracted code
			} else {
				System.out.println("No 6-digit code found.");
			}
			// Code for Adding correct OTP
			// Clear OTP fields before entering the resent OTP
			List<WebElement> otpInputFields = driver.findElements(
					By.xpath("/html/body/app-root/app-login/div/div[2]/div[3]/form/ng-otp-input/div/input"));
			for (WebElement otpInputField : otpInputFields) {
				otpInputField.clear();
			}
			code = code.trim();
			for (int i = 0; i < code.length(); i++) {
				char digit = code.charAt(i);
//				System.out.println("digit is: " + digit);
				String digitAsString = String.valueOf(digit);
				String inputXPath = "/html/body/app-root/app-login/div/div[2]/div[3]/form/ng-otp-input/div/input["
						+ (i + 1) + "]";
				WebElement otp_Input = driver.findElement(By.xpath(inputXPath));
				System.out.println("digitAsString: " + digitAsString);
				otp_Input.sendKeys(digitAsString);
			}
			Confirm_Code.click();
			Thread.sleep(10000);
			String home_currentURL = driver.getCurrentUrl();
			System.out.println(home_currentURL);
			if (home_currentURL.equals("https://ruhiy-admin-stg.veroke.com/#/main/users-management")) {
				test.log(Status.PASS, "Admin successfully loogged in into the system after adding code OTP code");
			} else {
				System.out.println("Else---Link" + home_currentURL);
				test.log(Status.FAIL, "Admin is not able to login even after adding correcy OTP");
			}

		} else {
			System.out.println("Error Message: " + currentURL);
			test.log(Status.FAIL,
					"User is not looged in even after adding correct email and paco and it is directed to : "
							+ currentURL);
		}
	}

	@Test(priority = 2)
	public void UserManagement() throws InterruptedException {
		ExtentTest test = extend.createTest("User Management Test");
		// ==========================================================================================
		// Checking User Count ---
		// ==========================================================================================

		Thread.sleep(4000);
		List<WebElement> no_of_user = driver.findElements(By.xpath(
				"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/table//tr"));
		System.out.println("Entries found are");
		System.out.println("===================================================");
		System.out.println("Totoal Number of User Are: " + (no_of_user.size() - 1));
		for (int i = 0; i < no_of_user.size(); i++) {
			WebElement element = no_of_user.get(i);
			System.out.println("--------------------------------------- ");
			System.out.println(element.getText());
			System.out.println("--------------------------------------- ");
		}
		System.out.println("===================================================");
		int page_one = no_of_user.size();// Page 2
		WebElement pagenation_move_next = driver.findElement(By.xpath(
				"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/app-pagination/div/div/div/mat-icon[2]"));
		pagenation_move_next.click();
		Thread.sleep(4000);
		int page_two = no_of_user.size();
		
		// Total number on the top
		WebElement counting = driver.findElement(By.xpath(
				"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[2]/div[1]/h1"));

		int no_one = no_of_user.size();
		String no_two = counting.getText();
		int no_two_int = Integer.parseInt(no_two);

		if (no_one == no_two_int) {
			test.log(Status.PASS, "Totoal Number of User mentioned on the portal are equal to " + no_two_int);
		} else {
			test.log(Status.FAIL, "Totoal Number of User mentioned on the portal are not equal "
					+ "Webportal Number is : " + no_two_int + "Conted Value is :" + no_one);
		}
		// ==========================================================================================
		// Searching
		// ==========================================================================================
		// ----------------Search#1
		WebElement searchfield = driver.findElement(By.xpath("//*[@id=\"mat-input-0\"]"));
		String search_element_1 = "Murtaza";
		String search_element_2 = "3";
		String search_element_3 = "213213123123123";

		searchfield.sendKeys(search_element_1);
		Thread.sleep(3000);
		try {
			WebElement no_element = driver.findElement(By.xpath(
					"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/div"));
			String textt = no_element.getText();
			if (textt.equals("No Record Found")) {
				test.log(Status.PASS, "No Record Found Upon searching '" + search_element_1 + "'");
			} else {
				isEmpltyCheck(search_element_1);
			}
		} catch (Exception e) {
			System.out.println("Element not found. Executing isEmpltyCheck...");
			int flag_value = isEmpltyCheck(search_element_1);
			if (flag_value == 0) {
				test.log(Status.PASS, "Upon Searching '" + search_element_1
						+ "' No Element observed in the table it returns text 'No Record Found'");
			}

			else if (flag_value == 1) {
				test.log(Status.PASS, "Upon Searching '" + search_element_1
						+ "' Element observed in the table which is printed on console");
			}

			else {
				test.log(Status.PASS, "Upon Searching '" + search_element_1
						+ "' Unexpected warning appear perform the same operation");
			}
		}
		Thread.sleep(3000);
		searchfield.clear();

		// ----------------------------------------Search#2
		searchfield.sendKeys(search_element_2);
		Thread.sleep(3000);
		try {
			WebElement no_element = driver.findElement(By.xpath(
					"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/div"));
			String textt = no_element.getText();
			if (textt.equals("No Record Found")) {
				test.log(Status.PASS, "No Record Found Upon searching '" + search_element_2 + "'");
			} else {
				isEmpltyCheck(search_element_2);
			}
		} catch (Exception e) {
			System.out.println("Element not found. Executing isEmpltyCheck...");
			int flag_value = isEmpltyCheck(search_element_2);
			if (flag_value == 0) {
				test.log(Status.PASS, "Upon Searching '" + search_element_2
						+ "' No Element observed in the table it returns text 'No Record Found'");
			}

			else if (flag_value == 1) {
				test.log(Status.PASS, "Upon Searching '" + search_element_2
						+ "' Element observed in the table which is printed on console");
			} else {
				test.log(Status.FAIL, "Upon Searching '" + search_element_2
						+ "' Unexpected warning appear perform the same operation");
			}
		}
		Thread.sleep(3000);
		searchfield.clear();

		// ----------------------------------------Search#3
		searchfield.sendKeys(search_element_3);
		Thread.sleep(3000);
		try {
			WebElement no_element = driver.findElement(By.xpath(
					"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/div"));
			String textt = no_element.getText();
			if (textt.equals("No Record Found")) {
				test.log(Status.PASS, "No Record Found Upon searching '" + search_element_3 + "'");
				searchfield.clear();
				Thread.sleep(3000);

			} else {
				isEmpltyCheck(search_element_3);
			}
		} catch (Exception e) {
			System.out.println("Element not found. Executing isEmpltyCheck...");
			int flag_value = isEmpltyCheck(search_element_3);
			if (flag_value == 0) {
				test.log(Status.PASS, "Upon Searching '" + search_element_3
						+ "' No Element observed in the table it returns text 'No Record Found'");
			}

			else if (flag_value == 1) {
				test.log(Status.PASS, "Upon Searching '" + search_element_3
						+ "' Element observed in the table which is printed on console");
			} else {
				test.log(Status.FAIL, "Upon Searching '" + search_element_3
						+ "' Unexpected warning appear perform the same operation manually");

			}
			Thread.sleep(3000);
			searchfield.clear();
		}
	}

// Is Empty Check for user management
	public Integer isEmpltyCheck(String Element) {
		int flag;
		System.out.println("------------------  " + Element + "  ------------------");
		List<WebElement> table_element = driver.findElements(By.xpath(
				"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/table"));
		try {
			WebElement no_element = driver.findElement(By.xpath(
					"/html/body/app-root/app-main/mat-sidenav-container/mat-sidenav-content/div[2]/app-users-management/app-table/div/div[3]/div[2]/div"));
			String textt = no_element.getText();
			if (textt.equals("No Record Found")) {
				flag = 0;
				return flag;
			}
		} catch (Exception e) {
			System.out.println("Executing Entity Check");
			if (!table_element.isEmpty()) {
				System.out.println("Entries found with search result: ");
				for (int i = 0; i < table_element.size(); i++) {
					WebElement element = table_element.get(i);
					System.out.println("--------------------------------------- ");
					System.out.println(element.getText());
					System.out.println("--------------------------------------- ");
				}

				flag = 1;
			} else {
				flag = 0;
			}
			return flag;

		}
		return null;
	}

	@AfterTest
	public void close_setup() throws InterruptedException {
		Thread.sleep(3000);
//		driver.close();
		extend.flush();

	}

}
