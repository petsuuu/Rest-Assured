import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

//import nao precisa mais colocar restAssured antes

public class OlaMundoTest {

    @Test
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        assertTrue(response.getBody().asString().equals(("Ola Mundo!")));
        assertTrue(response.statusCode() == 200);
        assertTrue("O Status code deveria ser 200", response.statusCode() == 200);
        assertEquals(200, response.statusCode());


        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        RestAssured.given()//Pré Condições
                .when()//Ação
                .get("http://restapi.wcaquino.me/ola")
                .then()//Assertivas
                //.assertThat()
                .statusCode(200);

    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        assertThat("Maria", is("Maria"));
        assertThat(128, is(128));
        //é um inteiro

        assertThat(128, Matchers.isA(Integer.class));
        // é um double
        assertThat(128d, isA(Double.class));
        //maior que
        assertThat(128d, greaterThan(120d));
        //menor que
        assertThat(128d, lessThan(130d));


        List <Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        //Verifica quantdade 5 elementos na lista
        assertThat(impares, hasSize(5));
        //Verifica elementos em ordem
        assertThat(impares, contains(1, 3, 5, 7, 9));
        //não importando a  ordem do elemntos
        assertThat(impares, Matchers.containsInAnyOrder(7, 9, 5, 3, 1));
        //Verifica um elemnto na lista
        assertThat(impares, hasItem(1));
        //Verifica mais de um elemnto na lista
        assertThat(impares, hasItems(1, 5));


        //verifica se maria é diferente que joao
        assertThat("Maria", is(not("joao")));
        assertThat("Maria", not("joao"));
        //Verifica se é um ou outro
        assertThat("Maria", anyOf(is("Maria"), is("joaquina")));
        //Verifica se existe o elmento dentro do texto
        assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));


    }

    @Test
    public void devoValidarBody() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200)
                .body(Matchers.is("Ola Mundo!"))
                .body(containsString("Mundo"))
                .body(is(not(nullValue())))
        ;
    }


}



