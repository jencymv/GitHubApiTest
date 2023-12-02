package com.tests.sep2023;

import static org.hamcrest.Matchers.lessThan;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.models.requests.PatchRepoPOJO;
import com.models.requests.PostAllRepoPOJO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Githib {
	private static String mytoken="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";
	
	@BeforeTest
	public void setUp() {
		RestAssured.baseURI="https://api.github.com/";
	}
	
	@Test
	
	public void GetASingleRepo()
	{
		
		//String mytoken=
		Header header=new Header("Authorization", "Bearer " + mytoken);
		Response response = RestAssured.given()
				.header(header).
				when().get("repos/jencymv/SeleniumFramework");

	
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode,200);
		
		JsonPath jsonObject =response.body().jsonPath();
		
		String name =jsonObject.get("full_name");
		Assert.assertEquals(name,"jencymv/SeleniumFramework");
		
		String branch = jsonObject.get("default_branch");
		Assert.assertEquals(branch,"main");

		String contentType = response.contentType();
		Assert.assertEquals(contentType, "application/json; charset=utf-8");
	}

	@Test
	public void GetASingleRepoNonExisting()
	{
		
//		String mytoken="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";
		Header header=new Header("Authorization", "Bearer " + mytoken);
		Response response = RestAssured.given()
				.header(header).
				when().get("repos/jencymv/Selenium");
		
		
		JsonPath jsonObject =response.body().jsonPath();
		String mess = jsonObject.get("messsage");

		Assert.assertEquals(mess, "Not Found");
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode,404);
	
	}
	@Test 
	
	public void getallrepo()
	{
RequestSpecification request = RestAssured.given();
		
				Header header=new Header("Authorization", "Bearer " + mytoken);
				Response response = RestAssured.given()
				.header(header).
				when().get("/user/repos");
		
		
		Assert.assertEquals(response.getStatusCode(),200);
		Assert.assertEquals(response.contentType(), "application/json; charset=utf-8");
		
		
		JsonPath jsonObject =response.body().jsonPath();
		//List<Int> count = jsonObject.get("findAll(it.id)");
		//System.out.println(count.size());
	}
	@Test
	
	public void CreateRepo()
	{
		PostAllRepoPOJO post = new PostAllRepoPOJO();
		post.setName("Hello2");
		post.setHomepage("https://github.com");
		post.setDescription("This is your first repo!");
		post.setPrivate("Public");
				
		String mytoken="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";


		Response response = RestAssured.given().
				auth().
				oauth2(mytoken).body(post).
				when().
				post("/user/repos");

	
		response.then()
		.statusCode(201)
		.time(lessThan(5000L));
			
		
		Assert.assertEquals(response.getStatusCode(),201);
		
		JsonPath jsonObject =response.body().jsonPath();
	
		Assert.assertEquals(jsonObject.get("name"),"Hello2");
		Assert.assertEquals(jsonObject.get("owner.login"),"jencymv");
		Assert.assertEquals(jsonObject.get("owner.type"),"User");
		
	}
	
	
	@Test
	
	public void CreateRepoExistingName()
	{
		PostAllRepoPOJO post = new PostAllRepoPOJO();
		post.setName("CucumberPOM");
		post.setHomepage("https://github.com");
		post.setDescription("This is your first repo!");
		post.setPrivate("Public");
		
		
		String mytoken="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";
		Header header=new Header("Authorization", "Bearer" + mytoken);

		Response response = RestAssured.given().
				auth().
				oauth2(mytoken).
				body(post).
				when().
				post("user/repos");

	
		response.then()
		.statusCode(422)
		.time(lessThan(5000L));
	
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode,422);
		
		JsonPath jsonObject =response.body().jsonPath();

	String error_msg =jsonObject.get("errors[0].message");
		Assert.assertEquals(error_msg,"name already exists on this account");
	
	}
	
	@Test
	
	public void UpdateRepo()
	{
		
		PatchRepoPOJO patch = new PatchRepoPOJO();
		patch.setName("trymodified");
		patch.setDescription("This is your modified repo!");
		patch.setPrivate("False");
		

		
		Response response = RestAssured.given().
				auth().
				oauth2(mytoken).
				body(patch).
				when().
				patch("/repos/jencymv/trya");
		
	
		
		response.then()
		.statusCode(200)
		.time(lessThan(4000L));
		
		JsonPath jsonObject =response.body().jsonPath();
		String name =jsonObject.get("name");
		Assert.assertEquals(name,"trymodified");
	}
	@Test
	
	public void DeleteRepo()
	{
		
		Response res = RestAssured.given().
				auth().
				oauth2(mytoken).
				when().delete("repos/jencymv/Hello");
		
		JsonPath jsonObject =res.body().jsonPath();
		
		res.then()
		.statusCode(204)
		.time(lessThan(4000L));
		
		Assert.assertEquals(res.getStatusCode(),204);
		System.out.println("Body"+res.body());
	}
	
	@Test
	
	public void DeleteRepoNonexisting()
	{
		
		Response res = RestAssured.given().
				auth().
				oauth2(mytoken).
				when().delete("repos/jencymv/Hello2");
	
		JsonPath jsonObject =res.body().jsonPath();
		res.then()
		.statusCode(404)
		.time(lessThan(4000L));
	}
}
