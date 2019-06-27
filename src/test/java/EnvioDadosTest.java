import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnvioDadosTest {

    @BeforeClass
    public static void setup() {
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }

    @Test
    public void deveEnviarValorViaQuery() {
        RestAssured.given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/v2/users?format=xml")
                .then()
                .log().all()
                .statusCode(200)

                //Verifica se o formato é XML
                .contentType(ContentType.XML)

        ;
    }

    @Test
    public void deveEnviarValorViaQueryViaParam() {
        RestAssured.given()
                .log().all()

                // Passa o Parametro
                .queryParam("format", "xml")
                .queryParam("outra", "coisa")
                .when()
                .get("http://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)

                //Verifica se o formato é XML
                .contentType(ContentType.XML)
                .contentType(Matchers.containsString("utf-8"))

        ;
    }

    @Test
    public void deveEnviarValorViaHeader() {

        RestAssured.given()
                .log().all()
                .accept(ContentType.JSON)
                .when()
                .get("http://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)

                //Verifica se o formato é XML
                .contentType(ContentType.JSON)

        ;
    }
}
