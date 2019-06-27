import io.restassured.RestAssured;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class SchemaTest {

    @BeforeClass
    public static void setup() {
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }


    @Test
    public void deveValidarEsquemaXML() {
        RestAssured.given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
        ;
    }

    //esperaço que uma exceção seja experada, verificado na primeira linha do erro qual exceção
    @Test(expected = SAXParseException.class)
    public void deveValidarEsquemaXMLInvalido() {
        RestAssured.given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/invalidUsersXML")
                .then()
                .log().all()
                .statusCode(200)

                //Verifica compara cortpo xml com corpo users.xsd
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xds"))

        ;
    }

    @Test
    public void deveValidarEsquemaJson() {
        RestAssured.given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(200)

                //Verifica compara cortpo xml com corpo users.xsd
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
        ;
    }

}
