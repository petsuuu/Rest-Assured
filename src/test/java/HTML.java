import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

public class HTML {

    @BeforeClass
    public static void setup() {
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }

    @Test
    public void deveFazerBuscasComHTML() {
        RestAssured.given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body("html.body.div.table.tbody.tr.size()", Matchers.is(3))
                .body("html.body.div.table.tbody.tr[1].td[2]", Matchers.is("25"))
                .appendRootPath("html.body.div.table.tbody")
                ///iterdor pega  alinha e tranforma em sting
                .body("tr.find{it.toString().startsWith('2')}.td[1]", Matchers.is("Maria Joaquina"))

        ;


    }
}
