import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/1")
                .then()
                .statusCode(200)
                .body("id", Matchers.is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18))
        ;

    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormar() {
        Response response = RestAssured.request((Method.GET), "http://restapi.wcaquino.me/users/1");


        //pagr do json com path
        response.path("id");

        //Vaidar Campo
        Assert.assertEquals((1), response.path("id"));
        Assert.assertEquals((1), response.path("%s", "id"));

        //jsonpath
        JsonPath jsonPath = new JsonPath((response.asString()));
        Assert.assertEquals(1, jsonPath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);

    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("name", containsString("Joaquina"))
                .body("endereco.rua", Matchers.is("Rua dos bobos"))
        ;
    }

    @Test
    public void deveVerificarLista() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                //verifica quantas lista tem
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItem("Zezinho"))
                .body("filhos.name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void deveRetornarErroUsuarioInexistente() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"))
        ;
    }

    @Test
    public void deveVerificarlistaRaiz() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("", hasSize(3))
                .body("name", hasItems("João da Silva", "João da Silva", "Ana Júlia"))
                .body("age[1]", is(25))
                .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
                .body("salary", contains(1234.5678f, 2500, null))

        ;

    }

    @Test
    public void devofazerVerificacoesAvancadas() {
        given()
                .when()
                .proxy("spobrproxy.serasa.intranet", 3128)
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))

                //findAll - traz todos que ele encontrar
                //find - traz somente um registro
                //Olha docuemntação Groovy
                //it = interação das tag


                //verificar quantos usarios existe ate 25 anos
                .body("age.findAll{it <= 25}.size()", is(2))
                //verificar quantos usarios existe ate 25 anos e maior que 20 anos
                .body("age.findAll{it <= 25 && it>20}.size()", is(1))
                // verificar quantos usarios existe ate 25 anos e maior que 20 anos e compara o nome
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                //Verifica o primeiro que tenha menso de 25 anos e verifca  o nome
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                //verifica a lista d ebaixo pra cima com -1
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))

                //Verifica um registro
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))

                //Verifica quantos names contem na responsa o n
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                //verificar os nomes que possuem mais que 10 caracteres
                .body("findAll{it.name.length()>10}.name", hasItems("Maria Joaquina", "João da Silva"))
                //colocar nome me maisculo
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                //Buscar todos os nomes que começa com Maria tranforme em maisculo
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                //Verifica que o item e também a coleeçãoq ue retornou possui o tamanho = a 1
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
                //Multiplica a idade
                .body("age.collect{it *2}", hasItems(60, 50, 40))
                //maior id na lista
                .body("id.max()", is(3))
                //menor salario da lista
                .body("salary.min()", is(1234.5678f))
                //somar os salarios, fazendo filtro para nao buscar nulos {it != null} e para nao quebrar casa decimais coloca uma marge de erro com closeTo
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
                //Mesma forma que do de cima que que coloco um min e um max do resultado esperado
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))

        ;
    }

    @Test
    public void devoUnitJsonPathComJAVA() {
        ArrayList <String> names =

                given()
                        .when()
                        .get("http://restapi.wcaquino.me/users")
                        .then()
                        .statusCode(200)
                        .extract().path("name.findAll{it.startsWith('Maria')}");
        //Verifica que so existe um nome Maria na array obtida
        Assert.assertEquals(1, names.size());
        // nao importa maiusculo ou minusculo
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
        //Usando assertEquals
        Assert.assertEquals(names.get(0).toUpperCase(), "mArIa Joaquina".toUpperCase());

    }
}