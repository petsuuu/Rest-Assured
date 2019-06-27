import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AuthTest {

    @BeforeClass
    public static void setup() {
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }


    @Test
    public void deveAcessarSWAPI() {
        RestAssured.given()
                .log().all()
                .when()
                .get("https://swapi.co/api/people/1")
                .then()
                .statusCode(200)
                .body("name", Matchers.is("Luke Skywalker"))
        ;
    }

    @Test
    public void deveObterClima() {

        //http://api.openweathermap.org/data/2.5/weather?q=Fortaleza,BR&appid=6795648875a96dc08f6644cd9deb09d8&units=metric

        RestAssured.given()
                .log().all()
                .queryParam("q", "Fortaleza,BR")
                .queryParam("appid", "6795648875a96dc08f6644cd9deb09d8")
                .queryParam("units", "metric")
                .when()
                .get("http://api.openweathermap.org/data/2.5/weather")
                .then()
                .statusCode(200)
                .body("name", Matchers.is("Fortaleza"))
                .body("coord.lon", Matchers.is(-38.52f))
                .body("main.temp", Matchers.greaterThan((25f)))
        ;
    }

    @Test
    public void naoDeveAcessarSemSenha() {
        RestAssured.given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/basicauth\n")
                .then()
                .log().all()
                .statusCode(401)

        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica() {
        RestAssured.given()
                .log().all()
                .when()
                .get("http://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"))

        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica2() {
        RestAssured.given()
                .log().all()
                .auth().basic("admin", "senha")
                .when()
                .get("http://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"))

        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge() {
        RestAssured.given()
                .log().all()
                .auth().preemptive().basic("admin", "senha")
                .when()
                .get("http://restapi.wcaquino.me/basicauth2")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"))

        ;
    }


    //http://barrigarest.wcaquino.me/contas
    @Test
    public void deveFazerAutenticacaoComTokenJWT() {
        Map <String, String> login = new HashMap <String, String>();
        login.put("email", "peterson.cardoso@me.com");
        login.put("senha", "123456");


        //Login na api
        //Receber o token
        String token = RestAssured.given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("http://barrigarest.wcaquino.me/signin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token");

        //Obter as contas
        RestAssured.given()
                .log().all()
                .header("Authorization", "JWT " + token)
                .when()
                .get("http://barrigarest.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", Matchers.hasItems("Conta de Teste"))
        ;
    }

    @Test
    public void deveAcessarAplicacaoWeb() {
        //login
        String cookie =
                RestAssured.given()
                        .log().all()
                        .formParam("email", "peterson.cardoso@me.com")
                        .formParam("senha", "123456")
                        .contentType(ContentType.URLENC.withCharset("UTF-8"))
                        .when()
                        .post("http://seubarriga.wcaquino.me/logar")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().header("set-cookie");

        //pegar o elemento entre tal coisas
        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println("cookie ====== " + cookie);


        //Obter conta
        String body =
                RestAssured.given()
                        .log().all()
                        .cookie("connect.sid", cookie)
                        .when()
                        .get("http://seubarriga.wcaquino.me/contas")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .body("html.body.table.tbody.tr[0].td[0]", Matchers.is("Conta de Teste"))
                        .extract().body().asString();


        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(body);
    }

}


